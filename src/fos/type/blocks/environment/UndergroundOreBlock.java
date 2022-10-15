package fos.type.blocks.environment;

import mindustry.graphics.MultiPacker;
import mindustry.type.Item;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

public class UndergroundOreBlock extends OverlayFloor {
    /** Used instead of itemDrop! */
    public Item drop;

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

    //can't see in-game
    @Override
    public void drawBase(Tile tile) {}
}
