package fos.content;

import arc.struct.Seq;
import fos.entities.bullets.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;

public class FOSUnits {
    public static UnitType mwArtillery, mwShotgun, mwStandard, mwMiner;

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
                        bullet = new SmallArtillery();
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
                        bullet = new SmallStandardFlak();
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
                        bullet = new SmallStandard();
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
    }
}
