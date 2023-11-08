package fos.type.bullets;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.type.unit.MissileUnitType;

public class StickyBulletType extends BasicBulletType {
    /** An interval between the contact with an enemy and the explosion. */
    public int explosionDelay;

    public StickyBulletType(float speed, float damage, int explosionDelay) {
        super(speed, damage);
        sprite = "fos-sticky-bullet";
        backSprite = "bullet-back";
        this.explosionDelay = explosionDelay;
        layer = Layer.flyingUnit + 1f;
        despawnHit = true;
        pierce = true;
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        //do not target missiles.
        if (((Unit) entity).type instanceof MissileUnitType) return;

        super.hitEntity(b, entity, health);

        b.hit(true);

        StickyBulletData data = new StickyBulletData();
        data.target = (Teamc) entity;
        b.data = data;

        b.lifetime = explosionDelay;
    }

    @Override
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
        super.hitTile(b, build, x, y, initialHealth, direct);

        //the bullet just stops.
        b.vel.set(Vec2.ZERO);
        b.lifetime = explosionDelay;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        StickyBulletData data = (StickyBulletData) b.data;
        if (data != null && data.target instanceof Unit u && !u.dead()) {
            float bx = b.x(), by = b.y();
            float ox = data.target.x(), oy = data.target.y();

            if (data.initialAngle == null) data.initialAngle = Mathf.angle(bx - ox, by - oy);
            if (data.targetRot == null) data.targetRot = u.rotation;

            float angle = data.initialAngle - data.targetRot + u.rotation;

            var vx = (u.x + Mathf.cos(angle * Mathf.degRad) * u.hitSize / 2) - b.x;
            var vy = (u.y + Mathf.sin(angle * Mathf.degRad) * u.hitSize / 2) - b.y;

            if (vx == 0 && vy == 0) {
                b.vel.set(Vec2.ZERO);
            } else {
                b.vel.set(vx, vy);
            }
        }
    }

    @Override
    public void removed(Bullet b) {
        super.removed(b);
        createSplashDamage(b, b.x, b.y);
    }

    @Override
    public void despawned(Bullet b) {
        super.despawned(b);
        createSplashDamage(b, b.x, b.y);
    }

    public static class StickyBulletData {
        public Teamc target;
        public Float initialAngle, targetRot;
    }
}
