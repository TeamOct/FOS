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

    default float chance(Entityc owner, Entityc entity) {
        float health = ((Healthc) entity).health();
        float chance;

        if (health <= minHP()){
            chance = maxChance();
        } else if (health >= maxHP()) {
            chance = minChance();
        } else {
            //this formula is really complicated, does it even make sense??
            chance = 1 - ((health - minHP()) / (maxHP() - minHP()));
        }

        if (owner instanceof Unit u && u.hasEffect(FOSStatuses.injected)) {
            chance *= 1.25f;
        }

        if (entity instanceof Unit u && u.hasEffect(FOSStatuses.injected)) {
            chance *= 1.25f;
        }

        return chance;
    }

    default void onHit(Bullet b, Hitboxc entity, boolean always) {
        if (entity instanceof Unit u) {
            //if the target is a boss AND a projectile can't attack bosses, return
            if (u.isBoss() && !attacksGuardians()) return;
            //no point of overriding the effect
            if (u.hasEffect(FOSStatuses.hacked)) return;
            //do not hack anyone immune to the effect
            if (u.isImmune(FOSStatuses.hacked)) return;
            //do not take over players
            if (u.isPlayer()) return;

            float chance = chance(b.owner, entity);
            if (Mathf.random() < chance || always) {
                u.apply(FOSStatuses.hacked);
                u.team = b.team;
                if (u.isBoss()) u.unapply(StatusEffects.boss);
            }
        }
    }

    default void onHit(Bullet b, Hitboxc entity) {
        onHit(b, entity, false);
    }
}
