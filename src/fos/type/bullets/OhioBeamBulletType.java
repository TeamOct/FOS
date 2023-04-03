package fos.type.bullets;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.Turret;

/**
 * A death ray fired by the Judge turret. Currently, only works properly with turrets.
 * @author Slotterleet
 */
public class OhioBeamBulletType extends ContinuousBulletType {
    /** The beam's color. */
    public Color color = Pal.turretHeat;

    public OhioBeamBulletType(float dps) {
        super();
        /* does nothing, purely for display lmao */ damage = 1f;
        collides = true;
        splashDamage = dps / (60f / damageInterval);
        splashDamageRadius = 64f;
        speed = 1f;
        lifetime = 600f;
        status = StatusEffects.melting;
        incendAmount = 20;
        incendSpread = 32f;
        shake = 10f;
        buildingDamageMultiplier = 0.5f;
        pierceBuilding = true;
        pierceArmor = true;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);

        if (b.owner instanceof Turret.TurretBuild t) {
            b.x(t.targetPos.x);
            b.y(t.targetPos.y);
        }
    }

    @Override
    public float continuousDamage() {
        if(!continuous) return -1f;
        return splashDamage / damageInterval * 60f;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        if (b.owner instanceof Turret.TurretBuild t) {
            if (t.isControlled()) {
                b.vel.setAngle(b.angleTo(Core.input.mouseWorld()));
            } else {
                b.vel.setAngle(b.angleTo(t.targetPos));
            }

            //check for turret range
            if (!Mathf.within(b.x, b.y, t.x, t.y, t.range())) {
                b.vel.setAngle(b.angleTo(t));
                b.vel.setLength(b.dst(t) - t.range() + 1f);
            } else if (Mathf.within(b.x, b.y, t.x, t.y, ((Turret)t.block).minRange)) {
                b.vel.setAngle(b.angleTo(t) + 180);
                b.vel.setLength(((Turret)t.block).minRange - b.dst(t) + 1f);
            }
        }

    }

    @Override
    public void draw(Bullet b) {
        Lines.stroke(80f, color);
        drawBeam(color, b.x, b.y);
        if (b.owner instanceof Turret.TurretBuild t) {
            drawBeam(color, t.x, t.y);
        }
    }

    /** Draws a beam that goes upwards. */
    public void drawBeam(Color color, float x, float y) {
        float radius = 32f;

        Draw.color(Pal.redLight, 0.6f);
        Fill.poly(x, y, 48, radius * 1.2f);

        Draw.color(color, color.a);
        Fill.poly(x, y, 48, radius);

        Fill.quad(x - radius, y, x + radius, y, x + radius, y + 800f, x - radius, y + 800f);

        Drawf.light(x, y + radius, x, y + radius + 400f, radius * 2, color, 0.8f);
        Draw.reset();
    }

    @Override
    public void applyDamage(Bullet b) {
        createSplashDamage(b, b.x, b.y);
        createIncend(b, b.x, b.y);
    }

    @Override
    public void drawLight(Bullet b) {
        //no light drawn here
    }
}
