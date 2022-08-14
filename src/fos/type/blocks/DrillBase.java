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

    //TODO utterly broken
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        Building build = indexer.findTile(player.team(), tile.x, tile.y, 120f, b -> b.block() instanceof OreDetector);
        return build != null && Mathf.within(tile.x, tile.y, build.x, build.y, 120f);
    }
}
