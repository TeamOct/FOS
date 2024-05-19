package fos.type.bullets;

import mindustry.entities.bullet.ContinuousLaserBulletType;

public class InjectorHackLaserBulletType extends ContinuousLaserBulletType implements InjectorBulletType {
    public float minChance, maxChance, minHP, maxHP;

    @Override
    public float minChance() {
        return minChance;
    }

    @Override
    public float maxChance() {
        return maxChance;
    }

    @Override
    public float minHP() {
        return minHP;
    }

    @Override
    public float maxHP() {
        return maxHP;
    }

    @Override
    public boolean attacksGuardians() {
        return true;
    }
}
