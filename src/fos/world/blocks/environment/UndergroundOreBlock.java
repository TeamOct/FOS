package fos.world.blocks.environment;

import arc.graphics.g2d.Draw;
import fos.world.blocks.production.OreDetector;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.OverlayFloor;

public class UndergroundOreBlock extends OverlayFloor {
    /** Used instead of itemDrop! */
    public Item drop;
    /** Self-explanatory. How strong of an ore detector is needed to find this. */
    public int depth = 1;

    /** Used by {@link OreDetector} **/
    public boolean shouldDrawBase = false;

    public UndergroundOreBlock(String name) {
        super(name);
        // can't place ores on shallow water without this.
        needsSurface = false;

        //hide an ore from the minimap
        useColor = false;
        playerUnmineable = true;
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
