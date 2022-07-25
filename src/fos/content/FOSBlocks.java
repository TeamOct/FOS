package fos.content;

import fos.entities.bullets.*;
import fos.type.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;

import static mindustry.type.ItemStack.*;

public class FOSBlocks {
    public static Block
    //crafting
    mechSeparator,
    //defense
    meteoriteWall, meteoriteWallLarge, pulse, thunder,
    //environment & ores
    meteoriteBlock, meteoriteFloor, oreLuminium;

    public static void load() {
        //region crafting
        mechSeparator = new Separator("mechanical-separator"){{
            hasItems = true;
            size = 2;
            itemCapacity = 10;
            requirements(Category.crafting, with(Items.copper, 200, Items.lead, 50));
            craftTime = 120;
            spinnerSpeed = 1f;
            results = with(Items.lead, 3, Items.graphite, 1);
        }};
        //endregion
        //region defense
        meteoriteWall = new MeteoriteWall("meteorite-wall"){{
            health = 520;
            size = 1;
            requirements(Category.defense, with(FOSItems.meteorite, 6));
        }};
        meteoriteWallLarge = new MeteoriteWall("meteorite-wall-large"){{
            health = 2080;
            size = 2;
            requirements(Category.defense, with(FOSItems.meteorite, 24));
        }};

        pulse = new TractorBeamTurret("pulse"){{
            health = 2400;
            size = 3;
            range = 160;
            targetAir = targetGround = true;
            retargetTime = 60;
            status = StatusEffects.disarmed;
            statusDuration = 60;
            damage = 0;
            force = 0;
            scaledForce = 0;
            consumePower(4);
            requirements(Category.turret, with(Items.copper, 5));
        }};
        thunder = new LaserTurret("thunder"){{
            health = 3200;
            size = 5;
            shake = 4;
            recoil = 5;
            range = 190;
            reload = 50;
            shootCone = 5;
            firingMoveFract = 0.5f;
            shootDuration = 220;
            shootType = new ThunderLaser();
            shootEffect = Fx.shootBigSmoke2;
            shootSound = Sounds.laserbig;
            loopSound = Sounds.beam;
            requirements(Category.turret, with(Items.silicon, 5));
        }};
        //endregion
        //region environment & ores
        meteoriteBlock = new StaticWall("meteorite-block"){{
            variants = 3;
        }};
        meteoriteFloor = new OreBlock("meteorite-floor"){{
            itemDrop = FOSItems.meteorite;
        }};
        oreLuminium = new OreBlock("ore-luminium"){{
            itemDrop = FOSItems.luminium;
        }};
        //endregion
        //region units

        //endregion
    }
}
