package fos.content;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Rect;
import arc.struct.Seq;
import fos.ai.*;
import fos.ai.bugs.*;
import fos.audio.FOSSounds;
import fos.gen.*;
import fos.graphics.*;
import fos.type.abilities.UnitResistanceAbility;
import fos.type.bullets.*;
import fos.type.content.WeaponSet;
import fos.type.units.types.*;
import fos.type.units.weapons.*;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.ElevationMoveUnit;
import mindustry.gen.LegsUnit;
import mindustry.gen.MechUnit;
import mindustry.gen.TankUnit;
import mindustry.gen.UnitEntity;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.weapons.*;
import mindustry.world.meta.BlockFlag;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static ent.anno.Annotations.EntityDef;
import static fos.content.FOSFluids.bugAcid;
import static fos.content.FOSItems.*;
import static fos.content.FOSStatuses.*;
import static mindustry.content.Items.copper;

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

    public static @EntityDef({Unitc.class, LumoniPlayerc.class, Legsc.class}) UnitType
        // PLAYER UNITS
        lord, king;

    public static @EntityDef({Unitc.class, Crawlc.class, Bugc.class}) UnitType
        // CRAWLING INSECTS
        bugSmall, bugMedium, terrapod;

    public static @EntityDef({Unitc.class, Legsc.class, Bugc.class}) UnitType
        // LEGS BUGS
        bugSmallSpitter;

    public static @EntityDef({Unitc.class, Bugc.class}) UnitType
        // FLYING INSECTS
        bugFlyingSmall, bugFlyingMedium;

    public static @EntityDef({Unitc.class, Minerc.class, BWorkerc.class}) UnitType
        // FLYING MINER INSECTS
        bugWorker;

    public static @EntityDef({Unitc.class}) UnitType
        // SCOUT INSECT - DIFFERENT UNIT ENTITY DUE TO UNIQUE BEHAVIOUR
        bugScout;

    public static @EntityDef({Unitc.class, Legsc.class, Burrowc.class, Bugc.class}) UnitType
        // BURROWING INSECTS
        grain;

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
                            hitSound = FOSSounds.sticky;
                            despawnEffect = Fx.explosion;
                            splashDamage = 60f;
                            splashDamageRadius = 16f;
                            collidesGround = collidesAir = true;
                            collidesTiles = true;
                        }};
                    }},

                    new HealthTriggerWeapon(){{
                        x = 0; y = 5;
                        rotate = false;
                        continuous = true;
                        shoot.firstShotDelay = 40f;
                        shootCone = 90f;
                        reload = 600f;
                        shake = 2f;
                        healthFrac = 0.5f;

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

                        parts.add(
                            new RegionPart("fos-citadel-front"){{
                                x = 0; y = 3;
                                growX = growY = -1;
                                //layer = Layer.groundUnit - 0.01f;
                                growProgress = p -> {
                                    var unit = Groups.unit.find(u -> u.type.name.equals("fos-citadel"));
                                    return unit == null || unit.health / unit.maxHealth > 0.5 ? 0 : 1;
                                };
                            }}
                        );
                    }}
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
            speed = 0.6f;
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
                new Weapon("fos-injector-missile"){{
                    x = 6; y = -6;
                    top = true;
                    mirror = true;
                    alternate = false;
                    rotate = true;
                    reload = 30f;
                    inaccuracy = 12f;
                    shootSound = Sounds.missile;
                    bullet = new MissileBulletType(){{
                        damage = 25f;
                        speed = 2.4f; lifetime = 90f;
                        width = 8f; height = 16f;
                        backColor = FOSPal.hackedBack;
                        frontColor = FOSPal.hacked;
                        homingPower = 0.06f;
                        hitSound = Sounds.explosion;
                        trailChance = 0.2f;
                        trailColor = FOSPal.hacked;
                    }};
                }},
                new InjectorWeapon("fos-hack-beamer"){{
                    x = 0; y = 6;
                    mirror = false;
                    rotate = true;
                    rotateSpeed = 0.75f;
                    continuous = true;
                    reload = 10f;
                    targetSwitchInterval = 60f;
                    controllable = false;
                    autoTarget = true;
                    rotationLimit = 45f;
                    shootSound = Sounds.none;
                    bullet = new InjectorHackLaserBulletType(){{
                        minHP = 500;
                        maxHP = 1800;
                        smokeEffect = Fx.none;
                        shootEffect = Fx.none;
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
                    new Weapon("fos-e-weapon"){{
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

            weapons.add(new PointDefenseWeapon("fos-e-point-defense-small"){{
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
                    new Weapon("fos-e-railgun"){{
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
                            pierceCap = 2;
                            pierce = pierceBuilding = true;
                            collidesAir = false;
                            damage = 75f;
                            //buildingDamageMultiplier = 0.75f;
                            length = 100f;
                            hitColor = Pal.surge;
                            endEffect = Fx.dynamicSpikes.wrap(Pal.surge, 16f);
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
                    new PointDefenseWeapon("fos-e-point-defense-small"){{
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
            health = 1600;
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

                        fragOnHit = true;
                        fragBullets = 1;
                        fragRandomSpread = 0;
                        fragBullet = new BasicBulletType(){{
                            width = height = 12;
                            shrinkX = shrinkY = -0.5f;
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
            health = 2800;
            armor = 20f;
            speed = 1.4f;
            hitSize = 24f;
            rotateSpeed = 2f;
            flying = true;
            accel = 0.05f;
            drag = 0.03f;

            engineOffset = 12f;
            engineSize = 4f;
            engineColor = Pal.reactorPurple2;
            engineColorInner = Pal.reactorPurple;
            engines.add(
                new UnitEngine(10f, -9f, 2f, -60f),
                new UnitEngine(-10f, -9f, 2f, -120f)
            );

            weapons.add(
                new Weapon(){{
                    x = 0; y = 6;
                    reload = 15f;
                    shootCone = 25f;
                    mirror = false;

                    shoot = new ShootAlternate(){{
                        spread = 2f;
                    }};

                    shootSound = Sounds.missile;

                    bullet = new MissileBulletType(){{
                        speed = 4f; lifetime = 30f;
                        damage = 70f;

                        weaveMag = 1.2f;
                        weaveScale = 4f;

                        backColor = trailColor = Pal.reactorPurple;
                        frontColor = Pal.reactorPurple2;
                        trailChance = 0f;
                        trailWidth = 1f;
                        trailLength = 12;
                        smokeEffect = new Effect(20f, e -> {
                            color(Pal.reactorPurple, Pal.reactorPurple2, e.fin());

                            randLenVectors(e.id, 5, e.finpow() * 6f, e.rotation, 20f, (x, y) -> {
                                Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f);
                            });
                        });

                        status = StatusEffects.slow;
                        statusDuration = 300f;
                    }};
                }},
                new HealthTriggerWeapon(){{
                    x = 0; y = 0;
                    alwaysShooting = true;
                    healthFrac = 0.4f;
                    reload = 600f;
                    mirror = false;

                    shoot.firstShotDelay = 300f;

                    parentizeEffects = true;
                    shootSound = Sounds.none;

                    bullet = new BombBulletType(){{
                        splashDamage = 1200f;
                        splashDamageRadius = 80f;
                        hittable = false;
                        killShooter = true;
                        instantDisappear = true;
                        buildingDamageMultiplier = 0.1f;

                        shootStatus = StatusEffects.unmoving;
                        shootStatusDuration = 300f;

                        hitEffect = Fx.reactorExplosion;
                        hitSound = Sounds.largeExplosion;
                        chargeEffect = FOSFx.bruntCharge;
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

        bugSmall = new BugUnitType("bug-small", BugCrawlUnit.class, false, true){{
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

            firstRequirements = ItemStack.with(zinc, 5);
        }};

        bugMedium = new BugUnitType("bug-medium", BugCrawlUnit.class, false, true){{
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

            firstRequirements = ItemStack.with(zinc, 5, diamond, 3);
        }};

        // TODO
        terrapod = new BugUnitType("terrapod", BugCrawlUnit.class, false){{
            health = 14000;
            armor = 12;
            absorption = 0.75f;
            speed = 0.6f;
            segments = 18;
            crushDamage = 1f;

            segmentScl = 8f;
            segmentMag = 6f;
        }};

        bugSmallSpitter = new BugUnitType("bug-small-spitter", BugLegsUnit.class, false){{
            health = 600;
            armor = 0;
            absorption = 0;
            speed = 0.6f;
            hitSize = 8;

            legCount = 6;

            weapons.add(
                new Weapon(){{
                    x = 0; y = 6;
                    reload = 150f;
                    recoil = 0;
                    mirror = false;
                    rotate = false;
                    shootCone = 20f;
                    shootSound = FOSSounds.spit;
                    bullet = new LiquidBulletType(bugAcid){{
                        speed = 4; lifetime = 37.5f;
                        damage = 40;
                        puddleSize = 15f;
                        orbSize = 3f;
                        despawnHit = true;

                        trailLength = 8;
                        trailWidth = 3f;
                        trailColor = bugAcid.color;
                    }};
                }}
            );

            firstRequirements = ItemStack.with(copper, 5);
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

        bugWorker = new BugUnitType("bug-worker", BWorkerMinerUnit.class){{
            health = 140;
            absorption = 0.1f;
            speed = 0.6f;
            //drag = 0.08f;
            //accel = 0.12f;
            flying = true;

            engineSize = 0;
            itemOffsetY = -3f;

            isEnemy = false;

            itemCapacity = 1;
            mineSpeed = 0.2f;
            mineTier = 2;
            mineRange = 4f;
            mineItems = Seq.with(zinc, copper);

            controller = u -> new MinerBugAI();
            defaultCommand = UnitCommand.mineCommand;
            playerControllable = false;
        }};

        bugScout = new BugUnitType("bug-scout", UnitEntity.class){{
            health = 120;
            speed = 1.4f;
            flying = true;
            drag = 0.06f;
            accel = 0.04f;
            omniMovement = false;
            fogRadius = 22.5f;
            drawBody = true;

            firstRequirements = ItemStack.with(zinc, 1, copper, 1);

            controller = u -> new ScoutBugAI();
        }};

        grain = new BurrowUnitType("grain", BugBurrowLegsUnit.class){{
            health = 900;
            absorption = 0.15f;
            hitSize = 40f;
            speed = 0.3f;
            rotateSpeed = 2f;
            targetAir = false;
            targetGround = true;
            targetFlags = new BlockFlag[]{BlockFlag.unitCargoUnloadPoint, BlockFlag.core, null};

            legCount = 4;
            legLength = 24;
            legBaseOffset = 12;
            legExtension = 3.25f;
            legStraightness = 0.5f;

            parts.addAll(
                new RegionPart(),
                new RegionPart("-armor"){{
                    x = 0; y = -12;
                    layerOffset = 0.01f;
                }}
            );

            weapons.add(
                new Weapon(){{
                    x = 8; y = 20;
                    reload = 300f;
                    rotate = false;
                    alternate = false;
                    ejectEffect = Fx.none;
                    recoil = 0;
                    shootX = -8f;
                    shootCone = 360f;
                    shootStatus = StatusEffects.unmoving;
                    shootStatusDuration = 600f;
                    shoot.firstShotDelay = 40f;
                    cooldownTime = 260f;

                    parts.addAll(
                        new RegionPart("fos-grain-claw-base"){{
                            x = 6; y = -6;
                            rotation = 45;
                            moveX = -6; moveY = -6; moveRot = -45;
                            progress = p -> p.heat > 0 ? 0 : Mathf.slope(Interp.pow4In.apply(p.charge));
                            layer = Layer.groundUnit - 0.01f;
                        }},
                        new RegionPart("fos-grain-claw"){{
                            x = 6; y = 4;
                            rotation = 120;
                            moveX = 6; moveY = -8; moveRot = -75;
                            progress = p -> p.heat > 0 ? 0 : Mathf.slope(Interp.pow4In.apply(p.charge));
                        }}
                    );

                    bullet = new ExplosionBulletType(){{
                        shootEffect = Fx.none;
                        hitEffect = Fx.none;
                        despawnEffect = Fx.none;
                        smokeEffect = Fx.none;
                        shootSound = Sounds.largeExplosion;
                        splashDamage = 600f;
                        splashDamageRadius = 4f;
                        splashDamagePierce = true;
                        killShooter = false;
                        rangeOverride = 1f;
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
    }
}
