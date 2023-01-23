package fos.ai;

import arc.math.Mathf;
import fos.type.units.BugUnit;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;

public class BugAI extends AIController implements ITargetable {
    public BugUnit bug;
    @Override
    public void updateUnit() {
        bug = (BugUnit) unit;

        if (bug.isFollowed) {
            int followers = Units.count(unit.x, unit.y, 240f, u -> u instanceof BugUnit);

            if (followers >= 10 + Mathf.floor(Vars.state.wave / 10f)) {
                bug.invading = true;
            }
        } else {
            //check for bug swarms nearby
            bug.following = Units.closest(unit.team, unit.x, unit.y, u ->
                u instanceof BugUnit b && b.isFollowed);

            //become a swarm leader if none exist, or if this bug is a boss
            if (bug.following == null) bug.isFollowed = true;
        }

        super.updateUnit();
    }

    @Override
    public void updateMovement() {
        Tile tile = unit.tileOn();
        Tile targetTile = tile;

        if (bug.invading) {
            target = findTarget(unit.x, unit.y, 1600f, false, true);

            if (target != null) {
                if (unit.within(target, 32f)) {
                    circleAttack(60f);
                    return;
                } else {
                    targetTile = pathfindTarget(target, unit);
                }
            }
        } else if (bug.following != null) {
            //if already close enough to a swarm leader, stand still
            if (Mathf.within(unit.x, unit.y, bug.following.x, bug.following.y, 32f)) return;

            bug.invading = ((BugUnit) bug.following).invading;

            targetTile = pathfindTarget(bug.following, unit);
        }

        if (targetTile == tile) return;
        unit.movePref(vec.trns(unit.angleTo(targetTile.worldx(), targetTile.worldy()), unit.speed()));
        faceTarget();
    }

    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground) {
        Teamc result = findMainTarget(x, y, range, air, ground);

        return checkTarget(result, x, y, range) ? Units.closestEnemy(unit.team, unit.x, unit.y, 800f, Unitc::isPlayer) : result;
    }

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground){
        for(BlockFlag flag : unit.type.targetFlags) {
            Teamc target;
            if (flag != null) {
                target = targetFlag(x, y, flag, true);
            } else {
                target = target(x, y, range, air, ground);
            }

            if (target != null) return target;
        }

        return targetFlag(x, y, null, true);
    }
}
