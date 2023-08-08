package fos.type.bullets;

import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;

public class InjectorBlastBulletType extends BulletType implements InjectorBulletType {
    public float chance = 0.1f;
    public boolean attacksGuardians = false;

    //used by Lieutenant aka tier 2 Injector
    public InjectorBlastBulletType(float chance, boolean attacksGuardians) {
        super();
        this.chance = chance;
        this.attacksGuardians = attacksGuardians;

        collidesTiles = collides = false;
        hitSound = Sounds.explosion;
        rangeOverride = 16f;
        hitEffect = Fx.pulverize;
        speed = 0f;
        killShooter = true;
        instantDisappear = true;
        hittable = false;
        collidesAir = collidesGround = true;
    }

    @Override
    public float minChance() {
        return chance;
    }

    @Override
    public float maxChance() {
        return chance;
    }

    @Override
    public float minHP() {
        return 0f;
    }

    @Override
    public float maxHP() {
        return 0f;
    }

    @Override
    public boolean attacksGuardians() {
        return attacksGuardians;
    }

    @Override
    public void hit(Bullet b) {
        super.hit(b);

        //do the actual "injection" here
        Units.nearbyEnemies(b.team, b.x, b.y, b.type.splashDamageRadius, u -> onHit(b, u));
    }
}
