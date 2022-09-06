package fos.content;

import arc.graphics.Color;
import arc.math.*;
import arc.struct.*;
import fos.type.abilities.HackFieldAbility;
import fos.type.bullets.*;
import fos.type.units.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.type.*;

public class FOSUnits {
    public static UnitType
    //mechs
    mwArtillery, mwShotgun, mwStandard, mwMiner, legion,
    //legs
    temp, testBoss,
    //flying
    sergeant, lieutenant, captain, general, marshal,

    hidden;

    public static void load(){
        mwArtillery = new UnitType("mw-artillery"){{
            health = 200;
            rotateSpeed = 1.5f;
            range = 216;
            weapons = Seq.with(
                    new Weapon("mw-artillery-weapon"){{
                        x = 4; y = 0;
                        ejectEffect = Fx.casing2;
                        reload = 120;
                        alternate = true;
                        bullet = FOSBullets.smallArtillery;
                    }}
            );
            constructor = MechUnit::create;
        }};
        mwShotgun = new UnitType("mw-shotgun"){{
            health = 220;
            rotateSpeed = 1.5f;
            range = 110;
            weapons = Seq.with(
                    new Weapon("mw-shotgun-weapon"){{
                        x = 4; y = 0;
                        ejectEffect = Fx.casing1;
                        reload = 180;
                        alternate = true;
                        shoot.shots = 4;
                        shootCone = 15;
                        bullet = FOSBullets.smallStandardFlak;
                    }}
            );
            constructor = MechUnit::create;
        }};
        mwStandard = new UnitType("mw-standard"){{
            health = 200;
            rotateSpeed = 1.5f;
            range = 160;
            weapons = Seq.with(
                    new Weapon("mw-standard-weapon"){{
                        x = 4; y = 0;
                        ejectEffect = Fx.casing1;
                        reload = 30;
                        alternate = true;
                        bullet = FOSBullets.smallStandard;
                    }}
            );
            constructor = MechUnit::create;
        }};
        mwMiner = new UnitType("mw-miner"){{
            health = 200;
            rotateSpeed = 10;
            mineTier = 1;
            mineSpeed = 0.5f;
            range = 30;
            weapons = Seq.with();
            aiController = () -> new MinerAI(){};
            constructor = UnitEntity::create;
        }};
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

        temp = new LuminaUnitType("TEMP-NAME"){{
            health = 2400;
            armor = 6;
            hitSize = 10;
            speed = 0.4f;
            flying = false;
            canBoost = false;
            buildSpeed = 1f;
            weapons.add(FOSWeaponModules.standard1.weapon);
            constructor = LuminaUnit::create;
        }};
        testBoss = new LuminaBossType("test-boss", FOSWeaponModules.standard5){{
            health = 4000;
            armor = 8;
            hitSize = 14;
            speed = 0.4f;
            flying = false;
            constructor = BossLegsUnit::create;
        }};

        sergeant = new UnitType("sergeant"){{
            health = 150;
            hitSize = 6;
            speed = 1.2f;
            flying = true;
            omniMovement = true;
            weapons.add(
                    new Weapon("injector"){{
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
            aiController = SuicideAI::new;
            weapons.add(
                new Weapon(){{
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
            aiController = MissileAI::new;
            weapons.add(
                new Weapon("missile-launcher"){{
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
                        backColor = Color.valueOf("51a0b0");
                        frontColor = Color.valueOf("8ae3df");
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
            aiController = HugAI::new;
            abilities.add(new HackFieldAbility(FOSStatuses.hacked, 40f, 0.005f));
            constructor = UnitEntity::create;
        }};
        marshal = new UnitType("marshal"){{
            health = 18000;
            hitSize = 36;
            speed = 0.8f;
            range = 280f;
            flying = true;
            weapons.add(
                new Weapon(){{
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
                        backColor = Color.valueOf("51a0b0");
                        frontColor = Color.valueOf("8ae3df");
                    }};
                }}
            );
            constructor = UnitEntity::create;
        }};

        hidden = new HiddenUnitType("hidden");
    }
}
