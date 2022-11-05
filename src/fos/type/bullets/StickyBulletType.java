package fos.type.bullets;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.pooling.Pool.Poolable;
import arc.util.pooling.Pools;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.*;
import mindustry.graphics.Layer;

public class StickyBulletType extends BasicBulletType {
    /** An interval between the contact with an enemy and the explosion. */
    public int explosionDelay;

    public StickyBulletType(float speed, float damage, int explosionDelay) {
        super(speed, damage);
        this.explosionDelay = explosionDelay;
        layer = Layer.flyingUnit + 1f;
        despawnHit = true;
        lifetime += explosionDelay;
        pierce = true;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        b.data = Pools.obtain(StickyBulletData.class, StickyBulletData::new);
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        super.hitEntity(b, entity, health);

        StickyBulletData data = (StickyBulletData) b.data;
        if (data.hit) return;

        data.target = (Teamc) entity;
        b.lifetime = explosionDelay;
        data.hit = true;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        StickyBulletData data = (StickyBulletData) b.data;

        if (data == null || data.target == null) return;

        float bx = b.x(), by = b.y();
        float ox = data.target.x(), oy = data.target.y();

        if (data.initialAngle == null) data.initialAngle = Mathf.angle(bx - ox, by - oy);
        if (data.target instanceof Unit u && data.targetRot == null) data.targetRot = u.rotation;

        b.vel(Vec2.ZERO);

        if (data.target instanceof Unit u) {
            float angle = data.initialAngle - data.targetRot + u.rotation;

            b.x = u.x + Mathf.cos(angle * Mathf.degreesToRadians) * u.hitSize / 2;
            b.y = u.y + Mathf.sin(angle * Mathf.degreesToRadians) * u.hitSize / 2;

            if (u.dead()) b.vel(Vec2.ZERO);
        }
    }

    @Override
    public void removed(Bullet b) {
        super.removed(b);
        createSplashDamage(b, b.x, b.y);
    }

    public static class StickyBulletData implements Poolable {
        public boolean hit = false;
        public Teamc target;
        public Float initialAngle, targetRot;

        @Override
        public void reset() {
            hit = false;
            initialAngle = null;
            target = null;
            targetRot = null;
        }
    }
}
