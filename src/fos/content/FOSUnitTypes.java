package fos.content;

import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.*;
import fos.ai.*;
import fos.gen.*;
import fos.graphics.*;
import fos.type.abilities.*;
import fos.type.bullets.*;
import fos.type.content.WeaponSet;
import fos.type.units.types.*;
import fos.type.units.weapons.InjectorWeapon;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.*;
import mindustry.audio.SoundLoop;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.ElevationMoveUnit;
import mindustry.gen.LegsUnit;
import mindustry.gen.MechUnit;
import mindustry.gen.TankUnit;
import mindustry.gen.UnitEntity;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.weapons.*;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static ent.anno.Annotations.EntityDef;
import static fos.content.FOSItems.zinc;
import static fos.content.FOSStatuses.*;
import static mindustry.Vars.*;

public class FOSUnitTypes {
    public static @EntityDef({Unitc.class, Mechc.class}) UnitType
        // MECH BOSSES
        legion;

    public static @EntityDef({Unitc.class, Mechc.class, DamageAbsorbc.class}) UnitType
        // MECH BOSSES W/ DR
        citadel;

    public static @EntityDef({Unitc.class, Tankc.class}) UnitType
        // TANK BOSSES
        myriad, warden;

    public static @EntityDef({Unitc.class, ElevationMovec.class}) UnitType
        // HOVERCRAFT (INJECTORS)
        sergeant, lieutenant,

        // HOVERCRAFT (DESTROYERS)
        assault, abrupt;

    public static @EntityDef({Unitc.class}) UnitType
        // FLYING (LEGION SUMMONS)
        legionnaire, legionnaireReplica,

        // FLYING (DESTROYERS)
        brunt;

    public static @EntityDef({Unitc.class, DamageAbsorbc.class}) UnitType
        // FLYING W/ DR (INJECTORS)
        captain;

    public static @EntityDef({Unitc.class, Legsc.class}) UnitType
        // LEGS (ELIMINATORS)
        radix, foetus;

    public static @EntityDef({Unitc.class, Legsc.class, DamageAbsorbc.class}) UnitType
        // LEGS W/ DR (ELIMINATORS)
        vitarus;

    public static @EntityDef({Unitc.class, Payloadc.class}) UnitType
        // PAYLOAD TODO
        vulture;

    public static @EntityDef({Unitc.class, Payloadc.class, BuildingTetherc.class}) UnitType
        // TETHER TODO
        testOverdrive;

    public static @EntityDef({Unitc.class, Submarinec.class}) UnitType
        // SUBMARINES TODO
        subSmall;

    public static @EntityDef({Unitc.class, LumoniPlayerc.class, Legsc.class}) UnitType
        // PLAYER UNITS
        lord, king;

    public static @EntityDef({Unitc.class, Crawlc.class, Bugc.class}) UnitType
        // CRAWLING INSECTS
        bugSmall, bugMedium;

    public static @EntityDef({Unitc.class, Bugc.class}) UnitType
        // FLYING INSECTS
        bugFlyingSmall, bugFlyingMedium;

    public static @EntityDef({Unitc.class, Minerc.class, BuildingTetherc.class}) UnitType
        // MINER UNITS
        draug;

    public static @EntityDef({Unitc.class}) UnitType
        // INTERNAL, USED FOR INITIALIZING WEAPON SETS
        weaponSetInit;

    public static void load(){
        legionnaire = new FOSUnitType("legionnaire", UnitEntity.class){{
            health = 200;
            hitSize = 12;
            rotateSpeed = 12f;
            speed = 2.5f;
            accel = 0.08f;
            drag = 0.04f;
            isEnemy = false;
            flying = true;
            targetPriority = -2f;
            playerControllable = false;
            weapons.add(
                new Weapon(){{
                    x = 2f; y = 3f;
                    mirror = true;
                    alternate = true;
                    top = false;
                    rotate = false;
                    reload = 30f;
                    shootCone = 15f;
                    bullet = new BasicBulletType(2.5f, 45f){{
                        width = 7f;
                        height = 9f;
                        lifetime = 75f;
                        shootEffect = Fx.shootSmall;
                        smokeEffect = Fx.shootSmallSmoke;
                        ammoMultiplier = 2;
                        trailWidth = 3;
                        trailLength = 8;
                        keepVelocity = false;
                    }};
                }}
            );
            controller = u -> new ProtectorAI();
        }};
        legionnaireReplica = new FOSUnitType("legionnaire-replica", UnitEntity.class){{
            health = 150;
            hitSize = 12;
            rotateSpeed = 12f;
            speed = 2f;
            accel = 0.08f;
            drag = 0.04f;
            isEnemy = false;
            flying = true;
            targetPriority = -2f;
            playerControllable = false;
            weapons.add(
                new Weapon(){{
                    x = 0f; y = 3f;
                    mirror = false;
                    top = false;
                    rotate = false;
                    reload = 30f;
                    shootCone = 15f;
                    bullet = new BasicBulletType(2.5f, 45f){{
                        width = 7f;
                        height = 9f;
                        lifetime = 75f;
                        shootEffect = Fx.shootSmall;
                        smokeEffect = Fx.shootSmallSmoke;
                        ammoMultiplier = 2;
                        trailWidth = 3;
                        trailLength = 8;
                        keepVelocity = false;
                    }};
                }}
            );
            controller = u -> new ProtectorAI();
        }};
        legion = new BossUnitType("legion", MechUnit.class){{
            health = 4500;
            armor = 10;
            hitSize = 25;
            speed = 0.3f;
            flying = false;
            canBoost = false;
            range = 40f;

            weapons.add(
                new RepairBeamWeapon("fos-legion-beam"){{
                    x = 4; y = 0;
                    mirror = true;
                    beamWidth = 0.6f;
                    bullet = new BulletType(){{
                        maxRange = 40f;
                    }};
                }},
                new Weapon("fos-legion-sapper"){{
                    x = 0f; y = 6f;
                    mirror = false;
                    reload = 40f;
                    top = false;
                    layerOffset = -0.05f;
                    rotate = false;
                    shootCone = 25f;
                    shootSound = Sounds.sap;
                    shoot = new ShootAlternate(){{
                        barrels = 2;
                        spread = 4f;
                    }};
                    bullet = new SapBulletType(){{
                        length = 80f;
                        damage = 60;
                        hitColor = color = Color.valueOf("bf92f9");
                        sapStrength = 0.6f;
                        despawnEffect = Fx.none;
                        shootEffect = Fx.shootSmall;
                    }};
                }}
            );

            abilities.add(new UnitResistanceAbility(legionnaire, 0.1f));

            float angle = 0f;
            int units = 1;
            for(int i = 0; i < units; i++){
                float x = Mathf.cos(angle) * 32;
                float y = Mathf.sin(angle) * 32;
                abilities.add(new UnitSpawnAbility(legionnaire, 600, x, y));
                angle += Mathf.PI2 / units;
            }

            //aiController = GroundBossAI::new;
        }};
        citadel = new BossUnitType("citadel", DamageAbsorbMechUnit.class){
            {
                health = 9000;
                armor = 20;
                absorption = 0.2f;
                hitSize = 40;
                rotateSpeed = 2f;
                speed = 0.2f;
                flying = false;
                canBoost = false;

                parts.add(
                    new RegionPart("-front"){{
                        growX = growY = -1;
                        //layer = Layer.groundUnit - 0.01f;
                        growProgress = p -> {
                            var unit = Groups.unit.find(u -> u.type.name.equals("fos-citadel"));
                            return unit == null || unit.health / unit.maxHealth > 0.5 ? 0 : 1;
                        };
                    }}
                );

                weapons.add(
                    new Weapon("fos-citadel-shotgun"){{
                        x = 18; y = 0;
                        rotate = true;
                        rotationLimit = 10f;
                        mirror = true;
                        alternate = true;
                        //layerOffset = -0.15f;
                        recoil = 4f;
                        recoilTime = 60f;
                        shake = 1f;
                        reload = 30f;
                        inaccuracy = 10f;
                        shoot.shots = 8;
                        bullet = new BasicBulletType(4f, 60f){{
                            lifetime = 40f;
                            width = 4f; height = 8f;
                            trailWidth = 2f;
                            trailLength = 12;
                            velocityRnd = 0.1f;
                        }};
                    }},
                    new Weapon("fos-citadel-stickybomb-launcher"){{
                        x = 8; y = -10;
                        rotate = true;
                        rotateSpeed = 1f;
                        mirror = true;
                        alternate = true;
                        recoil = 2f;
                        reload = 60f;
                        inaccuracy = 3f;
                        bullet = new StickyBulletType(4f, 20f, 180){{
                            lifetime = 60f;
                            width = height = 16f;
                            trailWidth = 3f;
                            trailLength = 12;
                            trailColor = Pal.plastaniumBack;
                            backColor = Pal.plastaniumBack;
                            frontColor = Pal.plastaniumFront;
                            ejectEffect = Fx.smokeCloud;
                            hitSound = Sounds.mud;
                            despawnEffect = Fx.explosion;
                            splashDamage = 60f;
                            splashDamageRadius = 16f;
                            collidesGround = collidesAir = true;
                            collidesTiles = true;
                        }};
                    }},

                    new Weapon(){
                        {
                            x = 0; y = 5;
                            rotate = false;
                            continuous = true;
                            shoot.firstShotDelay = 40f;
                            shootCone = 90f;
                            reload = 600f;
                            shake = 2f;

                            chargeSound = Sounds.lasercharge2;
                            shootSound = Sounds.laserbig;

                            bullet = new ContinuousLaserBulletType(60){{
                                length = 120;
                                width = 8f;
                                lifetime = 230f;
                                status = StatusEffects.melting;

                                incendChance = 0.2f;
                                incendSpread = 5f;
                                incendAmount = 1;

                                chargeEffect = new Effect(40f, 100f, e -> {
                                    color(Pal.lightFlame);
                                    stroke(e.fin() * 2f);
                                    Lines.circle(e.x, e.y, e.fout() * 50f);
                                }).followParent(true).rotWithParent(true);
                            }};
                        }

                        @Override
                        public void update(Unit unit, WeaponMount mount) {
                            // TODO: this copy of the super method is WAY too long
                            boolean can = unit.canShoot() && unit.health / unit.maxHealth < 0.5f;
                            float lastReload = mount.reload;
                            mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0);
                            mount.recoil = Mathf.approachDelta(mount.recoil, 0, unit.reloadMultiplier / recoilTime);
                            if(recoils > 0){
                                if(mount.recoils == null) mount.recoils = new float[recoils];
                                for(int i = 0; i < recoils; i++){
                                    mount.recoils[i] = Mathf.approachDelta(mount.recoils[i], 0, unit.reloadMultiplier / recoilTime);
                                }
                            }
                            mount.smoothReload = Mathf.lerpDelta(mount.smoothReload, mount.reload / reload, smoothReloadSpeed);
                            mount.charge = mount.charging && shoot.firstShotDelay > 0 ? Mathf.approachDelta(mount.charge, 1, 1 / shoot.firstShotDelay) : 0;

                            float warmupTarget = (can && mount.shoot) || (continuous && mount.bullet != null) || mount.charging ? 1f : 0f;
                            if(linearWarmup){
                                mount.warmup = Mathf.approachDelta(mount.warmup, warmupTarget, shootWarmupSpeed);
                            }else{
                                mount.warmup = Mathf.lerpDelta(mount.warmup, warmupTarget, shootWarmupSpeed);
                            }

                            //rotate if applicable
                            if(rotate && (mount.rotate || mount.shoot) && can){
                                float axisX = unit.x + Angles.trnsx(unit.rotation - 90,  x, y),
                                    axisY = unit.y + Angles.trnsy(unit.rotation - 90,  x, y);

                                mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
                                mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta);
                                if(rotationLimit < 360){
                                    float dst = Angles.angleDist(mount.rotation, baseRotation);
                                    if(dst > rotationLimit/2f){
                                        mount.rotation = Angles.moveToward(mount.rotation, baseRotation, dst - rotationLimit/2f);
                                    }
                                }
                            }else if(!rotate){
                                mount.rotation = baseRotation;
                                mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
                            }

                            float
                                weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : baseRotation),
                                mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                                mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y),
                                bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
                                bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY),
                                shootAngle = bulletRotation(unit, mount, bulletX, bulletY);

                            //find a new target
                            if(!controllable && autoTarget){
                                if((mount.retarget -= Time.delta) <= 0f){
                                    mount.target = findTarget(unit, mountX, mountY, bullet.range, bullet.collidesAir, bullet.collidesGround);
                                    mount.retarget = mount.target == null ? targetInterval : targetSwitchInterval;
                                }

                                if(mount.target != null && checkTarget(unit, mount.target, mountX, mountY, bullet.range)){
                                    mount.target = null;
                                }

                                boolean shoot = false;

                                if(mount.target != null){
                                    shoot = mount.target.within(mountX, mountY, bullet.range + Math.abs(shootY) + (mount.target instanceof Sized s ? s.hitSize()/2f : 0f)) && can;

                                    if(predictTarget){
                                        Vec2 to = Predict.intercept(unit, mount.target, bullet.speed);
                                        mount.aimX = to.x;
                                        mount.aimY = to.y;
                                    }else{
                                        mount.aimX = mount.target.x();
                                        mount.aimY = mount.target.y();
                                    }
                                }

                                mount.shoot = mount.rotate = shoot;

                                //note that shooting state is not affected, as these cannot be controlled
                                //logic will return shooting as false even if these return true, which is fine
                            }

                            if(alwaysShooting) mount.shoot = true;

                            //update continuous state
                            if(continuous && mount.bullet != null){
                                if(!mount.bullet.isAdded() || mount.bullet.time >= mount.bullet.lifetime || mount.bullet.type != bullet){
                                    mount.bullet = null;
                                }else{
                                    mount.bullet.rotation(weaponRotation + 90);
                                    mount.bullet.set(bulletX, bulletY);
                                    mount.reload = reload;
                                    mount.recoil = 1f;
                                    unit.vel.add(Tmp.v1.trns(unit.rotation + 180f, mount.bullet.type.recoil * Time.delta));
                                    if(shootSound != Sounds.none && !headless){
                                        if(mount.sound == null) mount.sound = new SoundLoop(shootSound, 1f);
                                        mount.sound.update(bulletX, bulletY, true);
                                    }

                                    if(alwaysContinuous && mount.shoot){
                                        mount.bullet.time = mount.bullet.lifetime * mount.bullet.type.optimalLifeFract * mount.warmup;
                                        mount.bullet.keepAlive = true;

                                        unit.apply(shootStatus, shootStatusDuration);
                                    }
                                }
                            }else{
                                //heat decreases when not firing
                                mount.heat = Math.max(mount.heat - Time.delta * unit.reloadMultiplier / cooldownTime, 0);

                                if(mount.sound != null){
                                    mount.sound.update(bulletX, bulletY, false);
                                }
                            }

                            //flip weapon shoot side for alternating weapons
                            boolean wasFlipped = mount.side;
                            if(otherSide != -1 && alternate && mount.side == flipSprite && mount.reload <= reload / 2f && lastReload > reload / 2f){
                                unit.mounts[otherSide].side = !unit.mounts[otherSide].side;
                                mount.side = !mount.side;
                            }

                            //shoot if applicable
                            if(mount.shoot && //must be shooting
                                can && //must be able to shoot
                                (!useAmmo || unit.ammo > 0 || !state.rules.unitAmmo || unit.team.rules().infiniteAmmo) && //check ammo
                                (!alternate || wasFlipped == flipSprite) &&
                                mount.warmup >= minWarmup && //must be warmed up
                                unit.vel.len() >= minShootVelocity && //check velocity requirements
                                (mount.reload <= 0.0001f || (alwaysContinuous && mount.bullet == null)) && //reload has to be 0, or it has to be an always-continuous weapon
                                (alwaysShooting || Angles.within(rotate ? mount.rotation : unit.rotation + baseRotation, mount.targetRotation, shootCone)) //has to be within the cone
                            ){
                                shoot(unit, mount, bulletX, bulletY, shootAngle);

                                mount.reload = reload;

                                if(useAmmo){
                                    unit.ammo--;
                                    if(unit.ammo < 0) unit.ammo = 0;
                                }
                            }
                        }
                    }
                );
            }
        };
        //TODO: campaign boss
        warden = new BossUnitType("warden", TankUnit.class){{
            health = 4500;
            armor = 15;
            hitSize = 36;
            speed = 0.2f;
            rotateSpeed = 0.75f;
            omniMovement = false;
            hidden = true; // TODO: unused

            treadPullOffset = 1;
            treadRects = new Rect[]{
                new Rect(36f, 24f - 73f, 24f, 73f),
                new Rect(30f, 80f - 37f, 18f, 37f)
            };

            weapons.add(new Weapon[]{
                new Weapon("fos-warden-artillery"){{
                    x = 16; y = -8;
                    rotate = true;
                    rotateSpeed = 3f;
                    top = true;
                    recoil = 2f;
                    shake = 2f;
                    reload = 75f;
                    shootCone = 18f;
                    mirror = alternate = true;
                    inaccuracy = 5f;
                    bullet = new ArtilleryBulletType(){{
                        width = 14f; height = 18f;
                        damage = 15f;
                        splashDamage = 60f;
                        splashDamageRadius = 20f;
                        speed = 2.5f;
                        lifetime = 100f;
                        trailWidth = 5f;
                        trailLength = 12;
                        trailEffect = Fx.artilleryTrail;
                        shootSound = Sounds.artillery;
                        hitEffect = despawnEffect = Fx.massiveExplosion;
                    }};
                }},
                new Weapon("fos-warden-artillery"){{
                    x = -12; y = -16;
                    rotate = true;
                    rotateSpeed = 3f;
                    top = true;
                    recoil = 2f;
                    shake = 2f;
                    reload = 75f;
                    shootCone = 18f;
                    mirror = alternate = true;
                    inaccuracy = 5f;
                    bullet = new ArtilleryBulletType(){{
                        width = 14f; height = 18f;
                        damage = 15f;
                        splashDamage = 60f;
                        splashDamageRadius = 20f;
                        speed = 2.5f;
                        lifetime = 100f;
                        trailWidth = 5f;
                        trailLength = 12;
                        trailEffect = Fx.artilleryTrail;
                        shootSound = Sounds.artillery;
                        hitEffect = despawnEffect = Fx.massiveExplosion;
                    }};
                }},

                new Weapon("fos-warden-machine-gun"){{
                    x = 14; y = 8;
                    rotate = false;
                    top = false;
                    baseRotation = 10f;
                    recoil = 4f;
                    shake = 0.5f;
                    reload = 7.5f;
                    shootCone = 15f;
                    mirror = true;
                    alternate = false;
                    inaccuracy = 4f;
                    bullet = new BasicBulletType(){{
                        width = 8f; height = 16f;
                        damage = 40f;
                        speed = 5f;
                        lifetime = 30f;
                        shootSound = Sounds.shoot;
                    }};
                }},
                new Weapon("fos-warden-machine-gun"){{
                    x = 18; y = 8;
                    rotate = false;
                    top = false;
                    baseRotation = 10f;
                    recoil = 4f;
                    shake = 0.5f;
                    reload = 7.5f;
                    shootCone = 15f;
                    mirror = true;
                    alternate = false;
                    inaccuracy = 4f;
                    bullet = new BasicBulletType(){{
                        width = 8f; height = 16f;
                        damage = 40f;
                        speed = 5f;
                        lifetime = 30f;
                        shootSound = Sounds.shoot;
                    }};
                }},

                new Weapon("fos-warden-antiair"){{
                    x = 0; y = -4;
                    rotate = true;
                    top = true;
                    rotateSpeed = 6f;
                    shootCone = 30f;
                    mirror = false;
                    reload = 120f;
                    shoot.shots = 4;
                    shoot.shotDelay = 10f;
                    inaccuracy = 4f;
                    bullet = new MissileBulletType(){{
                        width = 8f; height = 14f;
                        damage = 30f;
                        splashDamage = 90f;
                        splashDamageRadius = 28f;
                        speed = 8f;
                        lifetime = 30f;
                        collidesAir = true;
                        collidesGround = false;
                        shootSound = Sounds.missile;
                        hitEffect = Fx.blastExplosion;
                    }};
                }}
            });
            aiController = GroundAI::new;
        }};
        myriad = new BossUnitType("myriad", TankUnit.class){{
            hitSize = 47f;
            treadPullOffset = 1;
            speed = 0.48f;
            health = 25000;
            armor = 30f;
            omniMovement = false;
            crushDamage = 30f / 5f;
            rotateSpeed = 0.7f;
            hidden = true; // TODO: unused

            float xo = 231f/2f, yo = 231f/2f;
            treadRects = new Rect[]{new Rect(27 - xo, 152 - yo, 56, 73), new Rect(24 - xo, 51 - 9 - yo, 29, 17), new Rect(59 - xo, 18 - 9 - yo, 39, 19)};

            weapons.add(new Weapon("fos-myriad-point-weapon"){{
                shootSound = Sounds.bolt;
                reload = 45f;
                shootY = 7f;
                recoil = 2f;
                rotate = true;
                rotateSpeed = 1.4f;
                mirror = true;
                x = 15f;
                y = 15f;
                shadow = 20f;
                heatColor = Color.valueOf("ff7665");

                bullet = new RailBulletType(){{
                    damage = 50f;
                    length = 200f;
                    hitColor = Color.valueOf("ff7665");
                    endEffect = Fx.dynamicSpikes.wrap(Liquids.neoplasm.color, 15f);
                    shootEffect = Fx.shootBig2;
                    smokeEffect = Fx.colorSpark;
                    lineEffect = Fx.chainLightning;
                    hitEffect = Fx.blastExplosion;
                    splashDamage = 15f;
                    splashDamageRadius = 20f;
                    status = StatusEffects.blasted;
                }};
            }});
            weapons.add(new Weapon("fos-myriad-weapon"){{
                shoot = new ShootHelix(){{
                    mag = 1f;
                    scl = 3f;
                }};
                shootSound = Sounds.largeCannon;
                layerOffset = 0.1f;
                reload = 120f;
                shootY = 27.5f;
                shake = 5f;
                recoil = 5f;
                rotate = true;
                rotateSpeed = 0.6f;
                mirror = false;
                x = 0f;
                y = -2f;
                shadow = 50f;
                shootWarmupSpeed = 0.06f;
                cooldownTime = 110f;
                heatColor = Color.valueOf("ff7665");
                minWarmup = 0.9f;

                for(int i = 1; i <= 3; i++){
                    int fi = i;
                    parts.add(new RegionPart("-blade"){{
                        progress = PartProgress.warmup.delay((3 - fi) * 0.3f).blend(PartProgress.reload, 0.3f);
                        heatProgress = PartProgress.heat.add(0.3f).min(PartProgress.warmup);
                        heatColor = new Color(1f, 0.1f, 0.1f);
                        mirror = true;
                        under = true;
                        moveRot = -40f * fi;
                        moveX = 3f;
                        layerOffset = -0.002f;

                        x = 11 / 4f;
                    }});
                }

                bullet = new BasicBulletType(9f, 160f){{
                    sprite = "missile-large";
                    width = 12f;
                    height = 20f;
                    lifetime = 35f;
                    hitSize = 6f;

                    smokeEffect = Fx.shootSmokeTitan;
                    pierceCap = 4;
                    pierce = true;
                    keepVelocity = false;
                    pierceBuilding = true;
                    hitColor = backColor = trailColor = Color.valueOf("ff7665");
                    frontColor = Color.white;
                    trailWidth = 4f;
                    trailLength = 40;
                    hitEffect = despawnEffect = Fx.dynamicSpikes.wrap(Liquids.neoplasm.color, 20f);

                    shootEffect = Fx.shootBigSmoke;

                    fragBullets = 4;
                    fragSpread = 90f;
                    fragRandomSpread = 0f;
                    fragBullet = new SapBulletType(){{
                        sapStrength = 0.95f;
                        length = 100f;
                        damage = 10;
                        shootEffect = Fx.shootSmall;
                        hitColor = color = Color.valueOf("ff7665");
                        despawnEffect = Fx.none;
                        width = 0.35f;
                        lifetime = 15f;
                        knockback = -2f;
                    }};

                    bulletInterval = 3f;
                    intervalRandomSpread = 0f;
                    intervalAngle = 0f;
                    intervalBullets = 1;
                    intervalBullet = new BasicBulletType(1f, 25f){{
                        keepVelocity = false;
                        sprite = "mine-bullet";
                        width = height = 13f;
                        lifetime = 1f;
                        trailWidth = 4f;
                        trailLength = 10;
                        frontColor = Color.white;
                        trailColor = backColor = hitColor = Color.valueOf("ff7665");
                        hitEffect = despawnEffect = Fx.dynamicSpikes.wrap(Liquids.neoplasm.color, 12f);

                        fragBullets = 1;
                        fragRandomSpread = 0f;
                        fragAngle = 180f;
                        fragBullet = new SapBulletType(){{
                            sapStrength = 0.95f;
                            length = 125f;
                            damage = 22;
                            shootEffect = Fx.shootSmall;
                            hitColor = color = Color.valueOf("ff7665");
                            despawnEffect = Fx.none;
                            width = 0.35f;
                            lifetime = 15f;
                            knockback = -1f;
                        }};
                    }};
                }};
            }});
        }};

        lord = new LumoniPlayerUnitType("lord", LegsLumoniPlayerUnit.class){{
            health = 1200;
            armor = 3;
            hitSize = 10;
            speed = 1f;
            flying = false;
            canBoost = false;
            mineTier = 2;
            mineSpeed = 8f;
            buildSpeed = 1f;
            weapons.add(FOSWeaponModules.standard1.weapons);
        }};
        king = new LumoniPlayerUnitType("king", LegsLumoniPlayerUnit.class){{
            health = 2000;
            armor = 6;
            hitSize = 15;
            speed = 1.5f;
            flying = false;
            canBoost = false;
            mineTier = 4;
            mineSpeed = 10f;
            buildSpeed = 2f;
            weapons.add(FOSWeaponModules.standard2.weapons);
        }};

        sergeant = new TrailUnitType("sergeant", ElevationMoveUnit.class){{
            health = 720;
            armor = 4;
            hitSize = 12;
            speed = 2f;
            hovering = true;
            omniMovement = true;
            immunities.add(hacked);
            circleTarget = false;
            engineColorInner = FOSPal.hacked;
            engineColor = FOSPal.hackedBack;
            engineLayer = Layer.groundUnit - 0.1f;
            trailColor = FOSPal.hackedBack;
            trailLength = 32;
            trailType = ArrowFadeTrail.class;
            useEngineElevation = false;
            aiController = InjectorAI::new;

            parts.add(new HoverPart(){{
                x = 4f;
                y = 1f;
                mirror = true;
                radius = 4f;
                phase = 90f;
                stroke = 2f;
                layerOffset = -0.001f;
                color = FOSPal.hackedBack;
            }});

            weapons.add(
                new InjectorWeapon("fos-injector"){{
                    x = 0; y = 0;
                    reload = 45;
                    ejectEffect = Fx.casing1;
                    shootSound = Sounds.bolt;
                    bullet = new InjectorBasicBulletType(0, 0.5f, 200, 450, false){{
                        homingPower = 0.2f;
                        width = 4f; height = 6f;
                        damage = 60;
                        speed = 6f;
                        lifetime = 50f;
                        trailColor = FOSPal.hackedBack;
                        trailLength = 6;
                    }};
                }}
            );
        }};
        lieutenant = new TrailUnitType("lieutenant", ElevationMoveUnit.class){{
            health = 1600;
            armor = 6;
            hitSize = 16;
            speed = 1.6f;
            hovering = true;
            shadowElevation = 0.2f;
            omniMovement = true;
            aiController = InjectorAI::new;
            engineColorInner = FOSPal.hacked;
            engineColor = FOSPal.hackedBack;
            engineLayer = Layer.groundUnit - 0.1f;
            trailColor = FOSPal.hackedBack;
            trailLength = 32;
            trailType = ArrowFadeTrail.class;
            lightRadius = 16f;
            useEngineElevation = false;
            immunities.addAll(hacked, injected);

            parts.add(new HoverPart(){{
                x = 6f;
                y = 2f;
                mirror = true;
                radius = 8f;
                phase = 90f;
                stroke = 2f;
                layerOffset = -0.001f;
                color = FOSPal.hackedBack;
            }});

            weapons.add(
                new InjectorWeapon(){{
                    x = y = 0;
                    mirror = false;
                    reload = 40f;
                    inaccuracy = 8f;
                    shootCone = 15f;
                    rotate = false;
                    shootY = 4f;
                    shootSound = Sounds.spark;
                    bullet = new BasicBulletType(){{
                        width = height = 12f;
                        backColor = FOSPal.hackedBack;
                        frontColor = FOSPal.hacked;
                        speed = 2f; lifetime = 90f;
                        rotateSpeed = 9f;
                        homingPower = 0.2f;
                        homingDelay = 30f;

                        trailLength = 6;
                        trailWidth = 4;
                        trailColor = FOSPal.hackedBack;

                        intervalBullets = 8;
                        bulletInterval = 4f;
                        intervalBullet = new LightningBulletType(){{
                            lightningLength = 4;
                            lightningLengthRand = 2;
                            lightningColor = FOSPal.hacked;
                            status = injected;
                            statusDuration = 300f;
                            damage = 2;
                        }};

                        fragBullets = 2;
                        fragOnHit = true;
                        despawnHit = false;
                        fragBullet = new LightningBulletType(){{
                            lightningLength = 6;
                            lightningLengthRand = 4;
                            lightningColor = FOSPal.hacked;
                            status = injected;
                            statusDuration = 300f;
                            damage = 80;
                        }};

                        status = injected;
                        statusDuration = 300f;
                    }};
                }}
            );
        }};
        captain = new TrailUnitType("captain", UnitEntity.class){{
            health = 4200;
            armor = 8;
            absorption = 0.15f;
            hitSize = 20;
            speed = 0.3f;
            flying = true;
            engineColorInner = FOSPal.hacked;
            engineColor = FOSPal.hackedBack;
            engineLayer = Layer.groundUnit - 0.1f;
            trailColor = FOSPal.hackedBack;
            trailLength = 48;
            trailType = ArrowFadeTrail.class;
            useEngineElevation = false;
            aiController = InjectorAI::new;
            immunities.add(hacked);
            weapons.add(
                new InjectorWeapon("fos-injector-missile"){{
                    x = 9; y = 0;
                    top = true;
                    mirror = true;
                    alternate = false;
                    rotate = true;
                    reload = 30f;
                    inaccuracy = 12f;
                    shootSound = Sounds.missile;
                    bullet = new InjectorBasicBulletType(0f, 0.95f, 300, 2500, false){{
                        damage = 25f;
                        speed = 2.4f; lifetime = 90f;
                        width = 8f; height = 16f;
                        sprite = "missile";
                        backColor = FOSPal.hackedBack;
                        frontColor = FOSPal.hacked;
                        shrinkY = 0f;
                        homingPower = 0.06f;
                        weaveScale = 0.8f;
                        weaveMag = 1.8f;
                        hitSound = Sounds.explosion;
                        trailChance = 0.2f;
                        trailColor = FOSPal.hacked;
                    }};
                }},
                new InjectorWeapon("fos-injector-missile"){{
                    x = 6; y = 12;
                    top = true;
                    mirror = true;
                    alternate = false;
                    rotate = true;
                    reload = 300f;
                    shoot.shots = 4;
                    shoot.shotDelay = 10f;
                    inaccuracy = 12f;
                    shootSound = Sounds.missile;
                    bullet = new InjectorBasicBulletType(0f, 0.95f, 300, 2500, false){{
                        damage = 12.5f;
                        speed = 2.4f; lifetime = 90f;
                        width = 8f; height = 16f;
                        sprite = "missile";
                        backColor = FOSPal.hackedBack;
                        frontColor = FOSPal.hacked;
                        shrinkY = 0f;
                        homingPower = 0.06f;
                        weaveScale = 0.8f;
                        weaveMag = 1.8f;
                        hitSound = Sounds.explosion;
                        trailChance = 0.2f;
                        trailColor = FOSPal.hacked;
                    }};
                }}
            );
        }};

        radix = new FOSUnitType("radix", LegsUnit.class){{
            health = 1000;
            armor = 5;
            speed = 0.6f;
            hitSize = 12;
            rotateSpeed = 2f;
            targetAir = false;

            legCount = 3;
            legLength = 4.0F;
            legGroupSize = 1;
            lockLegBase = true;
            legContinuousMove = true;
            legExtension = -3.0F;
            legBaseOffset = 4.0F;
            legMaxLength = 1.1F;
            legMinLength = 0.16F;
            legLengthScl = 0.925F;
            legForwardScl = 0.9075F;
            legMoveSpace = 2F;

            weapons.add(
                    new Weapon("cp-e-weapon"){{
                        x = 0; y = 3;
                        recoil = 1f;
                        mirror = false;
                        layerOffset = -0.0001f;
                        reload = 30f;
                        rotate = false;
                        shootSound = Sounds.missile;
                        bullet = new BasicBulletType(){{
                            damage = 20;
                            lifetime = 45;
                            speed = 5f;
                            width = height = 10f;
                            collidesAir = false;

                            trailLength = 12;
                            trailWidth = 3;
                            trailEffect = Fx.artilleryTrail;
                            trailColor = backColor = Pal.surge;
                            frontColor = Color.white;

                            fragBullets = 3;
                            fragOnHit = true;
                            fragBullet = new LightningBulletType(){{
                                damage = 10;
                                lightningLength = 3;
                                lightningLengthRand = 2;
                            }};
                        }};
                    }}
            );
        }};
        foetus = new FOSUnitType("foetus", LegsUnit.class){{
            health = 1950;
            armor = 5;
            speed = 0.4f;
            hitSize = 18;
            rotateSpeed = 1.8f;
            targetAir = false;

            legCount = 3;
            legLength = 6.0F;
            legGroupSize = 1;
            lockLegBase = true;
            legContinuousMove = true;
            legExtension = -3.0F;
            legBaseOffset = 4.0F;
            legMaxLength = 1.1F;
            legMinLength = 0.2F;
            legLengthScl = 0.925F;
            legForwardScl = 0.9075F;
            legMoveSpace = 1.085F;

            abilities.add(new EnergyFieldAbility(20f, 90f, 90f){{
                x = 0; y = -2;
                statusDuration = 120f;
                maxTargets = 10;
                color = Pal.surge;
                effectRadius = 3f;
                healPercent = 2f;
                healEffect = hitEffect = Fx.blastExplosion;
                damageEffect = Fx.chainLightning;
                //buildingDamageMultiplier = 1.25f;
            }});

            weapons.add(new PointDefenseWeapon("cp-e-point-defense-small"){{
                x = 9; y = -3;
                mirror = true;
                rotate = true;
                rotateSpeed = 10f;
                reload = 12f;

                targetInterval = 5f;
                targetSwitchInterval = 10f;
                recoil = 0.2f;

                bullet = new BulletType(){{
                    shootSound = Sounds.lasershoot;
                    shootEffect = Fx.sparkShoot;
                    hitEffect = Fx.pointHit;
                    maxRange = 175f;
                    damage = 10f;
                }};
            }});
        }};
        vitarus = new FOSUnitType("vitarus", DamageAbsorbLegsUnit.class){{
            health = 3100;
            armor = 7;
            absorption = 0.1f;
            speed = 0.35f;
            hitSize = 22;
            rotateSpeed = 1f;

            legCount = 4;
            legLength = 10.0F;
            legGroupSize = 2;
            lockLegBase = true;
            legContinuousMove = true;
            legExtension = -3.0F;
            legBaseOffset = 5.0F;
            legMaxLength = 1.1F;
            legMinLength = 0.2F;
            legLengthScl = 0.925F;
            legForwardScl = 0.9075F;
            legMoveSpace = 1.085F;

            weapons.add(
                    new Weapon("cp-e-railgun"){{
                        x = 9.5f; y = 4f;
                        recoil = 3f;
                        rotate = true;
                        rotateSpeed = 1.25f;
                        rotationLimit = 30f;
                        layerOffset = -0.0001f;
                        mirror = true;
                        reload = 40f;
                        shootSound = Sounds.shootSmite;

                        bullet = new RailBulletType(){{
                            pierceCap = 1;
                            pierce = pierceBuilding = true;
                            collidesAir = false;
                            damage = 75f;
                            //buildingDamageMultiplier = 0.75f;
                            length = 100f;
                            hitColor = Pal.surge;
                            hitEffect = endEffect = Fx.dynamicSpikes.wrap(Pal.surge, 16f);
                            shootEffect = Fx.shootBig2;
                            smokeEffect = Fx.colorSpark;
                            lineEffect = Fx.chainLightning;
                            hitEffect = Fx.blastExplosion;
                            status = StatusEffects.blasted;

                            fragBullets = 3;
                            fragOnHit = true;
                            fragRandomSpread = 0f;
                            fragBullet = new LightningBulletType(){{
                                damage = 20;
                                //buildingDamageMultiplier = 0.6f;
                                lightningLength = 7;
                                lightningLengthRand = 7;
                                lightningColor = hitColor = Pal.surge;
                            }};
                        }};
                    }},
                    new PointDefenseWeapon("cp-e-point-defense-small"){{
                        x = 30f/4f; y = -25f/4f;
                        mirror = true;
                        rotate = true;
                        rotateSpeed = 10f;
                        reload = 9f;

                        targetInterval = 5f;
                        targetSwitchInterval = 5f;
                        recoil = 0.2f;

                        bullet = new BulletType(){{
                            shootSound = Sounds.lasershoot;
                            shootEffect = Fx.sparkShoot;
                            hitEffect = Fx.pointHit;
                            maxRange = 160f;
                            damage = 25f;
                        }};
                    }}
            );
        }};

/*
        testOverdrive = new UnitType("test-overdrive"){{
            health = 360;
            hitSize = 12;
            rotateSpeed = 10f;
            flying = true;
            speed = 1.2f;
            hidden = true; // TODO: unused
        }};
*/

        assault = new FOSUnitType("assault", ElevationMoveUnit.class){{
            health = 800;
            //armor = 2f;
            hitSize = 9f;
            hovering = true;
            shadowElevation = 0.1f;
            drag = 0.07f;
            speed = 1.8f;
            rotateSpeed = 5f;
            engineOffset = 7f;
            engineSize = 2f;
            itemCapacity = 0;

            abilities.add(new MoveEffectAbility(0f, -7f, Pal.sapBulletBack, Fx.missileTrailShort, 4f) {{
                teamColor = true;
            }});

            parts.add(new HoverPart() {{
                x = 3.9f;
                y = -4;
                mirror = true;
                radius = 6f;
                phase = 90f;
                stroke = 2f;
                layerOffset = -0.001f;
                color = Color.valueOf("bf92f9");
            }});
            weapons.add(
                new Weapon("fos-assault-dumpster-device"){{
                    //TODO: make a burst rocket launcher with 6 missiles later
                    x = 3; y = 0;
                    rotate = false;
                    mirror = alternate = true;
                    shoot.shots = 2;
                    shoot.shotDelay = 20;
                    reload = 50f;
                    bullet = new MissileBulletType(){{
                        damage = 50;
                        speed = 5; lifetime = 40;
                        homingPower = 0.1f;
                        homingDelay = 10f;
                        backColor = trailColor = Pal.sapBulletBack;
                        frontColor = Pal.reactorPurple2;
                        trailLength = 12;
                        buildingDamageMultiplier = 0.5f;
                    }};
                }}
            );
        }};
        abrupt = new FOSUnitType("abrupt", ElevationMoveUnit.class){{
            health = 800;
            armor = 4;
            speed = 1.5f;
            hovering = true;
            drag = 0.06f;
            accel = 0.2f;
            rotateSpeed = 5f;

            abilities.add(new MoveEffectAbility(0f, -7f, Pal.sapBulletBack, Fx.missileTrailShort, 4f) {{
                teamColor = true;
            }});

            parts.add(new HoverPart() {{
                //TODO: waiting for sprites ...
                x = 3.9f;
                y = -4;
                mirror = true;
                radius = 6f;
                phase = 90f;
                stroke = 2f;
                layerOffset = -0.001f;
                color = Color.valueOf("bf92f9");
            }});

            weapons.add(
                new Weapon("fos-abrupt-bomber"){{
                    x = 0; y = -5;
                    mirror = false;
                    reload = 240f;
                    shoot.shots = 5;
                    inaccuracy = 11.25f;
                    shootSound = Sounds.artillery;
                    bullet = new BasicBulletType(){{
                        width = height = 12;
                        speed = 3f;
                        lifetime = 60f;
                        hitEffect = Fx.fallSmoke;
                        backColor = trailColor = Pal.sapBulletBack;
                        frontColor = Pal.reactorPurple2;
                        trailLength = 4;
                        buildingDamageMultiplier = 0.3f;

                        fragOnHit = false;
                        fragBullets = 1;
                        fragRandomSpread = 0;
                        fragBullet = new BasicBulletType(){{
                            width = height = 12;
                            shrinkX = shrinkY = -1;
                            sprite = "shell";
                            speed = 1f;
                            drag = 0.0083f;
                            lifetime = 60f;
                            splashDamage = 200;
                            splashDamageRadius = 16f;
                            buildingDamageMultiplier = 0.3f;
                            backColor = Pal.sapBulletBack;
                            frontColor = Pal.reactorPurple2;
                            hitEffect = Fx.massiveExplosion;
                            hitSound = Sounds.boom;
                        }};
                    }};
                }}
            );
        }};
        brunt = new FOSUnitType("brunt", UnitEntity.class){{
            health = 1500;
            armor = 22.5f;
            speed = 0.8f;
            hitSize = 24f;
            rotateSpeed = 2f;
            flying = true;

            abilities.add(new DamageFieldAbility(120f, 1f){{
                borderColor = Pal.sapBulletBack.cpy().a(0.4f);
            }});

            weapons.add(
                new Weapon(){{
                    shootCone = 360f;
                    range = 32f;
                    shoot.firstShotDelay = 300f;
                    bullet = new BombBulletType(){{
                        splashDamage = 1000f;
                        splashDamageRadius = 80f;
                        status = StatusEffects.slow;
                        statusDuration = 300f;
                        hittable = false;
                        killShooter = true;
                        instantDisappear = true;
                        hitEffect = Fx.reactorExplosion;
                        buildingDamageMultiplier = 0.1f;
                    }};
                }}
            );
        }};

/*
        vulture = new CarrierUnitType("vulture"){{
            health = 400;
            speed = 2f;
            hitSize = 16f;
            payloadCapacity = 8f * 8f;
            engineOffset = 10f;
            flying = true;
            omniMovement = true;
            lowAltitude = true;
            hidden = true; // TODO: unused
            controller = u -> new CarrierAI();
        }};
*/

        bugSmall = new BugUnitType("bug-small", BugCrawlUnit.class, false){{
            health = 160;
            armor = 8;
            hitSize = 16f;
            speed = 0.3f;
            segments = 3;
            crushDamage = 0.2f;

            //copied from renale for now
            segmentScl = 3f;
            segmentPhase = 5f;
            segmentMag = 0.5f;
        }};

        bugMedium = new BugUnitType("bug-medium", BugCrawlUnit.class, false){{
            health = 480;
            armor = 15;
            hitSize = 18f;
            speed = 0.48f;
            segments = 4;
            crushDamage = 0.6f;

            //copied from renale too, he's the same size for some reason
            segmentScl = 3f;
            segmentPhase = 5f;
            segmentMag = 0.5f;
        }};

        bugFlyingSmall = new BugUnitType("bug-flying-small", BugUnit.class, true, true){{
            health = 60;
            armor = 1;
            hitSize = 6f;
            speed = 2f;
            rotateSpeed = 6f;
            range = 10f;
            circleTarget = true;
            hidden = true; // TODO: unused
            weapons.add(
                new Weapon(){{
                    x = 0f; y = 5f;
                    reload = 150f;
                    shootSound = Sounds.rockBreak;
                    rotate = false;
                    shootWarmupSpeed = 0.3f;
                    minWarmup = 0.9f;
                    parts.addAll(
                        new RegionPart("-stinger"){{
                            x = 0f; y = -4f;
                            moveX = 0f; moveY = 8f;
                            layer = Layer.legUnit + 0.01f;
                        }}
                    );
                    bullet = new ExplosionBulletType(75, 2){{
                        killShooter = false;
                        collidesAir = collidesGround = true;
                        hitSound = Sounds.none;
                        despawnSound = Sounds.none;
                    }};
                }}
            );
        }};
        bugFlyingMedium = new BugUnitType("bug-flying-medium", BugUnit.class, true){{
            health = 200;
            armor = 2;
            hitSize = 12f;
            speed = 0.8f;
            rotateSpeed = 2f;
            hidden = true; // TODO: unused
            weapons.add(
                new Weapon(){{
                    x = 0f; y = 6f;
                    reload = 180f;
                    shootSound = Sounds.mud;
                    rotate = false;
                    shoot = new ShootSpread(){{
                        shots = 3;
                        spread = 20f;
                    }};
                    bullet = new LiquidBulletType(){{
                        damage = 60f;
                        speed = 1.33f;
                        lifetime = 60f;
                        collidesAir = collidesGround = true;
                        trailColor = Liquids.slag.color;
                        trailWidth = 3f;
                        trailLength = 14;
                        liquid = Liquids.slag;
                    }};
                }}
            );
        }};

        draug = new FOSUnitType("draug", BuildingTetherMinerUnit.class){{
            health = 110;
            armor = 2;
            hitSize = 10f;
            flying = true;
            drag = 0.06f;
            accel = 0.12f;
            speed = 1.8f;
            isEnemy = false;
            useUnitCap = false;

            mineItems = Seq.with(zinc);
            mineTier = 2;
            mineSpeed = 1.2f;

            controller = u -> new MinerAI();
            defaultCommand = UnitCommand.mineCommand;
            playerControllable = false;

            // you have incurred my wrath. prepare to die.
        }};

        weaponSetInit = new FOSUnitType("weapon-set-init", UnitEntity.class){{
            hidden = true;
            internal = true;
            for (var s : WeaponSet.sets) {
                weapons.add(s.weapons);
            }
        }};

        //TODO
/*
        subSmall = new SubmarineUnitType("sub-small"){{
            health = 250;
            speed = 0.7f;
            hitSize = 12f;
            hidden = true; // TODO: unused
            weapons.add(new Weapon("fos-sub-missile-launcher"){{
                reload = 22f;
                x = 0f;
                y = 1f;
                top = true;
                rotate = true;
                mirror = false;
                ejectEffect = Fx.casing1;
                bullet = new MissileBulletType(3f, 5){{
                    width = 8f;
                    height = 11f;
                    lifetime = 36f;
                }};
            }});
        }};
*/
    }
}
