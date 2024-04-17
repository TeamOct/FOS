package fos.ai;

import arc.math.Mathf;
import fos.core.FOSVars;
import fos.gen.Bugc;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.tilesize;

public class BugAI extends AIController implements TargetableAI {
    private Bugc bug;

    @Override
    public void updateUnit() {
        if (bug == null) {
            bug = (Bugc) unit;
        }

        if (bug.isFollowed()) {
            int followers = Units.count(unit.x, unit.y, 15f * tilesize, u -> u instanceof Bugc b && b.following() == bug);

            if (followers >= Math.max(Mathf.floor(4 + evo() * 30), 49)) {
                bug.invading(true);
            }
        } else {
            //check for bug swarms nearby
            bug.following(Units.closest(unit.team, unit.x, unit.y, 15f * tilesize, u -> u instanceof Bugc b && b.isFollowed()));

            //become a swarm leader if none exist, or if this bug is a boss
            if (bug.following() == null || bug.isBoss()) bug.isFollowed(true);
        }

        super.updateUnit();
    }

    @Override
    public void updateMovement() {
        // FIXME: bugs spinning around for no reason
        Tile tile = unit.tileOn();
        Tile targetTile = tile;

        if (bug.invading() && evo() >= 0.05f) {
            target = target(unit.x, unit.y, 25f * tilesize, false, true);

            if (target != null) {
                if (unit.within(target, bug.hitSize() * 1.5f)) {
                    vec.set(target).sub(unit);
                    vec.setLength(unit.speed());
                    unit.lookAt(vec);
                    unit.moveAt(vec);
                    return;
                } else {
                    targetTile = pathfindTarget(target, unit);
                }
            }
        } else if (bug.following() != null) {
            var f = bug.following();
            bug.invading(f instanceof Bugc bf && bf.invading());

            moveTo(f, 12f + f.type.hitSize, 7, true, null);
            return;
        }

        if (!bug.invading() && !bug.idle()) {
            //find a random point to walk at
            boolean foundTile = false;
            while (!foundTile) {
                int x = Mathf.random(-40, 40);
                int y = Mathf.random(-40, 40);
                Tile t = Vars.world.tileWorld(unit.x + x, unit.y + y);
                if (t != null && t.block() == Blocks.air) {
                    targetTile = pathfindTarget(vec.set(unit).add(x*8, y*8), unit);
                    foundTile = true;
                    bug.idle(true);
                }
            }
        }

        if (targetTile == tile) return;

        unit.movePref(vec.trns(unit.angleTo(targetTile.worldx(), targetTile.worldy()), unit.speed()));
        faceTarget();
    }

    @Override
    public Teamc target(float x, float y, float range, boolean air, boolean ground) {
        for(BlockFlag flag : unit.type.targetFlags) {
            Teamc target;
            if (flag != null) {
                target = targetFlag(x, y, flag, true);
            } else {
                target = target(x, y, range, air, ground);
            }

            if (target != null) return target;
        }

        Teamc result = Units.closestTarget(unit.team, x, y, range, u -> false, b -> true);

        return checkTarget(result, x, y, range) ? bug.closestEnemyCore() : result;
    }

    @Override
    public boolean keepState() {
        return true;
    }

    private float evo() {
        return FOSVars.evoController.getTotalEvo();
    }
}
