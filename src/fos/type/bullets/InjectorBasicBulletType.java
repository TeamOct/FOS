package fos.type.bullets;

import fos.graphics.FOSPal;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.*;

public class InjectorBasicBulletType extends BasicBulletType implements InjectorBulletType {
    public float minChance, maxChance, minHP, maxHP;
    public boolean attacksGuardians;

    //chance varies, but with permanent effect (tiers 1, 3)
    public InjectorBasicBulletType(float minChance, float maxChance, float minHP, float maxHP, boolean attacksGuardians) {
        super(8, 10);
        lifetime = 20;
        width = 6; height = 12;
        hittable = false;
        absorbable = true;
        //collidesTiles = false;
        frontColor = FOSPal.hacked;
        backColor = FOSPal.hackedBack;

        this.minChance = minChance;
        this.maxChance = maxChance;
        this.minHP = minHP;
        this.maxHP = maxHP;
        this.attacksGuardians = attacksGuardians;
    }

    //fixed chance (tier 5)
    public InjectorBasicBulletType(float chance, boolean attacksGuardians) {
        this(chance, chance, 0f, 0f, attacksGuardians);
    }

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
        return attacksGuardians;
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        super.hitEntity(b, entity, health);

        //do the actual "injection" here
        onHit(b, entity);
    }
}
