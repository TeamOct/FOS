package fos.type.blocks.environment;

import arc.Events;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.util.Strings;
import fos.core.FOSVars;
import fos.graphics.FOSShaders;
import fos.graphics.ShaderTextureRegion;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
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

    @Override
    public void drawBase(Tile tile) {
        Events.run(EventType.Trigger.draw, () -> {
            if (Vars.world.tile(tile.x, tile.y).overlay() != this) return;

            int variant = Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1));
            if (shader instanceof FOSShaders.AnimatedFloorShader afs) {
                afs.setX(tile.x);
                afs.setY(tile.y);
            }
            Draw.draw(Layer.blockUnder, () -> Draw.rect(regions[variant], tile.worldx(), tile.worldy()));
        });
        Events.run(EventType.Trigger.update, () -> {
            if (Vars.renderer.lights.enabled() && Vars.world.tile(tile.x, tile.y).overlay() == this && tile.block() == Blocks.air) {
                drawEnvironmentLight(tile);
            }
        });
    }

    @Override
    public void drawEnvironmentLight(Tile tile) {
        int variant = Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1));

        Drawf.light(tile.worldx(), tile.worldy(), regions[variant], lightColor, lightColor.a);
    }
}
