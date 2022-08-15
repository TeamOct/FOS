package fos.type.blocks;

import arc.math.Mathf;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class DrillBase extends Block {
    public DrillBase(String name) {
        super(name);
        solid = true;
        destructible = true;
        breakable = true;
        group = BlockGroup.drills;
        priority = TargetPriority.transport;
    }

    //TODO now fixed, but still incompatible with cores
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        Building build = indexer.findTile(player.team(), tile.worldx(), tile.worldy(), 240f, b -> b instanceof OreDetector.OreDetectorBuild);
        return build != null && Mathf.within(tile.worldx(), tile.worldy(), build.x, build.y, 120f);
    }
}
