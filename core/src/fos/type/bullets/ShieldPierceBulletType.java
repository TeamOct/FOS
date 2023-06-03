package fos.type.bullets;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;

/**
 * A bullet that has a chance to pierce and nullify a target's shield completely.
 * @author Slotterleet
 */
public class ShieldPierceBulletType extends BulletType {
    private static float cdist = 0f;
    private static Unit result;

    /** Shield pierce chance. */
    public float pierceChance = 0.1f;
    /** Smoke trail spacing (why). */
    public float trailSpacing = 100f;
    /** Effect displayed on success. */
    public Effect pierceEffect = Fx.unitShieldBreak;

    public ShieldPierceBulletType(float pierceChance) {
        super();
        trailEffect = Fx.missileTrailSmoke;
        this.pierceChance = pierceChance;
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        float px = b.x + b.lifetime * b.vel.x,
            py = b.y + b.lifetime * b.vel.y,
            rot = b.rotation();

        Geometry.iterateLine(0f, b.x, b.y, px, py, trailSpacing, (x, y) -> {
            trailEffect.at(x, y, rot);
        });

        b.time = b.lifetime;
        b.set(px, py);

        //calculate hit entity

        cdist = 0f;
        result = null;
        float range = 1f;

        Units.nearbyEnemies(b.team, px - range, py - range, range*2f, range*2f, e -> {
            if(e.dead() || !e.checkTarget(collidesAir, collidesGround) || !e.hittable()) return;

            e.hitbox(Tmp.r1);
            if(!Tmp.r1.contains(px, py)) return;

            float dst = e.dst(px, py) - e.hitSize;
            if((result == null || dst < cdist)){
                result = e;
                cdist = dst;
            }
        });

        if(result != null){
            b.collision(result, px, py);
            if (Mathf.random() < pierceChance) {
                result.shield = 0f;
                pierceEffect.at(px, py, b.rotation());
            }
        }else if(collidesTiles){
            Building build = Vars.world.buildWorld(px, py);
            if(build != null && build.team != b.team){
                build.collision(b);
            }
        }

        b.remove();

        b.vel.setZero();
    }
}
