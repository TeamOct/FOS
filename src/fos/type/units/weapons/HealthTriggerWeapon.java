package fos.type.units.weapons;

import arc.Core;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.entities.*;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.type.*;

import static mindustry.Vars.headless;

public class HealthTriggerWeapon extends Weapon {
    /** Health fraction needed to reach before activating the weapon. */
    public float healthFrac = 0.5f;

    public HealthTriggerWeapon() {
        super();
    }

    public HealthTriggerWeapon(String name) {
        super(name);
    }

    @Override
    public void addStats(UnitType u, Table t) {
        super.addStats(u, t);

        t.row();
        t.add("[lightgray]" + Core.bundle.format("stat.fos-healthtrigger", Mathf.round(healthFrac * 100)));
    }

    @Override
    public void update(Unit unit, WeaponMount mount) {
        boolean can = unit.canShoot() && unit.health / unit.maxHealth < healthFrac;
        float lastReload = mount.reload;
        mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0);
        mount.recoil = Mathf.approachDelta(mount.recoil, 0, unit.reloadMultiplier / recoilTime);
        if (recoils > 0) {
            if (mount.recoils == null) mount.recoils = new float[recoils];
            for (int i = 0; i < recoils; i++) {
                mount.recoils[i] = Mathf.approachDelta(mount.recoils[i], 0, unit.reloadMultiplier / recoilTime);
            }
        }
        mount.smoothReload = Mathf.lerpDelta(mount.smoothReload, mount.reload / reload, smoothReloadSpeed);
        mount.charge = mount.charging && shoot.firstShotDelay > 0 ? Mathf.approachDelta(mount.charge, 1, 1 / shoot.firstShotDelay) : 0;

        float warmupTarget = (can && mount.shoot) || (continuous && mount.bullet != null) || mount.charging ? 1f : 0f;
        if (linearWarmup) {
            mount.warmup = Mathf.approachDelta(mount.warmup, warmupTarget, shootWarmupSpeed);
        } else {
            mount.warmup = Mathf.lerpDelta(mount.warmup, warmupTarget, shootWarmupSpeed);
        }

        float
            mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
            mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

        //find a new target
        if (!controllable && autoTarget) {
            if ((mount.retarget -= Time.delta) <= 0f) {
                mount.target = findTarget(unit, mountX, mountY, bullet.range, bullet.collidesAir, bullet.collidesGround);
                mount.retarget = mount.target == null ? targetInterval : targetSwitchInterval;
            }

            if (mount.target != null && checkTarget(unit, mount.target, mountX, mountY, bullet.range)) {
                mount.target = null;
            }

            boolean shoot = false;

            if (mount.target != null) {
                shoot = mount.target.within(mountX, mountY, bullet.range + Math.abs(shootY) + (mount.target instanceof Sized s ? s.hitSize() / 2f : 0f)) && can;

                if (predictTarget) {
                    Vec2 to = Predict.intercept(unit, mount.target, bullet);
                    mount.aimX = to.x;
                    mount.aimY = to.y;
                } else {
                    mount.aimX = mount.target.x();
                    mount.aimY = mount.target.y();
                }
            }

            mount.shoot = mount.rotate = shoot;

            //note that shooting state is not affected, as these cannot be controlled
            //logic will return shooting as false even if these return true, which is fine
        }

        //rotate if applicable
        if (rotate && (mount.rotate || mount.shoot) && can) {
            float axisX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                axisY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

            mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta);
            if (rotationLimit < 360) {
                float dst = Angles.angleDist(mount.rotation, baseRotation);
                if (dst > rotationLimit / 2f) {
                    mount.rotation = Angles.moveToward(mount.rotation, baseRotation, dst - rotationLimit / 2f);
                }
            }
        } else if (!rotate) {
            mount.rotation = baseRotation;
            mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
        }

        float
            weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : baseRotation),
            bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
            bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY),
            shootAngle = bulletRotation(unit, mount, bulletX, bulletY);

        if (alwaysShooting) mount.shoot = true;

        //update continuous state
        if (continuous && mount.bullet != null) {
            if (!mount.bullet.isAdded() || mount.bullet.time >= mount.bullet.lifetime || mount.bullet.type != bullet) {
                mount.bullet = null;
            } else {
                mount.bullet.rotation(weaponRotation + 90);
                mount.bullet.set(bulletX, bulletY);
                mount.reload = reload;
                mount.recoil = 1f;
                unit.vel.add(Tmp.v1.trns(mount.bullet.rotation() + 180f, mount.bullet.type.recoil * Time.delta));
                if (shootSound != Sounds.none && !headless) {
                    if (mount.sound == null) mount.sound = new SoundLoop(shootSound, 1f);
                    mount.sound.update(bulletX, bulletY, true);
                }

                //target length of laser
                float shootLength = Math.min(Mathf.dst(bulletX, bulletY, mount.aimX, mount.aimY), range());
                //current length of laser
                float curLength = Mathf.dst(bulletX, bulletY, mount.bullet.aimX, mount.bullet.aimY);
                //resulting length of the bullet (smoothed)
                float resultLength = Mathf.approachDelta(curLength, shootLength, aimChangeSpeed);
                //actual aim end point based on length
                Tmp.v1.trns(shootAngle, mount.lastLength = resultLength).add(bulletX, bulletY);

                mount.bullet.aimX = Tmp.v1.x;
                mount.bullet.aimY = Tmp.v1.y;

                if (alwaysContinuous && mount.shoot) {
                    mount.bullet.time = mount.bullet.lifetime * mount.bullet.type.optimalLifeFract * mount.warmup;
                    mount.bullet.keepAlive = true;

                    unit.apply(shootStatus, shootStatusDuration);
                }
            }
        } else {
            //heat decreases when not firing
            mount.heat = Math.max(mount.heat - Time.delta * unit.reloadMultiplier / cooldownTime, 0);

            if (mount.sound != null) {
                mount.sound.update(bulletX, bulletY, false);
            }
        }

        //flip weapon shoot side for alternating weapons
        boolean wasFlipped = mount.side;
        if (otherSide >= 0 && alternate && mount.side == flipSprite && otherSide < unit.mounts.length && mount.reload <= reload / 2f && lastReload > reload / 2f) {
            unit.mounts[otherSide].side = !unit.mounts[otherSide].side;
            mount.side = !mount.side;
        }

        if (!headless && activeSound != Sounds.none && mount.shoot && can && mount.warmup >= minWarmup) {
            Vars.control.sound.loop(activeSound, unit, activeSoundVolume);
        }

        float velLen = unit.isRemote() ? unit.vel.len() : unit.deltaLen() / Time.delta;

        //shoot if applicable
        if (mount.shoot && //must be shooting
            can && //must be able to shoot
            !(bullet.killShooter && mount.totalShots > 0) && //if the bullet kills the shooter, you should only ever be able to shoot once
            (!alternate || wasFlipped == flipSprite) &&
            mount.warmup >= minWarmup && //must be warmed up
            velLen >= minShootVelocity && //check velocity requirements
            (mount.reload <= 0.0001f || (alwaysContinuous && mount.bullet == null)) && //reload has to be 0, or it has to be an always-continuous weapon
            (alwaysShooting || Angles.within(rotate ? mount.rotation : unit.rotation + baseRotation, mount.targetRotation, shootCone)) //has to be within the cone
        ) {
            shoot(unit, mount, bulletX, bulletY, shootAngle);

            mount.reload = reload;
        }
    }
}
