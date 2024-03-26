package fos.ai;

import fos.content.FOSStatuses;
import fos.type.bullets.InjectorBulletType;
import mindustry.ai.types.GroundAI;
import mindustry.entities.*;
import mindustry.gen.Teamc;

public class InjectorAI extends GroundAI {
    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground) {
        InjectorBulletType bullet = (InjectorBulletType) unit.type.weapons.get(0).bullet;
        var t = Units.bestEnemy(unit.team, x, y, 999f, u ->
            /*u.health <= bullet.maxHP() &&*/ !u.isPlayer() && (!u.isBoss() || bullet.attacksGuardians()) && !u.isImmune(FOSStatuses.hacked), UnitSorts.weakest);

        return t != null ? t : super.findMainTarget(x, y, range, air, ground);
    }
}
