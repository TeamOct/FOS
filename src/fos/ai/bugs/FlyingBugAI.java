package fos.ai.bugs;

import arc.math.Mathf;
import fos.gen.Bugc;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.world.meta.BlockFlag;

public class FlyingBugAI extends AIController {
    private Bugc bug;

    @Override
    public void updateUnit() {
        bug = (Bugc) unit;

        if (bug.isFollowed()) {
            int followers = Units.count(unit.x, unit.y, 240f, u -> u instanceof Bugc);

            if (followers >= 10 + Mathf.floor(Vars.state.wave / 10f)) {
                bug.invading(true);
            }
        } else {
            //check for bug swarms nearby
            bug.following(Units.closest(unit.team, unit.x, unit.y, u ->
                    (u instanceof Bugc b && b.isFollowed() && !(b.invading()))
            ));

            //become a swarm leader if none exist, or if this bug is a boss
            if (bug.following() == null || bug.isBoss()) bug.isFollowed(true);
        }

        super.updateUnit();
    }

    @Override
    public void updateMovement() {
        if (bug.invading()) {
            target = findTarget(unit.x, unit.y, 1600f, unit.type.targetAir, true);
        } else if (bug.following() != null) {
            //if already close enough to another bug when idle, stand still
            Unit nearest = Units.closest(unit.team, unit.x, unit.y, u -> (u instanceof Bugc) && u != this.unit);
            if (Mathf.within(unit.x, unit.y, nearest.x, nearest.y, 10f) && !bug.invading()) return;

            bug.invading(bug.following() instanceof Bugc bf && bf.invading());
            target = bug.following();
        } else {
            vec.set(unit.x + 36f, unit.y + 24f);
            moveTo(vec, 3f);

            //nullpointer is happening if target isn't declared
            target = unit;
        }

        if (unit.isShooting && unit.range() == 0.01f) {
            vec.set(target).sub(unit);
            vec.setLength(unit.speed() * 5f);
            unit.moveAt(vec);
        } else if (unit.type.circleTarget && target.team() != unit.team) {
            circleAttack(120f);
        } else {
            moveTo(target, unit.range() == 0.01f ? 24f : unit.range() * 0.8f, 20f, true, null);
        }
        faceTarget();
    }

    @Override
    public boolean shouldShoot() {
        return unit.type.range != 0.01f || unit.mounts[0].reload <= 0f;
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

        return Units.closestTarget(unit.team, x, y, range, u -> false, b -> true);
    }
}
