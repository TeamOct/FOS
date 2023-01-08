package fos.type.blocks.production;

import arc.math.Mathf;
import fos.type.blocks.storage.DetectorCoreBlock;
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

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        Building build = indexer.findTile(player.team(), tile.worldx(), tile.worldy(), 999f, b ->
            b instanceof OreDetector.OreDetectorBuild || b instanceof DetectorCoreBlock.LuminaCoreBuild);
        if (build instanceof OreDetector.OreDetectorBuild) {
            return Mathf.within(tile.worldx(), tile.worldy(), build.x, build.y, ((OreDetector.OreDetectorBuild) build).range());
        } else {
            return build != null && Mathf.within(tile.worldx(), tile.worldy(), build.x, build.y, build.block().name.equals("fos-core-colony") ? 0 : 25f * 8f);
        }
    }
}
