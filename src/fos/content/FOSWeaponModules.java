package fos.content;

import arc.Events;
import arc.graphics.Color;
import fos.type.abilities.UnitResistanceAbility;
import fos.type.bullets.SmartBulletType;
import fos.type.content.WeaponSet;
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
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.*;

//why the heck I added FOS to class's name if weapon modules are unique to this mod anyway
//just because it looks nicer :)
public class FOSWeaponModules {
    public static WeaponSet
        standard1, standard2, standard3, standard4, standard5,
        shotgun1, shotgun2, shotgun3, shotgun4, shotgun5,
        support1, support2, support3, support4, support5,
        legionFabricator;

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
                    bullet = new BasicBulletType(2.5f, 72){{
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
                    bullet = new BasicBulletType(2f, 180){{
                        width = 8f; height = 10f;
                        lifetime = 40f;
                        shootEffect = Fx.shootSmallSmoke;
                        trailLength = 6;
                        fragOnHit = false;
                        fragBullets = 2;
                        fragVelocityMin = 1f;
                        fragBullet = new SmartBulletType(7f, 60){{
                            width = 4f; height = 5f;
                            lifetime = 20f;
                            trailLength = 15;
                            hitEffect = Fx.hitBulletSmall;
                            collidesTiles = true;
                        }};
                    }};
                }}
            );
            reqs = with(tin, 75, silver, 75);
            researchCost = empty;
        }};
        standard3 = new WeaponSet("standard3", new Weapon("fos-standard-weapon3"){{
            x = 0; y = 0;
            alternate = mirror = false;
            rotate = true;
            recoil = 2f;
            reload = recoilTime = 15f;
            inaccuracy = 8f;
            cooldownTime = 30f;
            bullet = new BasicBulletType(3.5f, 120){{
                width = 7f; height = 9f;
                trailLength = 12;
                lifetime = 60f;
                homingPower = 0.8f;
            }};
        }}).reqs(with(tin, 250, silicon, 200, vanadium, 100));
        //TODO: placeholders
        standard4 = new WeaponSet("standard4", new Weapon("fos-standard-weapon4")).reqs(with(lead, 1));
        standard5 = new WeaponSet("standard5", new Weapon("fos-standard-weapon5"){{
            x = 0; y = 0;
            alternate = mirror = false;
            rotate = true;
            top = true;
            recoil = 4f;
            reload = recoilTime = 10f;
            bullet = new BasicBulletType(13f, 240){{
                pierce = true;
                pierceCap = 10;
                width = 14f;
                height = 33f;
                lifetime = 15f;
                shootEffect = Fx.shootBig;
                trailLength = 24;
                fragVelocityMin = 0.4f;

                hitEffect = Fx.blastExplosion;
                splashDamage = 18f;
                splashDamageRadius = 13f;

                fragBullets = 3;
                fragLifeMin = 0f;
                fragRandomSpread = 30f;

                fragBullet = new BasicBulletType(9f, 20){{
                    width = 10f;
                    height = 10f;
                    pierce = true;
                    pierceBuilding = true;
                    pierceCap = 3;

                    lifetime = 20f;
                    hitEffect = Fx.flakExplosion;
                    splashDamage = 15f;
                    splashDamageRadius = 10f;
                }};
            }};
        }}).reqs(with(tin, 500, silver, 500, vanadium, 300, nickel, 250, luminium, 200));

        // SHOTGUNS
        shotgun1 = new WeaponSet("shotgun1"){{
            weapons.add(
                new Weapon("fos-shotgun-mount1"){{
                    x = y = 0;
                    alternate = mirror = false;
                    rotate = true;
                    rotateSpeed = 5f;
                    inaccuracy = 30f;
                    shoot.shots = 4;
                    shoot.shotDelay = 0f;
                    reload = recoilTime = 90f;
                    shootSound = Sounds.shotgun;
                    ejectEffect = Fx.casing2;
                    bullet = new ShrapnelBulletType(){{
                        width = 2f;
                        length = 48f;
                        fromColor = Pal.accentBack;
                        toColor = Pal.accent;
                        damage = 120;
                        lifetime = 20f;
                        hitEffect = Fx.hitBulletSmall;
                        knockback = 4f;
                    }};
                }}
            );
            reqs = empty;
        }};
        shotgun2 = new WeaponSet("shotgun2", new Weapon("fos-shotgun-mount2"){{
            x = y = 0;
            alternate = mirror = false;
            rotate = true;
            rotateSpeed = 5f;
            inaccuracy = 10f;
            shoot.shots = 4;
            shoot.shotDelay = 0f;
            reload = recoilTime = 90f;
            shootSound = Sounds.shotgun;
            ejectEffect = Fx.casing2;
            bullet = new ShrapnelBulletType(){{
                width = 2f;
                length = 80f;
                fromColor = Pal.accentBack;
                toColor = Pal.accent;
                damage = 120;
                lifetime = 20f;
                hitEffect = Fx.hitBulletSmall;
                knockback = 3f;
            }};
        }}).reqs(with(tin, 60, silver, 90));
        shotgun3 = new WeaponSet("shotgun3", new Weapon("fos-shotgun-mount3"){{
            x = y = 0;
            alternate = mirror = false;
            rotate = true;
            rotateSpeed = 3f;
            shoot = new ShootAlternate(){{
                shots = barrels = 6;
                spread = 3f;
            }};
            reload = recoilTime = 40f;
            shootSound = Sounds.cannon;
            ejectEffect = Fx.casing3;
            bullet = new BasicBulletType(){{
                speed = 4f; lifetime = 20f;
                damage = 80;
                hitEffect = Fx.hitBulletSmall;
                trailLength = 4;
                trailEffect = Fx.trailFade;
                knockback = 5f;
                fragOnHit = true;
                fragBullets = 1;
                fragBullet = new ExplosionBulletType(){{
                    splashDamage = 120;
                    splashDamageRadius = 16;
                    killShooter = false;
                    knockback = 4f;
                }};
            }};
        }}).reqs(with(tin, 200, silver, 50, silicon, 200, vanadium, 100));
        //TODO: placeholders
        shotgun4 = new WeaponSet("shotgun4", new Weapon()).reqs(with(lead, 1));
        shotgun5 = new WeaponSet("shotgun5", new Weapon()).reqs(with(lead, 1));

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
            bullet = new BasicBulletType(){{
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
        }}).reqs(with(tin, 50, silver, 100));
        support3 = new WeaponSet("support3"){{
            abilities.add(
                new EnergyFieldAbility(80f, 40f, 64f){{
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
            reqs = with(tin, 75, silver, 150, vanadium, 150);
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
        }}.reqs(with(tin, 200, silver, 125, silicon, 150)).produceTime(60 * 20);
    }
}
