package fos.ai;

import fos.content.FOSUnitTypes;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;

public class ProtectorAI extends AIController {
    public Unit protectTarget;

    @Override
    public void updateMovement() {
        super.updateMovement();

        if (protectTarget == null) return;

        // Just circle around the unit. That's the entire move set of this AI.
        circle(protectTarget, protectTarget.hitSize * 2 + unit.hitSize);

        if (unit.isShooting()) {
            unit.aimLook(unit.aimX, unit.aimY);
        } else {
            faceMovement();
        }
    }

    @Override
    public void updateTargeting() {
        if (protectTarget == null || protectTarget.dead()) {
            protectTarget = Units.closest(unit.team, unit.x, unit.y, u -> u.isPlayer() || u.type == FOSUnitTypes.legion);
        }

        // couldn't find a new ally to protect? commit seppuku
        if (protectTarget == null) {
            unit.kill();
            return;
        }

        if (!protectTarget.isPlayer()) {
            super.updateTargeting();
        } else {
            // Ok, I lied. A player has to somehow control their shooting.
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
