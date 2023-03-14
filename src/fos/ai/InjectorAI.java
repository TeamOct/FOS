package fos.ai;

import fos.type.bullets.InjectorBulletType;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.UnitSorts;
import mindustry.entities.Units;
import mindustry.gen.Teamc;

public class InjectorAI extends FlyingAI {
    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground) {
        InjectorBulletType bullet = (InjectorBulletType) unit.type.weapons.get(0).bullet;
        return Units.bestEnemy(unit.team, x, y, range * 100f, u -> u.health <= bullet.maxHP && !u.isPlayer() && (!u.isBoss() || bullet.attacksGuardians), UnitSorts.weakest);
    }
}
