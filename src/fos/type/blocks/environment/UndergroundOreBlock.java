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

    //can't see in-game
    @Override
    public void drawBase(Tile tile) {}
}
