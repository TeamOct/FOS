package fos.type.blocks.environment;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Strings;
import fos.core.FOSVars;
import fos.graphics.*;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.graphics.Drawf;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.OreBlock;

public class AnimatedOreBlock extends OreBlock {
    public int frames = 1;
    protected TextureRegion[] regions;

    // TODO make base shader for animated ore block
    public Shader shader;

    public AnimatedOreBlock(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();

        if (frames > 0) {
            regions = new TextureRegion[variants];
            for (int i = 0; i < variants; i++) {
                // TODO make file searching better or integrate similar everywhere
                ShaderTextureRegion str = new ShaderTextureRegion(shader, new Texture(FOSVars.internalTree.child(
                            Strings.format("sprites/blocks/environment/@/@-@.png", name, name, i+1))), (s, o) -> {}, 0);
                regions[i] = str;

                variantRegions[i] = regions[i];
            }
        } else {
            throw new IllegalArgumentException("Animated ores must have 1 or more variants!");
        }
    }

    //overridden to prevent crashes. FIXME: actual regions are fucked up
    @Override
    public TextureRegion[] editorVariantRegions() {
        if (editorVariantRegions == null) {
            variantRegions();
            editorVariantRegions = new TextureRegion[variantRegions.length];
            for (int i = 1; i <= variantRegions.length; i++) {
                editorVariantRegions[i-1] = Core.atlas.find(name + "-" + i + "-2");
            }
        }
        return editorVariantRegions;
    }

    @Override
    public void drawBase(Tile tile) {

    }

    @Override
    public void renderUpdate(UpdateRenderState state) {
        new DrawRequest(state){
            @Override
            public void draw() {
                Tile tile = state.tile;
                int variant = Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1));
                if (shader instanceof FOSShaders.AnimatedFloorShader afs) {
                    afs.setX(tile.x);
                    afs.setY(tile.y);
                }
                Draw.rect(regions[variant], tile.worldx(), tile.worldy());

                if (Vars.renderer.lights.enabled()) {
                    float scl = regions[variant].scale;
                    regions[variant].scale = 1.5f;
                    Drawf.light(tile.worldx(), tile.worldy(), regions[variant], Color.white, 1f);
                    regions[variant].scale = scl;
                }
            }
        };
    }

    // TODO optimize
    @Override
    public boolean updateRender(Tile tile) {
        return tile.block() == Blocks.air;
    }

    public abstract static class DrawRequest {
        public static Seq<DrawRequest> requests = new Seq<>();
        static {

            Events.run(EventType.Trigger.postDraw, () -> requests.clear());
            Events.run(EventType.Trigger.draw, () -> requests.each(DrawRequest::draw));
        }

        public UpdateRenderState state;

        public DrawRequest(UpdateRenderState state) {
            this.state = state;
            requests.add(this);
        }

        public abstract void draw() ;
    }
}
