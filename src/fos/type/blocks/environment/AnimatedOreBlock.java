package fos.type.blocks.environment;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.OreBlock;

public class AnimatedOreBlock extends OreBlock {
    public int frames = 1;
    public float frameTime = 5f;
    protected TextureRegion[][] regions;

    public AnimatedOreBlock(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();

        if (frames > 0) {
            regions = new TextureRegion[variants][frames];
            for (int i = 0; i < variants; i++) {
                for (int j = 0; j < frames; j++) {
                    regions[i][j] = Core.atlas.find(name + "-" + (i+1) + "-" + (j+1));
                }

                variantRegions[i] = regions[i][0];
            }
        } else {
            throw new IllegalArgumentException("Animated ores must have 1 or more frames!");
        }
    }

    @Override
    public void drawBase(Tile tile) {
        Events.run(EventType.Trigger.draw, () -> {
            int variant = Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1));
            int frame = (int)(Time.globalTime / frameTime + (tile.x + tile.y) * 2) % frames;
            Draw.draw(Layer.blockProp, () -> Draw.rect(regions[variant][frame], tile.worldx(), tile.worldy()));
        });
        Events.run(EventType.Trigger.update, () -> {
            //this method isn't called for overlays in the renderer so call it here instead
            if (Vars.renderer.lights.enabled() && tile.block() == Blocks.air) {
                drawEnvironmentLight(tile);
            }
        });
    }

    @Override
    public void drawEnvironmentLight(Tile tile) {
        int variant = Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1));

        Drawf.light(tile.worldx(), tile.worldy(), regions[variant][0], lightColor, lightColor.a);
    }
}
