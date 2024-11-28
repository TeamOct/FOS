package fos.entities.bullet.player;

import arc.Events;
import arc.util.*;
import mindustry.Vars;
import mindustry.entities.bullet.*;
import mindustry.game.EventType;
import mindustry.gen.*;

public class PlayerBasicBulletType extends BasicBulletType {

    public PlayerBasicBulletType() {
        super();
    }

    public PlayerBasicBulletType(float speed, float damage) {
        super(speed, damage);
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        boolean wasDead = entity instanceof Unit u && u.dead;
        boolean isBoss = Vars.state.isCampaign() && entity instanceof Unit u && u.isBoss();
        int players = Groups.player.size();

        if (entity instanceof Healthc h){
            if (pierceArmor) {
                h.damagePierce(isBoss ? b.damage * (2f / (players + 1)) : b.damage);
            } else {
                h.damage(isBoss ? b.damage * (2f / (players + 1)) : b.damage);
            }
        }

        if (entity instanceof Unit unit) {
            Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
            if(impact) Tmp.v3.setAngle(b.rotation() + (knockback < 0 ? 180f : 0f));
            unit.impulse(Tmp.v3);
            unit.apply(status, statusDuration);

            EventType.UnitDamageEvent e = Reflect.get(BulletType.class, "bulletDamageEvent");
            Events.fire(e.set(unit, b));
        }

        if (!wasDead && entity instanceof Unit unit && unit.dead) {
            Events.fire(new EventType.UnitBulletDestroyEvent(unit, b));
        }

        handlePierce(b, health, entity.x(), entity.y());
    }
}
