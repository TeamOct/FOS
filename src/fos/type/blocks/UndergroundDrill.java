package fos.type.blocks;

import arc.math.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;

public class UndergroundDrill extends Drill {
    public UndergroundDrill(String name){
        super(name);
        drillTime = 360f;
        buildType = UndergroundDrillBuild::new;
    }

    //placeable on drill bases only
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (isMultiblock()) {
            for(Tile other : tile.getLinkedTilesAs(this, tempTiles)) {
                Building block = other.build;
                return block != null && block.block() instanceof DrillBase && block.team == team;
            }
            return false;
        } else {
            Building block = tile.build;
            return block != null && block.block() instanceof DrillBase && block.team == team;
        }
    }

    public Item getOutput(Tile tile) {
        countOre(tile);

        //if nothing's under the drill, mine sand
        return returnItem != null ? returnItem : Items.sand;
    }

    public class UndergroundDrillBuild extends DrillBuild {
        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            dominantItem = getOutput(tile);
            if (dominantItem == Items.sand) dominantItems = (int)Mathf.sqr(size);
        }
    }
}
