package fos.type.bullets;

import fos.content.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class InjectorBulletType extends BasicBulletType {
    private final float minChance, maxChance, minHPThreshold, maxHPThreshold;
    private final boolean attacksGuardians;
    public InjectorBulletType(float minchance, float maxchance, float minHP, float maxHP, boolean attacksBosses){
        super(8, 1);
        lifetime = 20;
        width = 6; height = 12;

        minChance = minchance;
        maxChance = maxchance;
        minHPThreshold = minHP;
        maxHPThreshold = maxHP;
        attacksGuardians = attacksBosses;
    }

    private float chance(Entityc entity) {
        float health = ((Unit) entity).health;
        if (health <= minHPThreshold){
            return maxChance;
        } else if (health >= maxHPThreshold) {
            return minChance;
        } else {
            float hpRange = maxHPThreshold - minHPThreshold;
            return (health - minHPThreshold) / hpRange;
        }

    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        if (entity instanceof Unit){
            //if it can't attack bosses, return
            if ((((Unit) entity).hasEffect(StatusEffects.boss)) && (!attacksGuardians)) return;
            //no point of overriding the effect
            if (((Unit) entity).hasEffect(FOSStatuses.hacked)) return;

            float chance = chance(entity);
            if (Math.random() < chance){
                ((Unit) entity).team = b.team;
                ((Unit) entity).apply(FOSStatuses.hacked);
            }
        }
    }
}
