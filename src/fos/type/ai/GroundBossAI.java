package fos.type.ai;

import fos.type.units.LuminaUnitType;
import mindustry.ai.Pathfinder;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.Tile;

import static mindustry.Vars.pathfinder;

public class GroundBossAI extends AIController {
    @Override
    public void updateMovement() {
        Building core = unit.closestEnemyCore();
        Unit player = Units.closestEnemy(unit.team, unit.x, unit.y, 999f, u -> u.type instanceof LuminaUnitType);
        Teamc curTarget = player != null ? player : core;

        if (curTarget == null) return;

        if (unit.within(curTarget, unit.range())) {
            target = curTarget;
            for(WeaponMount mount : unit.mounts){
                if(mount.weapon.controllable && mount.weapon.bullet.collidesGround){
                    mount.target = curTarget;
                }
            }
        }

        if (!unit.within(curTarget, unit.type.range)) {
            if (player == null) {
                pathfind(Pathfinder.fieldCore);
            } else {
                pathfindUnit(curTarget);
            }
        }

        faceTarget();
    }

    public void pathfindUnit(Teamc target) {
        int costType = unit.pathType();

        Tile tile = target.tileOn();
        if(tile == null) return;
        Tile targetTile = pathfinder.getTargetTile(tile, pathfinder.getField(unit.team, costType, Pathfinder.fieldCore));

        if(tile == targetTile) return;

        unit.movePref(vec.trns(unit.angleTo(targetTile.worldx(), targetTile.worldy()), unit.speed()));
    }
}
