package fos.ai;

import arc.util.*;
import fos.content.FOSUnitTypes;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.type.UnitType;

public class ProtectorAI extends AIController {
    public UnitType toProtect = FOSUnitTypes.legion;
    public Unit protectTarget;

    @Override
    public void updateMovement() {
        super.updateMovement();

        if (protectTarget == null) return;

        Teamc mainTarget = !protectTarget.isAI() ? null : Reflect.get(AIController.class, protectTarget.controller(), "target");
        // range check is needed for player only, AI range is already capped by legion's range
        if ((protectTarget.isPlayer() && protectTarget.isShooting() && unit.dst(protectTarget) <= unit.range()) ||
            (protectTarget.isAI() && mainTarget != null)) {
            // offensive stance - when a legion/player targets something
            if (protectTarget.isPlayer()) {
                Tmp.v1.set(protectTarget.aimX, protectTarget.aimY).sub(unit).setLength(unit.speed());
                unit.movePref(Tmp.v1);
            } else {
                target = mainTarget;
                circleAttack(unit.type.circleTargetRadius);
            }
        } else {
            // defensive stance - guard a unit
            circle(protectTarget, protectTarget.hitSize * 2 + unit.hitSize);
            faceMovement();
        }
    }

    @Override
    public void updateTargeting() {
        if (protectTarget == null || protectTarget.dead() || !protectTarget.isValid()) {
            protectTarget = Units.closest(unit.team, unit.x, unit.y, u -> u.isPlayer() || u.type == toProtect);
        }

        // couldn't find a new ally to protect? commit seppuku
        if (protectTarget == null) {
            unit.kill();
            return;
        }

        if (!protectTarget.isPlayer()) {
            super.updateTargeting();
        } else {
            unit.aimX = protectTarget.aimX;
            unit.aimY = protectTarget.aimY;
            unit.isShooting(protectTarget.isShooting());

            for (var wm : unit.mounts()) {
                wm.aimX = unit.aimX;
                wm.aimY = unit.aimY;
                wm.shoot = unit.isShooting();
            }
        }
    }
}
