package fos.content;

import fos.type.bullets.SmartBulletType;
import fos.type.content.WeaponSet;
import mindustry.content.Fx;
import mindustry.entities.bullet.*;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;

import static fos.content.FOSItems.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;

//why the heck I added FOS to class's name if weapon modules are unique to this mod anyway
//just because it looks nicer :)
public class FOSWeaponModules {
    public static WeaponSet
        standard1, standard2, standard3, standard4, standard5,
        shotgun1, shotgun2, shotgun3, shotgun4, shotgun5;

    public static void load() {
        standard1 = new WeaponSet("standard1", new Weapon("fos-standard-weapon1"){{
            x = 0; y = 0;
            alternate = mirror = false;
            rotate = true;
            top = true;
            recoil = 0.4f;
            reload = 20f;
            bullet = new BasicBulletType(2.5f, 36){{
                width = 7f; height = 9f;
                trailLength = 8;
                lifetime = 60f;
            }};
        }}).reqs(with(tin, 75, silver, 75));
        standard2 = new WeaponSet("standard2", new Weapon("fos-standard-weapon2"){{
            x = 0; y = 0;
            alternate = mirror = false;
            rotate = true;
            recoil = 0.3f;
            reload = 40f;
            bullet = new BasicBulletType(2f, 90){{
                width = 8f; height = 10f;
                lifetime = 40f;
                shootEffect = Fx.shootSmallSmoke;
                trailLength = 6;
                fragOnHit = false;
                fragBullets = 2;
                fragVelocityMin = 1f;
                collidesTiles = false;
                fragBullet = new SmartBulletType(7f, 30){{
                    width = 4f; height = 5f;
                    lifetime = 20f;
                    trailLength = 15;
                    hitEffect = Fx.hitBulletSmall;
                    collidesTiles = true;
                }};
            }};
        }}).reqs(with(tin, 150, silver, 150));
        standard3 = new WeaponSet("standard3", new Weapon("fos-standard-weapon3"){{
            x = 0; y = 0;
            alternate = mirror = false;
            rotate = true;
            recoil = 2f;
            reload = 15f;
            inaccuracy = 8f;
            cooldownTime = 30f;
            bullet = new BasicBulletType(3.5f, 60f){{
                width = 7f; height = 9f;
                trailLength = 12;
                lifetime = 60f;
                homingPower = 0.8f;
            }};
        }}).reqs(with(tin, 250, diamond, 150, silicon, 300));
        //TODO: placeholders
        standard4 = new WeaponSet("standard4", new Weapon("fos-standard-weapon4")).reqs(with(lead, 1));
        standard5 = new WeaponSet("standard5", new Weapon("fos-standard-weapon5"){{
            x = 0; y = 0;
            alternate = mirror = false;
            rotate = true;
            recoil = 4f;
            reload = 10f;
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

        shotgun1 = new WeaponSet("shotgun1", new Weapon("fos-shotgun-mount1"){{
            x = y = 0;
            alternate = mirror = false;
            rotate = true;
            rotateSpeed = 5f;
            inaccuracy = 30f;
            shoot.shots = 4;
            shoot.shotDelay = 0f;
            reload = 90f;
            shootSound = Sounds.shotgun;
            ejectEffect = Fx.casing2;
            bullet = new ShrapnelBulletType(){{
                width = 2f;
                length = 48f;
                fromColor = Pal.accentBack;
                toColor = Pal.accent;
                damage = 75f;
                lifetime = 20f;
                hitEffect = Fx.hitBulletSmall;
            }};
        }}).reqs(with(tin, 60, silver, 50));
        //TODO: placeholders
        shotgun2 = new WeaponSet("shotgun2", new Weapon()).reqs(with(lead, 1));
        shotgun3 = new WeaponSet("shotgun3", new Weapon()).reqs(with(lead, 1));
        shotgun4 = new WeaponSet("shotgun4", new Weapon()).reqs(with(lead, 1));
        shotgun5 = new WeaponSet("shotgun5", new Weapon()).reqs(with(lead, 1));
    }
}
