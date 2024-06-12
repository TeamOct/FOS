package fos.type.bullets;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.annotations.Annotations;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.*;
import mindustry.graphics.Layer;

public class StickyBulletType extends BasicBulletType {
    /** An interval between the contact with an enemy and the explosion. */
    public int explosionDelay;
    /** If true, ignores explosion delay and only blows up after double tapping. */
    public boolean persistent = false;
    /** Persistent sticky bomb limit. */
    public int bulletCap = 10;

    public StickyBulletType(float speed, float damage) {
        this(speed, damage, 10);
        persistent = true;
    }

    public StickyBulletType(float speed, float damage, int explosionDelay) {
        super(speed, damage);
        this.explosionDelay = explosionDelay;

        sprite = "fos-sticky-bullet";
        backSprite = "bullet-back";
        layer = Layer.flyingUnit + 1f;
        hitEffect = Fx.blastExplosion;
        hitSound = Sounds.explosion;

        setDefaults = false;
        despawnHit = false;
        pierce = true;
        collides = true;
        collidesGround = true;
        collidesAir = false;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);

        int counter = Groups.bullet.count(bul -> bul.type == this);
        if (persistent && counter > bulletCap) {
            Groups.bullet.find(bul -> bul.type == this).remove();
        }

    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        super.hitEntity(b, entity, health);

        //b.hit(true);

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
    public void hit(Bullet b, float x, float y) {
        // apply splash damage only when a sticky is already triggered.
        if (b.data instanceof StickyBulletData)
            super.hit(b, x, y);
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        StickyBulletData data = (StickyBulletData) b.data;

        if (data != null) {
            if (persistent) {
                if (b.owner instanceof Unitc u && u.dead())
                    b.remove();
                b.time = 0f;
            }

            if (data.target == null) {
                b.vel.set(Vec2.ZERO);
                return;
            }

            if (data.target instanceof Unit u && !u.dead()) {
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
    }

    @Override
    public void removed(Bullet b) {
        super.removed(b);

        if (b.data == null) {
            // this bullet was already despawned; create a new one that stays on ground
            Bullet nb = create(b.owner, b.team, b.x, b.y, b.rotation());
            //nb.hit(true);
            nb.lifetime = explosionDelay;
            nb.data = new StickyBulletData();
        } else {
            hit(b);
        }
    }

    @Annotations.Remote(called = Annotations.Loc.both, targets = Annotations.Loc.client, forward = true)
    public static void detonate(Player player) {
        Groups.bullet.each(b -> {
            if (b == null || !(b.data instanceof StickyBulletData) || b.owner == null) return;

            if (b.owner == player.unit()) b.lifetime = 1f;
        });
    }

    public static class StickyBulletData {
        public Teamc target;
        public Float initialAngle, targetRot;
    }
}
