package fos.content;

import fos.type.content.WeaponModule;
import mindustry.content.Fx;
import mindustry.entities.bullet.*;
import mindustry.type.Weapon;

//why the heck I added FOS to class's name if weapon modules are unique to this mod anyway
public class FOSWeaponModules {
    public static WeaponModule standard1, standard5;

    public static void load() {
        standard1 = new WeaponModule("standard1", new Weapon("standard-weapon1"){{
            x = 0; y = 0;
            alternate = mirror = false;
            top = true;
            recoil = 0.4f;
            reload = 20f;
            bullet = new BasicBulletType(2.5f, 36){{
                width = 7f; height = 9f;
                trailLength = 8;
                lifetime = 60f;
            }};
        }});
        standard5 = new WeaponModule("standard5", new Weapon("standard-weapon5"){{
            x = 0; y = 0;
            alternate = mirror = false;
            recoil = 2f;
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
        }});
    }
}
