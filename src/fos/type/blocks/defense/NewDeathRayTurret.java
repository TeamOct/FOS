package fos.type.blocks.defense;

import arc.Core;
import arc.flabel.effects.ShakeEffect;
import arc.func.Floatp;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Fire;
import mindustry.gen.Icon;
import mindustry.gen.Posc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.meta.Stat;

// TODO add boosters
// TODO consumption on charging
public class NewDeathRayTurret extends PowerTurret {
    /** time of shooting on full charge **/
    public float shootDuration = 180f;
    /** time for full charge **/
    public float chargeTime = 60f;
    public Floatp raySize = () -> size * Vars.tilesize / 2f;
    /** time of drawing the ray after stopping shooting **/
    public float rayEffectTime = 60f;
    public float aimingSpeed = 192f / 60f;

    public Effect ray = new Effect(60f, e -> {
        RayEffectData data = e.data();
        NewDeathRayTurret.drawBeam(Pal.slagOrange, e.x, e.y, raySize.get() * e.fout());
        NewDeathRayTurret.drawBeam(Pal.slagOrange, data.rsx, data.rsy,
                (shootType instanceof DeathRayBulletType d ? d.raySize : 0) * e.fout());
    });

    static class RayEffectData {
        public float rsx, rsy;

        public RayEffectData(float x, float y) {
            rsx = x;
            rsy = y;
        }
    }

    public NewDeathRayTurret(String name) {
        super(name);
    }

    /** Draws a beam that goes upwards. (Yes, I'm just spizdil)*/
    public static void drawBeam(Color color, float x, float y, float rad) {
        float z = Draw.z();
        Draw.z(Layer.effect);
        Draw.color(Pal.redLight, 0.6f);
        Fill.poly(x, y, 48, rad * 1.2f);

        Draw.color(color, 1f);
        Fill.poly(x, y, 48, rad);

        Fill.quad(x - rad, y, x + rad, y, x + rad, y + 4000f, x - rad, y + 4000f);

        Fill.poly(x, y + 4000f, 48, rad);

        Drawf.light(x, y + rad, x, y + rad + 1600f, rad * 2, color, 0.8f);
        Draw.reset();
        Draw.z(z);
    }

    // TODO charge stat
    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.reload);
        stats.remove(Stat.inaccuracy);
        stats.remove(Stat.ammoUse);
        stats.remove(Stat.ammo);
        stats.remove(Stat.booster);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("charge", (b) -> new Bar(
                Core.bundle.get("blocks." + name + "-charge-bar"), Pal.ammo,
                () -> b instanceof NewDeathRayTurretBuild t ? t.charge : 0f
        ));
    }

    @SuppressWarnings("unused")
    public class NewDeathRayTurretBuild extends PowerTurretBuild {
        public float charge = 0f;
        /** for ray drawing **/
        public Vec2 recentShoot = new Vec2();
        public Vec2 aimPos = new Vec2();
        /** using for ray draw **/
        public boolean shooting = false;
        /** is turret uncharged **/
        public boolean overHeat = false;

        public boolean effectSpawned = true;

        public DeathRayBulletType shoot() {
            return (DeathRayBulletType) shootType;
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            aimPos.set(tile.worldx(), tile.worldy());
            return super.init(tile, team, shouldAdd, rotation);
        }

        public boolean updateAim() {
            if (targetPos.isZero()) return false;
            aimPos.add(targetPos.cpy().sub(aimPos).nor().scl(aimingSpeed * edelta()));
            return aimPos.cpy().sub(targetPos).len() <= shoot().raySize;
        }

        @Override
        protected void findTarget() {
            target = Units.closestEnemy(team, x, y, range(), (u) -> !u.dead() &&
                    (targetAir && u.isFlying()) ||
                    (targetGround && u.isGrounded()));
        }

        @Override
        public void updateTile() {
            if(!validateTarget()) target = null;

            clipSize = range;

            wasShooting = false;

            unit.tile(this);
            unit.rotation(rotation);
            unit.team(team);

            if(logicControlTime > 0){
                logicControlTime -= Time.delta;
            }

            if(heatRequirement > 0){
                heatReq = calculateHeat(sideHeat);
            }

            updateReload();

            if(hasAmmo()){
                if (Float.isNaN(reloadCounter)) reloadCounter = 0;

                if (timer(timerTarget, targetInterval)) {
                    // TODO re-write this method
                    findTarget();
                }

                shooting = false;
                if(validateTarget()){
                    boolean canShoot = true;

                    if (isControlled()) {
                        targetPos.set(unit.aimX(), unit.aimY());
                        canShoot = unit.isShooting();
                    } else if (logicControlled()){
                        canShoot = logicShooting;
                    } else {
                        targetPosition(target);

                        if (Float.isNaN(rotation)) rotation = 0;
                    }

                    if (!isControlled()) {
                        unit.aimX(targetPos.x);
                        unit.aimY(targetPos.y);
                    }

                    float targetRot = angleTo(targetPos);

                    if (canShoot) {
                        wasShooting = true;
                        updateShooting();
                    }
                }
            }

            if(coolant != null){
                // TODO re-write cooling
                updateCooling();
            }
        }

        @Override
        public void draw() {
            super.draw();

            Tmp.c4.set(Draw.getColor());
            Draw.color(Pal.lightOrange);
            float z = Draw.z();
            Draw.z(Layer.effect);

            Lines.stroke(8, Pal.lighterOrange.cpy().mul(0.8f));
            Lines.circle(aimPos.x, aimPos.y, shoot().raySize);

            Draw.color(Tmp.c4);
            Draw.z(z);

            if (charge > 0f && shooting) {
                effectSpawned = false;
                drawBeam(Pal.slagOrange, x, y, raySize.get());
                drawBeam(Pal.slagOrange, recentShoot.x, recentShoot.y,
                        shootType instanceof DeathRayBulletType d ? d.raySize : 0);
            } else if (!effectSpawned) {
                ray.lifetime = rayEffectTime;
                ray.clip = Math.max(Vars.world.width(), Vars.world.height()) * 2f;
                ray.at(x, y, 0, new RayEffectData(recentShoot.x, recentShoot.y));
                effectSpawned = true;
            }
        }

        @Override
        public void targetPosition(Posc pos) {
            if (pos != null)
                targetPos.set(pos);
            else
                targetPos.set(Vec2.ZERO);
        }

        @Override
        protected void updateShooting() {
            if (!updateAim()) return;
            if (!aimPos.within(x, y, range())) return;

            if (!overHeat) {
                charge -= 1 / shootDuration * Time.delta;
                clampCharge();

                if (charge == 0)
                    overHeat = true;

                recentShoot.set(aimPos.x, aimPos.y);
                shooting = true;

                Effect.shake(5, 10, this);
                Effect.shake(5, 10, aimPos);
                Vars.world.tileWorld(aimPos.x, aimPos.y)
                        .circle(Mathf.ceil(shoot().raySize / Vars.tilesize), Fires::create);

                Damage.damage(aimPos.x, aimPos.y,
                        shootType instanceof DeathRayBulletType d ? d.raySize : 0, shootType.damage);
            }
        }

        @Override
        public float estimateDps() {
            return overHeat ? 0 : shootType.damage * 60f;
        }

        @Override
        protected void updateReload() {
            if (!shooting && charge < 1f) {
                charge += 1f / chargeTime * edelta();
                clampCharge();
            }
            if (charge == 1f) {
                overHeat = false;
            }
        }

        void clampCharge(){
            charge = Mathf.clamp(charge, 0, 1f);
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case ammo -> power.status;
                case ammoCapacity -> 1;
                default -> super.sense(sensor);
            };
        }

        @Override
        public float activeSoundVolume(){
            return 0.1f;
        }

        @Override
        public boolean shouldActiveSound(){
            return shooting;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            charge = read.f();
            overHeat = read.bool();
            shooting = read.bool();
            aimPos.set(read.f(), read.f());
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(charge);
            write.bool(overHeat);
            write.bool(shooting);
            write.f(aimPos.x);
            write.f(aimPos.y);
        }

        @Override
        public boolean hasAmmo() {
            return power.status > 0;
        }
    }

    /** shouldn't be created **/
    public static class DeathRayBulletType extends BulletType {
        public float raySize = 8f;
    }
}
