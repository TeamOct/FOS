package fos.type.blocks.production;

import arc.Core;
import arc.math.Mathf;
import fos.type.blocks.storage.DetectorCoreBlock;
import mindustry.entities.TargetPriority;
import mindustry.game.Team;
import mindustry.logic.Ranged;
import mindustry.world.*;
import mindustry.world.meta.BlockGroup;

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

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return nearestDetector(team, tile.worldx(), tile.worldy()) != null;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Tile tile = world.tile(x, y);
        var detector = nearestDetector(player.team(), x*8, y*8);
        if (tile == null) return;

        if (detector == null) {
            drawPlaceText(Core.bundle.get("bar.detectorreq"), x, y, valid);
        }
    }

    protected Ranged nearestDetector(Team team, float wx, float wy) {
        return (Ranged)indexer.findTile(team, wx, wy, 999f, b -> (b.block instanceof OreDetector || b.block instanceof DetectorCoreBlock)
            && Mathf.within(wx, wy, b.x, b.y, ((Ranged)b).range()));
    }
}
