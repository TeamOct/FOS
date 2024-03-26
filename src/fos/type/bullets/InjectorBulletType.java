package fos.type.bullets;

import arc.math.Mathf;
import fos.content.FOSStatuses;
import mindustry.content.StatusEffects;
import mindustry.gen.*;

/**
 * An interface for Injector bullets which change the target's team to the bullet owner's team.
 * @author Slotterleet
 */
public interface InjectorBulletType {
    float minChance();
    float maxChance();
    float minHP();
    float maxHP();
    boolean attacksGuardians();

    default float chance(Entityc entity) {
        float health = ((Unit) entity).health;
        if (health <= minHP()){
            return maxChance();
        } else if (health >= maxHP()) {
            return minChance();
        } else {
            //this formula is really complicated, does it even make sense??
            return 1 - ((health - minHP()) / (maxHP() - minHP()));
        }
    }

    default void onHit(Bullet b, Hitboxc entity) {
        if (entity instanceof Unit u) {
            //if the target is a boss AND a projectile can't attack bosses, return
            if (u.isBoss() && !attacksGuardians()) return;
            //no point of overriding the effect
            if (u.hasEffect(FOSStatuses.hacked)) return;
            //do not hack anyone immune to the effect
            if (u.isImmune(FOSStatuses.hacked)) return;
            //do not take over players
            if (u.isPlayer()) return;

            float chance = chance(entity);
            if (Mathf.random() < chance) {
                u.apply(FOSStatuses.hacked);
                u.team = b.team;
                if (u.isBoss()) u.unapply(StatusEffects.boss);
            }
        }
    }
}
