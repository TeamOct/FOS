package fos.content;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Rect;
import fos.ai.*;
import fos.gen.*;
import fos.graphics.FOSPal;
import fos.type.abilities.*;
import fos.type.bullets.*;
import fos.type.units.*;
import fos.type.units.weapons.InjectorWeapon;
import mindustry.ai.types.GroundAI;
import mindustry.annotations.Annotations;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.weapons.RepairBeamWeapon;

import static fos.content.FOSStatuses.hacked;

public class FOSUnitTypes {
    public static @Annotations.EntityDef({Mechc.class}) UnitType
        // MECH BOSSES
        legion, citadel;

    public static @Annotations.EntityDef({Tankc.class}) UnitType
        // TANK BOSSES
        warden;

    public static @Annotations.EntityDef({FOSHovercraftc.class, Unitc.class}) UnitType
        // HOVERCRAFT (INJECTORS)
        sergeant, lieutenant, captain,

        // HOVERCRAFT (DESTROYERS)
        assault, abrupt;

    public static @Annotations.EntityDef({Unitc.class}) UnitType
        // FLYING (LEGION SUMMONS)
        legionnaire, legionnaireReplica,

        // FLYING (DESTROYERS)
        brunt;

    public static @Annotations.EntityDef({Legsc.class}) UnitType
        // LEGS (ELIMINATORS)
        radix, foetus, vitarus;

    public static @Annotations.EntityDef({Payloadc.class, Unitc.class}) UnitType
        // PAYLOAD
        vulture;

    public static @Annotations.EntityDef({BuildingTetherc.class, Payloadc.class}) UnitType
        // TETHER
        testOverdrive;

    //TODO submarines
    public static @Annotations.EntityDef({Submarinec.class}) UnitType
        // SUBMARINES TODO
        subSmall;

    public static @Annotations.EntityDef({LumoniPlayerUnitc.class, Legsc.class}) UnitType
        // PLAYER UNITS
        lord, king;

    public static @Annotations.EntityDef({Bugc.class, Crawlc.class}) UnitType
        // CRAWLING INSECTS
        bugSmall, bugMedium;

    public static @Annotations.EntityDef({Bugc.class, Unitc.class}) UnitType
        // FLYING INSECTS
        bugFlyingSmall, bugFlyingMedium;

    public static void load(){
        //DestroyersUnits.load();

        legionnaire = new FOSUnitType("legionnaire"){{
            health = 100;
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
                    bullet = new BasicBulletType(2.5f, 22.75f){{
                        width = 7f;
                        height = 9f;
                        lifetime = 45f;
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
        legionnaireReplica = new FOSUnitType("legionnaire-replica"){{
            health = 75;
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
                    bullet = new BasicBulletType(2.5f, 22.75f){{
                        width = 7f;
                        height = 9f;
                        lifetime = 45f;
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
        legion = new BossUnitType("legion"){{
            health = 2250;
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
                        damage = 30;
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
        //TODO: campaign boss
        citadel = new BossUnitType("citadel"){{
            health = 3750;
            armor = 30;
            hitSize = 40;
            rotateSpeed = 2f;
            speed = 0.05f;
            flying = false;
            canBoost = false;
            weapons.add(
                new Weapon("fos-citadel-shotgun"){{
                    x = 18; y = 0;
                    rotate = false;
                    mirror = true;
                    alternate = true;
                    //layerOffset = -0.15f;
                    recoil = 4f;
                    recoilTime = 60f;
                    reload = 30f;
                    inaccuracy = 10f;
                    shoot.shots = 6;
                    bullet = new BasicBulletType(4f, 15f){{
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
                    reload = 90f;
                    inaccuracy = 3f;
                    bullet = new StickyBulletType(2f, 1f, 180){{
                        lifetime = 120f;
                        width = height = 16f;
                        trailWidth = 3f;
                        trailLength = 12;
                        trailColor = Pal.plastaniumBack;
                        backColor = Pal.plastaniumBack;
                        frontColor = Pal.plastaniumFront;
                        ejectEffect = Fx.smokeCloud;
                        hitSound = Sounds.mud;
                        despawnEffect = Fx.explosion;
                        splashDamage = 40f;
                        splashDamageRadius = 16f;
                        collidesGround = collidesAir = true;
                        collidesTiles = true;
                    }};
                }}
            );
        }};
        //TODO: campaign boss
        warden = new BossUnitType("warden"){{
            health = 4500;
            armor = 15;
            hitSize = 36;
            speed = 0.2f;
            rotateSpeed = 0.75f;
            omniMovement = false;
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

        lord = new LumoniPlayerUnitType("lord"){{
            health = 1200;
            armor = 3;
            hitSize = 10;
            speed = 0.8f;
            flying = false;
            canBoost = false;
            mineTier = 2;
            mineSpeed = 8f;
            buildSpeed = 1f;
            weapons.add(FOSWeaponModules.standard1.weapons);
        }};
        king = new LumoniPlayerUnitType("king"){{
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

        sergeant = new FOSUnitType("sergeant"){{
            health = 75;
            hitSize = 12;
            speed = 1.2f;
            hovering = true;
            omniMovement = true;
            immunities.add(hacked);
            circleTarget = true;
            trailColor = FOSPal.hackedBack;
            trailLength = 12;
            aiController = InjectorAI::new;
            weapons.add(
                new InjectorWeapon("fos-injector"){{
                    x = 0; y = 0;
                    reload = 60;
                    ejectEffect = Fx.casing1;
                    shootSound = Sounds.bolt;
                    bullet = new InjectorBasicBulletType(0, 0.3f, 25, 200, false){{
                        homingPower = 0.2f;
                        width = 4f; height = 6f;
                        damage = 10;
                        speed = 6f;
                        lifetime = 50f;
                        trailColor = FOSPal.hackedBack;
                        trailLength = 6;
                    }};
                }}
            );
        }};
        lieutenant = new FOSUnitType("lieutenant"){{
            health = 360;
            hitSize = 16;
            speed = 2.4f;
            hovering = true;
            omniMovement = true;
            aiController = InjectorAI::new;
            trailColor = FOSPal.hackedBack;
            trailLength = 24;
            immunities.add(hacked);
            weapons.add(
                new InjectorWeapon(){{
                    x = y = 0;
                    mirror = false;
                    rotate = true;
                    reload = 120f;
                    bullet = new InjectorBlastBulletType(0.1f, false){{
                        splashDamage = 10f;
                        splashDamageRadius = 16f;
                    }};
                }}
            );
        }};
        captain = new FOSUnitType("captain"){{
            health = 900;
            hitSize = 20;
            speed = 1.1f;
            hovering = true;
            trailColor = FOSPal.hackedBack;
            trailLength = 8;
            aiController = InjectorAI::new;
            immunities.add(hacked);
            weapons.add(
                new InjectorWeapon("fos-injector-missile"){{
                    x = 9; y = 0;
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

        radix = new FOSUnitType("radix"){{
            health = 300;
            armor = 1;
            speed = 0.8f;
            hitSize = 12;
            rotateSpeed = 2f;
            targetAir = false;

            legCount = 3;
            legLength = 8f;
            legContinuousMove = true;
            legExtension = -2f;
            legBaseOffset = 3f;
            legMaxLength = 1.1f;
            legMinLength = 0.2f;
            legLengthScl = 0.96f;
            legForwardScl = 1.1f;
            legGroupSize = 1;

            weapons.add(
                new Weapon("fos-radix-weapon"){{
                    x = 8; y = -2;
                    recoil = 1f;
                    mirror = alternate = true;
                    reload = 45f;
                    rotate = false;
                    ejectEffect = Fx.none;
                    bullet = new BasicBulletType(){{
                        damage = 20;
                        lifetime = 45;
                        speed = 5f;
                        width = height = 10f;
                        collidesAir = false;

                        trailLength = 12;
                        trailWidth = 3;
                        trailEffect = Fx.artilleryTrail;
                        trailColor = backColor = Pal.accent.cpy().mul(0.8f);
                        frontColor = Pal.accent;

                        fragBullets = 9;
                        fragOnHit = true;
                        fragBullet = new LightningBulletType(){{
                            damage = 10;
                            lightningLength = 2;
                            lightningLengthRand = 2;
                        }};
                    }};
                }}
            );
        }};
        foetus = new FOSUnitType("foetus"){{
            health = 550;
            armor = 3;
            speed = 0.5f;
            hitSize = 18;
            rotateSpeed = 1.8f;
            targetAir = false;

            legCount = 3;
            legGroupSize = 1;
            legStraightness = 0.4f;
            baseLegStraightness = 0.5f;
            legLength = 9f;
            legForwardScl = 0.45f;
            legMoveSpace = 0.8f;
            rippleScale = 2f;
            legExtension = -5f;

            weapons.add(
                new Weapon("fos-foetus-weapon"){{
                    x = 0; y = -4;
                    recoil = 3f;
                    mirror = false;
                    reload = 120f;
                    ejectEffect = Fx.none;
                    shoot = new ShootMulti(
                        new ShootAlternate(){{
                            barrels = shots = 3;
                            spread = 3.5f;
                        }},
                        new ShootHelix(){{
                            mag = 3f;
                        }}
                    );
                    bullet = new BasicBulletType(){{
                        damage = 75;
                        speed = 3;
                        lifetime = 85;
                        width = height = 12;

                        collidesAir = false;
                        pierce = pierceBuilding = true;
                        pierceCap = 1;

                        trailLength = 6;
                        trailWidth = 4;
                        trailEffect = Fx.artilleryTrail;
                        trailColor = backColor = Pal.accent.cpy().mul(0.8f);
                        frontColor = Pal.accent;

                        intervalBullets = 1;
                        bulletInterval = 4;
                        intervalBullet = new LightningBulletType(){{
                            damage = 15;
                            collidesAir = false;
                            ammoMultiplier = 1f;
                            lightningColor = Pal.accent;
                            lightningLength = 3;
                            lightningLengthRand = 6;

                            //for visual stats only.
                            //buildingDamageMultiplier = 0.25f;

                            lightningType = new BulletType(0.0001f, 0f){{
                                lifetime = Fx.lightning.lifetime;
                                hitEffect = Fx.hitLancer;
                                despawnEffect = Fx.none;
                                status = StatusEffects.shocked;
                                statusDuration = 10f;
                                hittable = false;
                                lightColor = Color.white;
                                //buildingDamageMultiplier = 0.25f;
                            }};
                        }};
                    }};
                }}
            );
        }};
        vitarus = new FOSUnitType("vitarus"){{
            health = 2100;
            armor = 4;
            speed = 0.4f;
            hitSize = 22;
            rotateSpeed = 1.5f;

            legCount = 4;
            legGroupSize = 2;
            legStraightness = 0.6f;
            baseLegStraightness = 0.5f;
            legLength = 11f;
            legForwardScl = 0.45f;
            legMoveSpace = 1f;
            rippleScale = 2f;
            legExtension = -5f;

            weapons.add(
                new Weapon("fos-vitarus-weapon"){{
                    x = 0; y = 6;
                    recoil = 5f;
                    mirror = false;
                    reload = 120f;
                    ejectEffect = Fx.none;

                    bullet = new BasicBulletType(){{
                        damage = 80;
                        speed = 6;
                        lifetime = 45;
                        width = height = 8f;
                        lightRadius = 4f;

                        pierce = pierceBuilding = true;
                        pierceCap = 3;

                        trailLength = 9;
                        trailWidth = 3;
                        trailEffect = Fx.artilleryTrail;
                        trailColor = backColor = Pal.accent.cpy().mul(0.8f);
                        frontColor = Pal.accent;

                        fragBullets = 13;
                        fragRandomSpread = 30f;
                        fragBullet = new LightningBulletType(){{
                            damage = 37.5f;
                            lightningLength = 6;
                            lightningLengthRand = 6;
                            lightningColor = Pal.accent;
                        }};
                    }};
                }},
                new Weapon("fos-vitarus-point-weapon"){{
                    x = 9; y = -4;
                    mirror = alternate = true;
                    rotate = true;
                    top = true;
                    reload = 30f;

                    shootSound = Sounds.pew;
                    shoot = new ShootHelix(){{
                        shots = 2;
                        mag = 2.5f;
                    }};

                    bullet = new BasicBulletType(){{
                        damage = 15;
                        speed = 3;
                        lifetime = 120;
                        width = 3; height = 3;

                        trailLength = 8;
                        trailWidth = 1.5f;
                        trailColor = backColor = Pal.accent.cpy().mul(0.8f);
                        frontColor = Pal.accent;
                    }};
                }}
            );
        }};

        testOverdrive = new UnitType("test-overdrive"){{
            health = 360;
            hitSize = 12;
            rotateSpeed = 10f;
            flying = true;
            speed = 1.2f;
        }};

        assault = new FOSUnitType("assault"){{
            health = 200;
            armor = 2f;
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
                    reload = 80f;
                    bullet = new MissileBulletType(){{
                        damage = 50;
                        speed = 5; lifetime = 40;
                        homingPower = 0.1f;
                        homingDelay = 10f;
                        backColor = trailColor = Pal.sapBulletBack;
                        frontColor = Pal.reactorPurple2;
                        trailLength = 12;
                        buildingDamageMultiplier = 0.3f;
                    }};
                }}
            );
        }};
        abrupt = new FOSUnitType("abrupt"){{
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
                    inaccuracy = 22.5f;
                    shootSound = Sounds.artillery;
                    bullet = new ArtilleryBulletType(){{
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
                            shrinkX = shrinkY = -2;
                            sprite = "shell";
                            speed = 1f;
                            drag = 0.0083f;
                            lifetime = 120f;
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
        brunt = new FOSUnitType("brunt"){{
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
                        buildingDamageMultiplier = 0.3f;
                    }};
                }}
            );
        }};

        vulture = new CarrierUnitType("vulture"){{
            health = 400;
            speed = 2f;
            hitSize = 16f;
            payloadCapacity = 8f * 8f;
            engineOffset = 10f;
            flying = true;
            omniMovement = true;
            lowAltitude = true;
            controller = u -> new CarrierAI();
        }};

        bugSmall = new BugUnitType("bug-small", false){{
            health = 80;
            armor = 8;
            hitSize = 6f;
            speed = 0.3f;
            segments = 3;
            crushDamage = 0.2f;

            //copied from renale for now
            segmentScl = 3f;
            segmentPhase = 5f;
            segmentMag = 0.5f;
        }};

        bugMedium = new BugUnitType("bug-medium", false){{
            health = 240;
            armor = 20;
            hitSize = 6f;
            speed = 0.25f;
            segments = 3;
            crushDamage = 0.6f;

            //copied from renale too, he's the same size for some reason
            segmentScl = 3f;
            segmentPhase = 5f;
            segmentMag = 0.5f;
        }};

        bugFlyingSmall = new BugUnitType("bug-flying-small", true, true){{
            health = 60;
            armor = 1;
            hitSize = 6f;
            speed = 2f;
            rotateSpeed = 6f;
            range = 10f;
            circleTarget = true;
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
                    bullet = new BasicBulletType(0f, 75f){{
                        instantDisappear = true;
                        width = height = 1f;
                        collidesAir = collidesGround = true;
                        hitSound = Sounds.none;
                        despawnSound = Sounds.none;
                    }};
                }}
            );
        }};
        bugFlyingMedium = new BugUnitType("bug-flying-medium", true){{
            health = 200;
            armor = 2;
            hitSize = 12f;
            speed = 0.8f;
            rotateSpeed = 2f;
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

        //TODO
        subSmall = new SubmarineUnitType("sub-small"){{
            health = 250;
            speed = 0.7f;
            hitSize = 12f;
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
    }
}
