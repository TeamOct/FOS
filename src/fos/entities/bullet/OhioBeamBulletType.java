package fos.entities.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Point2;
import fos.content.FOSFx;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.Turret;

/**
 * A death ray fired by the Judge turret. Currently, only works properly with turrets.
 * @author Slotterleet
 */
public class OhioBeamBulletType extends ContinuousBulletType {
    /** The beam's color. */
    public Color color = Pal.slagOrange;
    /** The beam radius. */
    public float width;
    /** Evaporation effect when a ray lands on liquid. */
    public Effect evaporationEffect = Fx.smokePuff;

    public OhioBeamBulletType(float dps, float width) {
        super();
        /* does nothing, purely for display lmao */ damage = 1f;
        collides = true;
        splashDamage = dps / (60f / damageInterval);
        this.width = width;
        splashDamageRadius = this.width;
        scaledSplashDamage = false;
        /* aaaaaaaaaa */ displayAmmoMultiplier = false;
        speed = 1f;
        lifetime = 30f;
        status = StatusEffects.melting;
        incendAmount = 20;
        incendSpread = this.width;
        shake = 10f;
        buildingDamageMultiplier = 0.5f;
        /* Anuke, why is this drawSize and not clipSize like everything else */ drawSize = 720f;
        pierceBuilding = true;
        pierceArmor = true;
        splashDamagePierce = true;
        despawnEffect = FOSFx.deathrayDespawn;
        despawnSound = Sounds.none;
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
            //check for turret range
            if (!Mathf.within(b.x, b.y, t.x, t.y, t.range())) {
                b.vel.setAngle(b.angleTo(t));
                b.vel.setLength(b.dst(t) - t.range() + 1f);
                return;
            } else if (Mathf.within(b.x, b.y, t.x, t.y, ((Turret)t.block).minRange)) {
                b.vel.setAngle(b.angleTo(t) + 180);
                b.vel.setLength(((Turret)t.block).minRange - b.dst(t) + 1f);
                return;
            }

            b.vel.setLength(b.type.speed);
            b.vel.setAngle(b.angleTo(t.targetPos));

            //don't reload the turret when it's still shooting.
            t.reloadCounter = ((Turret) t.block).reload;
        }
    }

    @Override
    public void despawned(Bullet b) {
        if (despawnHit) {
            hit(b);
        }

        if (b.owner instanceof Turret.TurretBuild t) {
            despawnEffect.at(b.x, b.y, b.rotation(), hitColor, Point2.pack((int)t.x, (int)t.y));
        }
        despawnSound.at(b);

        Effect.shake(despawnShake, despawnShake, b);
    }

    @Override
    public void draw(Bullet b) {
        var centerTile = b.tileOn();
        if (centerTile != null) {
            centerTile.circle((int) (width / Vars.tilesize + 1), t -> {
                if (t != null && t.floor().isLiquid && !t.solid())
                    evaporationEffect.at(t.worldx(), t.worldy(), t.floor().liquidDrop.gasColor);
            });
        }

        Lines.stroke(80f, color);
        drawBeam(color, b.x, b.y, width);
        if (b.owner instanceof Turret.TurretBuild t) {
            drawBeam(color, t.x, t.y, width);
        }
    }

    /** Draws a beam that goes upwards. */
    public static void drawBeam(Color color, float x, float y, float rad) {
        Draw.color(Pal.redLight, 0.6f);
        Fill.poly(x, y, 48, rad * 1.2f);

        Draw.color(color, 1f);
        Fill.poly(x, y, 48, rad);

        Fill.quad(x - rad, y, x + rad, y, x + rad, y + 4000f, x - rad, y + 4000f);

        Fill.poly(x, y + 4000f, 48, rad);

        Drawf.light(x, y + rad, x, y + rad + 1600f, rad * 2, color, 0.8f);
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
