package fos.ai;

import fos.content.FOSUnitTypes;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;

public class ProtectorAI extends AIController {
    public Unit protectTarget;

    @Override
    public void updateUnit() {
        super.updateUnit();

        if (protectTarget == null || protectTarget.dead()) {
            protectTarget = Units.closest(unit.team, unit.x, unit.y, u -> u.isPlayer() || u.type == FOSUnitTypes.legion);
        }
    }

    @Override
    public void updateMovement() {
        super.updateMovement();

        if (protectTarget == null) return;

        // Just circle around the unit. That's the entire move set of this AI.
        circle(protectTarget, protectTarget.hitSize * 2 + unit.hitSize);

        // Ok, I lied. A player has to somehow control their shooting.
        if (protectTarget.isPlayer()) {
            for (var wm : unit.mounts()) {
                wm.aimX = protectTarget.aimX;
                wm.aimY = protectTarget.aimY;
                wm.shoot = protectTarget.isShooting();
            }
        }

        faceTarget();
    }
}
