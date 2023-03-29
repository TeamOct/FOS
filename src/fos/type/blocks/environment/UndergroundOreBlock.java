package fos.type.blocks.environment;

import arc.graphics.g2d.Draw;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

public class UndergroundOreBlock extends OverlayFloor {
    /** Used instead of itemDrop! */
    public Item drop;

    /** Used by {@link fos.type.blocks.production.OreDetector} **/
    public boolean shouldDrawBase = false;

    public UndergroundOreBlock(String name) {
        super(name);
        //hide an ore from the minimap
        useColor = false;
        playerUnmineable = true;
        variants = 1;
    }

    @Override
    public void load() {
        super.load();

        //just in case somebody decides to declare itemDrop
        if (itemDrop != null) {
            drop = itemDrop;
            itemDrop = null;
        }
    }

    @Override
    public void drawBase(Tile tile) {
        if (shouldDrawBase) {
            float l = Draw.z();
            Draw.z(Layer.light);

            super.drawBase(tile);

            Draw.z(l);
        }
    }
}
