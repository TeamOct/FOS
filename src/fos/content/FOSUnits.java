package fos.content;

import arc.math.*;
import fos.graphics.FOSPal;
import fos.type.abilities.*;
import fos.ai.*;
import fos.type.bullets.*;
import fos.type.units.*;
import fos.type.units.weapons.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;

import static fos.content.FOSStatuses.hacked;

public class FOSUnits {
    public static UnitType
    //mechs
    legion, citadel,
    //legs
    lord, testBoss,
    //flying
    sergeant, lieutenant, captain, general, marshal,
    //payload
    vulture,
    //get stick-BUG-ged lol
    smallBug, mediumBug, largeBug, hugeBug, titanBug,
    smallFlying, mediumFlying, largeFlying, hugeFlying, titanFlying;

    public static void load(){
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
            constructor = MechUnit::create;
        }};
        citadel = new UnitType("citadel"){{
            health = 7500;
            armor = 40;
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
            constructor = MechUnit::create;
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
            weapons.add(FOSWeaponModules.standard1.weapon);
            constructor = LumoniPlayerUnit::create;
        }};
        testBoss = new BossUnitType("test-boss", FOSWeaponModules.standard2){{
            health = 2800;
            armor = 8;
            /* custom range to prevent cheesing */ range = 200f;
            hitSize = 14;
            speed = 0.6f;
            flying = false;
            weapons.add(FOSWeaponModules.standard2.weapon);
            constructor = LegsUnit::create;
            aiController = GroundBossAI::new;
        }};

        sergeant = new UnitType("sergeant"){{
            health = 150;
            hitSize = 12;
            speed = 1.2f;
            flying = true;
            omniMovement = true;
            immunities.add(hacked);
            circleTarget = true;
            //aiController = InjectorAI::new;
            weapons.add(
                new InjectorWeapon("fos-injector"){{
                    bullet = new InjectorBulletType(0, 0.3f, 50, 300, false){{
                        homingPower = 1;
                        speed = 1.2f;
                    }};
                    x = 0; y = 0;
                    reload = 60 * 5;
                    ejectEffect = Fx.casing1;
                }}
            );
            constructor = UnitEntity::create;
        }};
        lieutenant = new UnitType("lieutenant"){{
            health = 260;
            hitSize = 8;
            speed = 1.4f;
            flying = true;
            //aiController = InjectorAI::new;
            immunities.add(hacked);
            weapons.add(
                new InjectorWeapon(){{
                    x = y = 0;
                    mirror = false;
                    rotate = true;
                    bullet = new InjectorBulletType(0.03f, false){{
                        splashDamage = 10;
                        splashDamageRadius = 16;
                        killShooter = true;
                        instantDisappear = true;
                    }};
                }}
            );
            constructor = UnitEntity::create;
        }};
        captain = new UnitType("captain"){{
            health = 600;
            hitSize = 12;
            speed = 1.1f;
            flying = true;
            //aiController = InjectorAI::new;
            immunities.add(hacked);
            weapons.add(
                new InjectorWeapon("fos-missile-launcher"){{
                    x = -2; y = -1;
                    mirror = true;
                    alternate = false;
                    rotate = true;
                    reload = 300f;
                    shoot.shots = 8;
                    shoot.shotDelay = 10f;
                    inaccuracy = 12f;
                    bullet = new InjectorBulletType(0f, 0.95f, 600, 5000, false){{
                        damage = 25f;
                        speed = 2.4f; lifetime = 90f;
                        width = 6f; height = 12f;
                        backColor = FOSPal.hackedBack;
                        frontColor = FOSPal.hacked;
                        homingPower = 1;
                        weaveScale = 0.8f;
                        weaveMag = 1.8f;
                    }};
                }}
            );
            constructor = UnitEntity::create;
        }};
        general = new UnitType("general"){{
            health = 6250;
            hitSize = 20;
            speed = 1.5f;
            flying = true;
            //aiController = InjectorAI::new;
            immunities.add(hacked);
            abilities.add(new HackFieldAbility(hacked, 40f, 0.002f));
            constructor = UnitEntity::create;
        }};
        marshal = new UnitType("marshal"){{
            health = 18000;
            hitSize = 36;
            speed = 0.8f;
            range = 280f;
            flying = true;
            immunities.add(hacked);
            //aiController = InjectorAI::new;
            weapons.add(
                new InjectorWeapon(){{
                    x = 0; y = 4;
                    reload = 900f;
                    shoot.firstShotDelay = 300f;
                    inaccuracy = 0f;
                    bullet = new InjectorBulletType(1, true){{
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
            constructor = UnitEntity::create;
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
            constructor = PayloadUnit::create;
            controller = u -> new CarrierAI();
        }};

        smallBug = new BugUnitType("bug-small", false){{
            health = 80;
            armor = 8;
            hitSize = 10f;
            speed = 0.3f;
            segments = 2;
            crushDamage = 0.2f;
        }};

        smallFlying = new BugUnitType("bug-flying-small", true, true){{
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
        mediumFlying = new BugUnitType("bug-flying-medium", true){{
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
    }
}
