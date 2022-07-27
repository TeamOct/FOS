package fos.content;

import arc.math.*;
import arc.struct.*;
import fos.type.bullets.InjectorBulletType;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.type.*;

public class FOSUnits {
    public static UnitType
    //mechs
    mwArtillery, mwShotgun, mwStandard, mwMiner, legion,
    //flying
    whip;

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
            constructor = () -> new MechUnit(){};
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
            constructor = () -> new MechUnit(){};
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
            constructor = () -> new MechUnit(){};
        }};
        mwMiner = new UnitType("mw-miner"){{
            health = 200;
            rotateSpeed = 10;
            mineTier = 1;
            mineSpeed = 0.5f;
            range = 30;
            weapons = Seq.with();
            aiController = () -> new MinerAI(){};
            constructor = () -> new UnitEntity(){};
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
            constructor = () -> new MechUnit(){};
        }};

        whip = new UnitType("whip"){{
            health = 150;
            hitSize = 6;
            speed = 1.2f;
            weapons.add(
                    new Weapon("injector"){{
                        bullet = new InjectorBulletType(0, 0.15f, 50, 300, false);
                        x = 0; y = 0;
                        reload = 60 * 20;
                        ejectEffect = Fx.casing1;
                    }}
            );
            constructor = () -> new UnitEntity(){};
        }};
    }
}
