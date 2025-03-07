package fos.content;

import arc.*;
import arc.graphics.Color;
import fos.audio.FOSSounds;
import fos.entities.abilities.UnitResistanceAbility;
import fos.entities.bullet.StickyBulletType;
import fos.entities.bullet.player.*;
import fos.type.WeaponSet;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.game.EventType;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;
import mindustry.type.weapons.*;

import static fos.content.FOSItems.*;
import static mindustry.content.Items.silicon;
import static mindustry.type.ItemStack.*;

//why the heck I added FOS to class's name if weapon modules are unique to this mod anyway
//just because it looks nicer :)
public class FOSWeaponModules {
    public static WeaponSet
        standard1, standard2, standard3, standard4,
        shotgun1, shotgun2, shotgun3, shotgun4,
        support1, support2, support3, support4,
        legionFabricator, citadelStickyLauncher;

    public static void load() {
        // BASIC / ASSAULT RIFLES
        standard1 = new WeaponSet("standard1"){{
            weapons.add(
                new Weapon("fos-standard-weapon1"){{
                    x = 0; y = 0;
                    alternate = mirror = false;
                    rotate = true;
                    top = true;
                    recoil = 0.4f;
                    reload = recoilTime = 20f;
                    bullet = new PlayerBasicBulletType(2.5f, 72){{
                        width = 7f; height = 9f;
                        trailLength = 8;
                        lifetime = 60f;
                    }};
                }}
            );
            reqs = empty;
            researchCost = empty;
        }};
        standard2 = new WeaponSet("standard2"){{
            weapons.add(
                new Weapon("fos-standard-weapon2"){{
                    x = 0; y = 0;
                    alternate = mirror = false;
                    rotate = true;
                    recoil = 0.3f;
                    reload = recoilTime = 40f;
                    bullet = new PlayerBasicBulletType(2f, 180){{
                        width = 8f; height = 10f;
                        lifetime = 40f;
                        shootEffect = Fx.shootSmallSmoke;
                        trailLength = 6;
                        fragOnHit = false;
                        fragBullets = 2;
                        fragVelocityMin = 1f;
                        fragBullet = new PlayerSmartBulletType(7f, 60){{
                            width = 4f; height = 5f;
                            lifetime = 20f;
                            trailLength = 15;
                            hitEffect = Fx.hitBulletSmall;
                            collidesTiles = true;
                        }};
                    }};
                }}
            );
            reqs = with(zinc, 75, silver, 75);
            researchCost = empty;
        }};
        standard3 = new WeaponSet("standard3", new Weapon("fos-standard-weapon3"){{
            x = 0; y = 0;
            rotateSpeed = 5f;
            alternate = mirror = false;
            rotate = true;
            recoil = 2f;
            reload = recoilTime = 110f;
            inaccuracy = 8f;
            shake = 1f;
            shootY = 1f;

            shoot.shots = 11;
            shoot.shotDelay = 20f;

            cooldownTime = 90f;
            ejectEffect = Fx.casing3;
            shootSound = Sounds.splash;

            bullet = new PlayerBasicBulletType(7f, 60){{
                width = 7f; height = 9f;
                trailLength = 12;
                lifetime = 30f;
                homingPower = 0.03f;
            }};
        }}).reqs(with(zinc, 250, silicon, 200, vanadium, 100));
        //TODO: placeholder
        //standard4 = new WeaponSet("standard4", new Weapon("fos-standard-weapon4")).reqs(with(lead, 1));

        // SHOTGUNS
        shotgun1 = new WeaponSet("shotgun1"){{
            weapons.add(
                new Weapon("fos-shotgun-mount1"){{
                    x = y = 0;
                    alternate = mirror = false;
                    rotate = true;
                    rotateSpeed = 5f;
                    inaccuracy = 0f;
                    reload = recoilTime = 45f;
                    shootY = 0.5f;

                    shootSound = Sounds.shootBig;
                    ejectEffect = Fx.casing2;

                    bullet = new PlayerBasicBulletType(){{
                        speed = 4.8f; lifetime = 15f;
                        width = 8f; height = 10f;
                        damage = 60;
                        knockback = 4f;
                        pierce = true;
                        pierceCap = 2;

                        hitColor = trailColor = Pal.accent;
                        hitEffect = despawnEffect = Fx.hitSquaresColor;
                        shootEffect = Fx.shootTitan;
                        smokeEffect = Fx.shootSmokeSquareSparse;
                        trailWidth = 2f;
                        trailLength = 6;
                        trailEffect = Fx.shootSmokeSquare;
                        trailChance = 0.08f;

                        intervalBullets = 3;
                        intervalRandomSpread = 30f;
                        bulletInterval = 45f;
                        intervalBullet = new PlayerBasicBulletType(){{
                            speed = 4.8f; lifetime = 15f;
                            width = 8f; height = 10f;
                            damage = 60;
                            knockback = 4f;
                            pierce = true;
                            pierceCap = 2;

                            velocityRnd = 0.2f;

                            hitColor = trailColor = Pal.accent;
                            hitEffect = despawnEffect = Fx.hitSquaresColor;
                            shootEffect = Fx.shootBigColor;
                            smokeEffect = Fx.shootSmokeSquareSparse;
                            trailWidth = 2f;
                            trailLength = 6;
                            trailEffect = Fx.shootSmokeSquare;
                            trailChance = 0.08f;
                        }};
                    }};
                }}
            );
            reqs = empty;
        }};
        shotgun2 = new WeaponSet("shotgun2"){{
            weapons.add(
                new Weapon("fos-shotgun-mount2"){{
                    x = y = 0;
                    alternate = mirror = false;
                    rotate = true;
                    rotateSpeed = 5f;
                    inaccuracy = 0f;
                    reload = recoilTime = 45f;
                    shootY = 0.5f;

                    shootSound = Sounds.shootBig;
                    ejectEffect = Fx.casing2;

                    bullet = new PlayerBasicBulletType(){{
                        speed = 6f; lifetime = 15f;
                        width = 8f; height = 10f;
                        damage = 60;
                        knockback = 4f;
                        pierce = true;
                        pierceCap = 2;

                        hitColor = trailColor = Pal.accent;
                        hitEffect = despawnEffect = Fx.hitSquaresColor;
                        shootEffect = Fx.shootTitan;
                        smokeEffect = Fx.shootSmokeSquareSparse;
                        trailWidth = 2f;
                        trailLength = 6;
                        trailEffect = Fx.shootSmokeSquare;
                        trailChance = 0.08f;

                        intervalBullets = 3;
                        intervalRandomSpread = 10f;
                        bulletInterval = 45f;
                        intervalBullet = new PlayerBasicBulletType(){{
                            speed = 6f; lifetime = 15f;
                            width = 8f; height = 10f;
                            damage = 60;
                            knockback = 4f;
                            pierce = true;
                            pierceCap = 2;

                            velocityRnd = 0.2f;

                            hitColor = trailColor = Pal.accent;
                            hitEffect = despawnEffect = Fx.hitSquaresColor;
                            shootEffect = Fx.shootBigColor;
                            smokeEffect = Fx.shootSmokeSquareSparse;
                            trailWidth = 2f;
                            trailLength = 6;
                            trailEffect = Fx.shootSmokeSquare;
                            trailChance = 0.08f;
                        }};
                    }};
                }}
            );
            reqs = with(zinc, 60, silver, 90);
        }};
        shotgun3 = new WeaponSet("shotgun3", new Weapon("fos-shotgun-mount3"){{
            x = y = 0;
            alternate = mirror = false;
            rotate = true;
            rotateSpeed = 3f;
            shoot = new ShootAlternate(){{
                shots = barrels = 6;
                spread = 1.2f;
            }};
            reload = recoilTime = 40f;
            shootSound = Sounds.cannon;
            ejectEffect = Fx.casing3;
            shootY = 1.5f;
            bullet = new PlayerBasicBulletType(){{
                speed = 8f; lifetime = 12f;
                damage = 150;
                hitEffect = Fx.flakExplosion;
                trailLength = 7;
                trailEffect = Fx.trailFade;
                knockback = 5f;
                fragOnHit = true;
                fragBullets = 1;
                fragBullet = new ExplosionBulletType(){{
                    splashDamage = 50;
                    splashDamageRadius = 16;
                    killShooter = false;
                    knockback = 4f;
                }};
            }};
        }}).reqs(with(zinc, 200, silver, 50, silicon, 200, vanadium, 100));
        //TODO: placeholder
        //shotgun4 = new WeaponSet("shotgun4", new Weapon()).reqs(with(lead, 1));

        // SUPPORT MODULES
        support1 = new WeaponSet("support1"){{
            weapons.add(new BuildWeapon("fos-support-mount1"));
            abilities.add(
                new StatusFieldAbility(FOSStatuses.buildBoost, 60, 30, 1){{
                    activeEffect = Fx.none;
                }}
            );
            reqs = empty;
        }};
        support2 = new WeaponSet("support2", new Weapon("fos-support-mount2"){{
            x = 0; y = 0;
            rotate = false;
            top = true;
            reload = 10f;
            mirror = false;
            inaccuracy = 4f;
            shootCone = 10f;
            recoil = 2f;
            bullet = new PlayerBasicBulletType(){{
                lifetime = 30f; speed = 4f;

                width = 10f; height = 20f;
                //sprite = "laser";
                backColor = Pal.heal;
                frontColor = Color.white;
                shootEffect = Fx.shootHeal;
                shootSound = Sounds.lasershoot;

                damage = 0;
                collidesTeam = true;
                collidesAir = false;
                collidesGround = true;
                healPercent = 5;
            }};
        }}).reqs(with(zinc, 50, silver, 100));
        support3 = new WeaponSet("support3"){{
            abilities.add(
                new EnergyFieldAbility(0f, 40f, 64f){{
                    x = 0; y = 2;
                    statusDuration = 120f;
                    maxTargets = 6;
                    color = Pal.heal;
                    effectRadius = 2f;
                    healPercent = 2.5f;
                    healEffect = Fx.heal;
                    damageEffect = Fx.chainLightning;
                }}
            );
            reqs = with(zinc, 75, silver, 150, vanadium, 150);
        }};

        // BOSS WEAPONS
        legionFabricator = new WeaponSet("legion-fabricator"){{
            weapons.add(
                new RepairBeamWeapon("fos-legion-beam-replica"){{
                    x = 0; y = 0;
                    mirror = false;
                    beamWidth = 0.4f;
                    repairSpeed = 0.2f;
                    bullet = new BulletType(){{
                        maxRange = 40f;
                    }};
                }}
            );
            customIcon = true;
            Events.on(EventType.ContentInitEvent.class, e -> {
                abilities.add(
                    new UnitResistanceAbility(FOSUnitTypes.legionnaireReplica, 0.05f),
                    new UnitSpawnAbility(FOSUnitTypes.legionnaireReplica, 600, 16, 0)
                );
            });
        }}.reqs(with(zinc, 200, silver, 125, silicon, 150)).produceTime(60 * 20);
        citadelStickyLauncher = new WeaponSet("citadel-stickybomb-launcher"){
            {
                description = Core.bundle.getOrNull(getContentType() + "." + this.name + ".description" + (Vars.mobile ? "-mobile" : ""));

                weapons.add(
                    new Weapon("fos-citadel-stickybomb-launcher"){{
                        x = 0; y = -2;
                        mirror = false;
                        rotate = false;
                        recoil = 2f;
                        reload = 60f;
                        inaccuracy = 6f;
                        shootSound = FOSSounds.sticky;
                        bullet = new StickyBulletType(4f, 20f){{
                            lifetime = 40f;
                            width = height = 16f;
                            velocityRnd = 0.2f;

                            trailWidth = 3f;
                            trailLength = 12;
                            trailColor = Pal.plastaniumBack;
                            backColor = Pal.plastaniumBack;
                            frontColor = Pal.plastaniumFront;
                            ejectEffect = Fx.smokeCloud;

                            splashDamage = 120f;
                            splashDamageRadius = 28f;
                        }};
                    }}
                );
                reqs = with(zinc, 150, diamond, 75, silicon, 150);
            }
        };
    }
}
