package fos.ai;

import fos.content.FOSStatuses;
import fos.type.bullets.InjectorBulletType;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.*;
import mindustry.gen.Teamc;

public class InjectorAI extends FlyingAI {
    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground) {
        InjectorBulletType bullet = (InjectorBulletType) unit.type.weapons.get(0).bullet;
        return Units.bestEnemy(unit.team, x, y, 999f,
            u -> /*u.health <= bullet.maxHP() &&*/ !u.isPlayer() && (!u.isBoss() || bullet.attacksGuardians()) && !u.isImmune(FOSStatuses.hacked), UnitSorts.weakest);
    }
}
