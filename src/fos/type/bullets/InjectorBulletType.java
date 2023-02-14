package fos.type.bullets;

import arc.math.Mathf;
import fos.content.*;
import fos.graphics.FOSPal;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class InjectorBulletType extends BasicBulletType {
    public final float minChance, maxChance, minHP, maxHP;
    public final boolean attacksGuardians;
    //chance varies, but with permanent effect (tiers 1, 3)
    public InjectorBulletType(float minChance, float maxChance, float minHP, float maxHP, boolean attacksGuardians) {
        super(8, 10);
        lifetime = 20;
        width = 6; height = 12;
        hittable = false;
        absorbable = true;
        collidesTiles = false;
        frontColor = FOSPal.hacked;
        backColor = FOSPal.hackedBack;

        this.minChance = minChance;
        this.maxChance = maxChance;
        this.minHP = minHP;
        this.maxHP = maxHP;
        this.attacksGuardians = attacksGuardians;
    }
    //fixed chance, (tiers 2, 5)
    public InjectorBulletType(float chance, boolean attacksGuardians) {
        super(8, 10);
        lifetime = 20;
        width = 6;
        height = 12;

        this.minChance = this.maxChance = chance;
        this.minHP = this.maxHP = 0;
        this.attacksGuardians = attacksGuardians;
    }

    private float chance(Entityc entity) {
        float health = ((Unit) entity).health;
        if (health <= minHP){
            return maxChance;
        } else if (health >= maxHP) {
            return minChance;
        } else {
            //this formula is really complicated, does it even make sense??
            return 1 - ((health - minHP) / (maxHP - minHP));
        }

    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        super.hitEntity(b, entity, health);

        if (entity instanceof Unit u) {
            //if the target is a boss AND a projectile can't attack bosses, return
            if (u.isBoss() && !attacksGuardians) return;
            //no point of overriding the effect
            if (u.hasEffect(FOSStatuses.hacked)) return;
            //do not take over players
            if (u.isPlayer()) return;

            float chance = chance(entity);
            if (Mathf.random(chance) < chance) {
                u.apply(FOSStatuses.hacked);
                u.team = b.team;
                if (u.isBoss()) u.unapply(StatusEffects.boss);
            }
        }
    }
}
