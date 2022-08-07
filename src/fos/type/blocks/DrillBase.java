package fos.type.blocks;

import arc.Core;
import arc.math.Mathf;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.indexer;

public class DrillBase extends Block {
    public DrillBase(String name) {
        super(name);
        solid = true;
        destructible = true;
        breakable = true;
        group = BlockGroup.drills;
        priority = TargetPriority.transport;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        float x = Core.input.mouseWorldX(), y = Core.input.mouseWorldY();
        Building build = indexer.findTile(team, x, y, 120f, b -> b.block() instanceof OreDetector);
        return build != null && Mathf.within(x, y, build.x, build.y, 120f);
    }
}
