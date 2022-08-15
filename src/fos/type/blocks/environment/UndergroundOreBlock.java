package fos.type.blocks.environment;

import arc.graphics.g2d.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

public class UndergroundOreBlock extends OverlayFloor {
    public UndergroundOreBlock(String name) {
        super(name);
        //hide an ore from the minimap
        useColor = false;
        playerUnmineable = true;
    }

    //an item drop's icon should be the in-editor icon
    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{
            this.itemDrop.uiIcon
        };
    }

    //can't see in-game
    @Override
    public void drawBase(Tile tile) {}
}
