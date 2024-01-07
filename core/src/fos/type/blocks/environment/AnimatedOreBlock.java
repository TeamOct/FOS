package fos.type.blocks.environment;

import arc.Events;
import arc.graphics.Blending;
import arc.graphics.g2d.*;
import arc.graphics.gl.Shader;
import fos.graphics.cachelayers.AnimatedOreCacheLayer;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.graphics.Drawf;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.OreBlock;

public class AnimatedOreBlock extends OreBlock {
    protected TextureRegion[] regions;

    // TODO make base shader for animated ore block
    public Shader shader;

    public AnimatedOreBlock(String name, Shader shader) {
        super(name);
        cacheLayer = new AnimatedOreCacheLayer(shader);
    }

    @Override
    public void drawBase(Tile tile) {
        super.drawBase(tile);

        Events.run(EventType.Trigger.draw, () -> {
            if (tile != null && tile.overlay() == this && Vars.renderer.lights.enabled()) {
                drawEnvironmentLight(tile);
            }
        });
    }

    @Override
    public void drawEnvironmentLight(Tile tile) {
        // stolen from WTTF, thanks sh1p
        Draw.blend(Blending.additive);

        Drawf.light(tile.worldx(), tile.worldy(), lightRadius, lightColor, lightColor.a);
        Drawf.light(tile.worldx(), tile.worldy(), region, lightColor, lightColor.a);

        Draw.blend();
    }
}
