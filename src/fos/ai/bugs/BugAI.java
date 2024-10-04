package fos.ai.bugs;

import arc.Events;
import arc.math.Mathf;
import fos.ai.FOSPathfindAI;
import fos.core.*;
import fos.gen.*;
import fos.type.blocks.units.BugSpawn;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.tilesize;

public class BugAI extends AIController implements FOSPathfindAI {
    private Bugc bug;

    @Override
    public void updateUnit() {
        if (bug == null) {
            bug = (Bugc) unit;
        }

        if (bug.isFollowed()) {
            int followers = Units.count(unit.x, unit.y, 15f * tilesize, u -> u instanceof Bugc b && b.following() == bug);

            if (followers >= Math.min(Mathf.floor(4 + evo() * 30), 49)) {
                bug.invading(true);
                Events.fire(new FOSEventTypes.InsectInvasionEvent());
            }
        } else {
            // check for bug swarms nearby
            bug.following(Units.closest(unit.team, unit.x, unit.y, 15f * tilesize, u -> u instanceof Bugc b && b.isFollowed()));

            // become a swarm leader if none exist, or if this bug is a boss
            if (bug.following() == null || bug.isBoss()) bug.isFollowed(true);
        }

        super.updateUnit();
    }

    @Override
    public void updateMovement() {
        Tile tile = unit.tileOn();
        Tile targetTile = tile;
        target = target(unit.x, unit.y, 25f * tilesize, false, true);

        if ((bug.invading()) || !hasNests()) {
            if (target != null) {
                if (unit.within(target, 64f)) {
                    if (unit instanceof Burrowc b && b.burrowed() && !b.isBurrowing()) {
                        b.burrow();
                    }
                } else if (!unit.within(target, 128f)) {
                    if (unit instanceof Burrowc b && !b.burrowed() && !b.isBurrowing()) {
                        b.burrow();
                    }
                }

                targetTile = pathfind(unit);
            }
        } else if (bug.following() != null) {
            var f = bug.following();
            bug.invading(f instanceof Bugc bf && bf.invading());

            moveTo(f, 12f + f.type.hitSize, 0, true, null);
            return;
        }

/*
        if (!bug.invading() && !bug.idle()) {
            // find a random point to walk at
            boolean foundTile = false;
            while (!foundTile) {
                int x = Mathf.random(-20, 20);
                int y = Mathf.random(-20, 20);
                Tile t = Vars.world.tileWorld(unit.x + x, unit.y + y);
                if (t != null && !t.legSolid()) {
                    targetTile = pathfind(unit);
                    foundTile = true;
                    bug.idle(true);
                }
            }
        }
*/

        if (targetTile == tile) return;

        if (!unit.inRange(target))
            unit.movePref(vec.trns(unit.angleTo(targetTile.worldx(), targetTile.worldy()), unit.speed()));
        unit.lookAt(unit.angleTo(target));
    }

    @Override
    public Teamc target(float x, float y, float range, boolean air, boolean ground) {
        var unitTarget = Units.closestTarget(unit.team, x, y, unit.range(), Unit::isValid, t -> false);
        if (unitTarget != null) return unitTarget;

        for (BlockFlag flag : unit.type.targetFlags) {
            Teamc target = null;
            if (flag != null) {
                target = targetFlag(x, y, flag, true);
            }

            if (target != null) return target;
        }

        Teamc result = Units.closestTarget(unit.team, x, y, range, u -> false, b -> true);

        return checkTarget(result, x, y, range) ? unit.closestEnemyCore() : result;
    }

    @Override
    public boolean keepState() {
        return false; // TODO: debug reasons
    }

    private float evo() {
        return FOSVars.evoController.getTotalEvo();
    }

    private boolean hasNests() {
        return Groups.build.contains(b -> b instanceof BugSpawn.BugSpawnBuild && b.enabled());
    }
}
