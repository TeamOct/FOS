package fos.ai;

import mindustry.ai.Pathfinder;
import mindustry.entities.Units;
import mindustry.entities.units.*;
import mindustry.gen.*;

public class GroundBossAI extends AIController implements TargetableAI {
    @Override
    public void updateMovement() {
        Building core = unit.closestEnemyCore();
        Unit player = Units.closestEnemy(unit.team, unit.x, unit.y, unit.range() * 10, Unitc::isPlayer);
        Teamc curTarget = player != null ? player : core;

        if (curTarget == null) return;

        if (unit.within(curTarget, unit.range())) {
            target = curTarget;
            for(WeaponMount mount : unit.mounts){
                if(mount.weapon.controllable && mount.weapon.bullet.collidesGround){
                    mount.target = curTarget;
                }
            }
        } else {
            if (player == null) {
                pathfind(Pathfinder.fieldCore);
            } else {
                pathfindTarget(curTarget, unit);
            }
        }

        faceTarget();
    }
}
