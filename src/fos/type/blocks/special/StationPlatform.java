package fos.type.blocks.special;

import fos.content.FOSBlocks;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Env;

public class StationPlatform extends Block {
    public StationPlatform(String name) {
        super(name);
        breakable = destructible = true;
        envRequired = Env.space;
        buildType = StationPlatformBuild::new;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (isMultiblock()) {
            for (Tile other : tile.getLinkedTilesAs(this, tempTiles)){
                if (other.floor() != Blocks.air && other.floor() != Blocks.empty) return false;
            }
            return true;
        } else {
            return tile.floor() == Blocks.empty || tile.floor() == Blocks.space;
        }
    }

    public class StationPlatformBuild extends Building {
        @Override
        public void updateTile() {
            if (isMultiblock()) {
                for (Tile other : tile.getLinkedTilesAs(this.block(), tempTiles)){
                    other.setFloor(Blocks.metalFloor.asFloor());
                    other.setBlock(Blocks.air);
                }
            } else {
                Tile tile = this.tile();
                tile.setFloor(Blocks.metalFloor.asFloor());
                tile.setBlock(Blocks.air);
            }
        }
    }
}
