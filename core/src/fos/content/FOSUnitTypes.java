package fos.content;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Rect;
import fos.ai.*;
import fos.gen.*;
import fos.graphics.FOSPal;
import fos.type.abilities.HackFieldAbility;
import fos.type.bullets.*;
import fos.type.units.*;
import fos.type.units.destroyers.DestroyersUnits;
import fos.type.units.weapons.InjectorWeapon;
import mindustry.ai.types.GroundAI;
import mindustry.annotations.Annotations;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

import static fos.content.FOSStatuses.hacked;

public class FOSUnitTypes {
    public static @Annotations.EntityDef({Mechc.class}) UnitType legion, citadel;

    public static @Annotations.EntityDef({Tankc.class}) UnitType warden;

    public static @Annotations.EntityDef({Unitc.class}) UnitType sergeant, lieutenant, captain, general, marshal,
            smoke, cloud;

    public static @Annotations.EntityDef({ElevationMovec.class, Unitc.class}) UnitType assault;

    public static @Annotations.EntityDef({Payloadc.class, Unitc.class}) UnitType vulture;

    public static @Annotations.EntityDef({BuildingTetherc.class, Payloadc.class}) UnitType testOverdrive;

    //TODO submarines
    public static @Annotations.EntityDef({Submarinec.class}) UnitType subSmall;

    public static @Annotations.EntityDef({LumoniPlayerUnitc.class, Legsc.class}) UnitType lord;

    public static @Annotations.EntityDef({Bugc.class, Crawlc.class}) UnitType bugSmall, bugMedium;

    public static @Annotations.EntityDef({Bugc.class, Unitc.class}) UnitType bugFlyingSmall, bugFlyingMedium;

    public static void load(){
        DestroyersUnits.load();

        //TODO: campaign boss
        legion = new UnitType("legion"){{
            health = 25000;
            armor = 25;
            hitSize = 25;
            speed = 0.1f;
            flying = false;
            canBoost = false;

            float angle = 0f;
            for(int i = 0; i < 8; i++){
                float x = Mathf.cos(angle) * 32;
                float y = Mathf.sin(angle) * 32;
                abilities.add(new UnitSpawnAbility(UnitTypes.atrax, 600, x, y));
                angle += Mathf.PI2 / 8;
            }
        }};
        //TODO: campaign boss
        citadel = new BossUnitType("citadel"){{
            health = 7500;
            armor = 30;
            hitSize = 40;
            speed = 0.05f;
            flying = false;
            canBoost = false;
            weapons.add(
                new Weapon("citadel-shotgun"){{
                    x = 24; y = 4;
                    rotate = false;
                    mirror = true;
                    alternate = true;
                    recoil = 4f;
                    recoilTime = 60f;
                    reload = 30f;
                    inaccuracy = 10f;
                    shoot.shots = 6;
                    bullet = new BasicBulletType(4f, 30f){{
                        lifetime = 40f;
                        width = 4f; height = 8f;
                        trailWidth = 2f;
                        trailLength = 12;
                        velocityRnd = 0.1f;
                    }};
                }},
                new Weapon("citadel-launcher"){{
                    x = 18; y = -20;
                    rotate = true;
                    mirror = true;
                    alternate = true;
                    recoil = 2f;
                    reload = 90f;
                    inaccuracy = 3f;
                    bullet = new StickyBulletType(2f, 1f, 180){{
                        lifetime = 120f;
                        width = height = 16f;
                        trailWidth = 4f;
                        trailLength = 12;
                        trailColor = Pal.plastaniumBack;
                        backColor = Pal.plastaniumBack;
                        frontColor = Pal.plastaniumFront;
                        ejectEffect = Fx.smokeCloud;
                        hitSound = Sounds.mud;
                        despawnEffect = Fx.explosion;
                        splashDamage = 80f;
                        splashDamageRadius = 16f;
                        collidesGround = collidesAir = true;
                        collidesTiles = true;
                    }};
                }}
            );
        }};
        //TODO: campaign boss
        warden = new BossUnitType("warden"){{
            health = 9000;
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
                new Weapon("warden-artillery"){{
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
                        damage = 30f;
                        splashDamage = 120f;
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
                new Weapon("warden-artillery"){{
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
                        damage = 30f;
                        splashDamage = 120f;
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

                new Weapon("warden-machine-gun"){{
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
                        damage = 80f;
                        speed = 5f;
                        lifetime = 30f;
                        shootSound = Sounds.shoot;
                    }};
                }},
                new Weapon("warden-machine-gun"){{
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
                        damage = 80f;
                        speed = 5f;
                        lifetime = 30f;
                        shootSound = Sounds.shoot;
                    }};
                }},

                new Weapon("warden-antiair"){{
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
                        damage = 60f;
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
            health = 2400;
            armor = 6;
            hitSize = 10;
            speed = 0.8f;
            flying = false;
            canBoost = false;
            mineTier = 2;
            mineSpeed = 8f;
            buildSpeed = 1f;
            weapons.add(FOSWeaponModules.standard1.weapons);
        }};

        sergeant = new UnitType("sergeant"){{
            health = 150;
            hitSize = 12;
            speed = 1.2f;
            flying = true;
            omniMovement = true;
            immunities.add(hacked);
            circleTarget = true;
            trailColor = FOSPal.hackedBack;
            trailLength = 12;
            aiController = InjectorAI::new;
            weapons.add(
                new InjectorWeapon("fos-injector"){{
                    bullet = new InjectorBasicBulletType(0, 0.3f, 50, 300, false){{
                        homingPower = 1;
                        speed = 1.2f;
                    }};
                    x = 0; y = 0;
                    reload = 60 * 5;
                    ejectEffect = Fx.casing1;
                }}
            );
        }};
        lieutenant = new UnitType("lieutenant"){{
            health = 360;
            hitSize = 16;
            speed = 2.4f;
            flying = true;
            circleTarget = true;
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
                        splashDamage = 20f;
                        splashDamageRadius = 32f;
                    }};
                }}
            );
        }};
        captain = new UnitType("captain"){{
            health = 900;
            hitSize = 20;
            speed = 1.1f;
            flying = true;
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
                    bullet = new InjectorBasicBulletType(0f, 0.95f, 600, 5000, false){{
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
                    bullet = new InjectorBasicBulletType(0f, 0.95f, 600, 5000, false){{
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
                }}
            );
        }};
        general = new UnitType("general"){{
            health = 6250;
            hitSize = 20;
            speed = 1.5f;
            flying = true;
            aiController = InjectorAI::new;
            immunities.add(hacked);
            range = 40f;
            abilities.add(new HackFieldAbility(hacked, 40f, 0.002f));
        }};
        marshal = new UnitType("marshal"){{
            health = 18000;
            hitSize = 36;
            speed = 0.8f;
            range = 280f;
            flying = true;
            immunities.add(hacked);
            aiController = InjectorAI::new;
            weapons.add(
                new InjectorWeapon(){{
                    x = 0; y = 4;
                    reload = 900f;
                    shoot.firstShotDelay = 300f;
                    inaccuracy = 0f;
                    bullet = new InjectorBasicBulletType(1, true){{
                        chargeEffect = Fx.lancerLaserCharge;
                        speed = 6f; lifetime = 64f;
                        width = height = 18f;
                        homingRange = 280f;
                        homingPower = 1f;
                        backColor = FOSPal.hackedBack;
                        frontColor = FOSPal.hacked;
                    }};
                }}
            );
        }};

        smoke = new UnitType("smoke"){{
            health = 200;
            armor = 3f;
            hitSize = 9f;
            rotateSpeed = 3f;
            omniMovement = false;
            circleTarget = true;
            speed = 4f;
            flying = true;
            trailLength = 32;
            trailColor = FOSPal.destroyerTrail;
            abilities.add(
                new LiquidExplodeAbility(){{
                    liquid = Liquids.slag;
                }}
            );
            weapons.add(
                new Weapon(){{
                    shootCone = 360f;
                    bullet = new ExplosionBulletType(120f, 16f){{
                        targetAir = true;
                        targetGround = false;
                        buildingDamageMultiplier = 0.2f;
                    }};
                }}
            );
        }};
        cloud = new UnitType("cloud"){{
            health = 400;
            armor = 4;
            hitSize = 12f;
            rotateSpeed = 2f;
            omniMovement = false;
            trailColor = engineColor = FOSPal.destroyerTrail;
            speed = 5f;
            accel = 0.005f;
            drag = 0.2f;
            abilities.add(
                new MoveLightningAbility(40f, 16, 0.2f, -6f, 3f, 5f, FOSPal.destroyerTrail.cpy().shiftSaturation(-0.3f))
            );
        }};

        testOverdrive = new UnitType("test-overdrive"){{
            health = 360;
            hitSize = 12;
            rotateSpeed = 10f;
            flying = true;
            speed = 1.2f;
        }};

        assault = new UnitType("assault"){{
            health = 400;
            armor = 2f;
            hovering = true;
            shadowElevation = 0.1f;
            drag = 0.07f;
            speed = 1.8f;
            rotateSpeed = 5f;
            engineOffset = 7f;
            engineSize = 2f;
            itemCapacity = 0;

            abilities.add(new MoveEffectAbility(0f, -7f, Pal.sapBulletBack, Fx.missileTrailShort, 4f){{
                teamColor = true;
            }});

            parts.add(new HoverPart(){{
                x = 3.9f;
                y = -4;
                mirror = true;
                radius = 6f;
                phase = 90f;
                stroke = 2f;
                layerOffset = -0.001f;
                color = Color.valueOf("bf92f9");
            }});
        }
            @Override
            public void update(Unit unit) {
                super.update(unit);
            }
        };

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
