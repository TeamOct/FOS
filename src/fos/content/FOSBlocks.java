package fos.content;

import arc.struct.*;
import fos.type.blocks.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.units.*;
import mindustry.world.meta.*;

import static mindustry.type.ItemStack.*;

public class FOSBlocks {
    public static Block
    //crafting
    mechSeparator,
    //production
    oreDetector,
    //defense
    meteoriteWall, meteoriteWallLarge, pulse, thunder,
    //environment & ores
    cyanium, cyaniumWall, meteoriteBlock, meteoriteFloor, oreTin, oreAluminium,
    //units
    moonwalkerFactory, reconstructorArtillery, reconstructorShotgun,
    //special
    nukeLauncher;

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
        //region production
        oreDetector = new OreDetector("ore-detector"){{
            health = 960;
            size = 3;
            requirements(Category.production, with(FOSItems.tin, 75));
            consumePower(0.5f);
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
            shootType = FOSBullets.thunderLaser;
            shootEffect = Fx.shootBigSmoke2;
            shootSound = Sounds.laserbig;
            loopSound = Sounds.beam;
            requirements(Category.turret, with(Items.silicon, 5));
        }};
        //endregion
        //region environment & ores
        cyanium = new Floor("cyanium"){{
            variants = 4;
        }};
        cyaniumWall = new StaticWall("cyanium-wall"){{
            variants = 4;
        }};
        meteoriteBlock = new StaticWall("meteorite-block"){{
            variants = 3;
        }};

        meteoriteFloor = new OreBlock("ore-meteorite"){{
            itemDrop = FOSItems.meteorite;
        }};
        oreTin = new UndergroundOreBlock("ore-tin"){{
            itemDrop = FOSItems.tin;
        }};
        oreAluminium = new UndergroundOreBlock("ore-aluminium"){{
            itemDrop = FOSItems.aluminium;
        }};
        //endregion
        //region units
        moonwalkerFactory = new UnitFactory("moonwalker-factory"){{
            health = 800;
            size = 4;
            requirements(Category.units, with(Items.copper, 200, Items.lead, 300));
            plans = Seq.with(
                    new UnitPlan(FOSUnits.mwStandard, 3600, with(Items.copper, 50, Items.lead, 30)),
                    new UnitPlan(FOSUnits.mwMiner, 5400, with(Items.copper, 90, Items.lead, 45))
            );
        }};
        reconstructorArtillery = new Reconstructor("mw-reconst-artillery"){{
            health = 800;
            size = 4;
            update = true;
            constructTime = 1800;
            consumeItems(with(Items.lead, 70, Items.scrap, 20));
            requirements(Category.units, with(Items.lead, 70, Items.scrap, 20));
            upgrades.addAll(
                    new UnitType[]{FOSUnits.mwStandard, FOSUnits.mwArtillery}
            );
        }};
        reconstructorShotgun = new Reconstructor("mw-reconst-shotgun"){{
            health = 800;
            size = 4;
            update = true;
            constructTime = 1800;
            consumeItems(with(Items.lead, 60, Items.scrap, 50));
            requirements(Category.units, with(Items.copper, 500, Items.lead, 600));
            upgrades.addAll(
                    new UnitType[]{FOSUnits.mwStandard, FOSUnits.mwShotgun}
            );
        }};
        //endregion
        //region special
        nukeLauncher = new NukeLauncher("rocket-silo"){{
            health = 7500;
            size = 5;
            hasItems = true;
            itemCapacity = 500;
            breakable = true;
            solid = true;
            update = true;
            configurable = true;
            consumePower(15);
            consumeItems(with(Items.lead, 500, Items.graphite, 500, Items.silicon, 500, Items.thorium, 500, Items.surgeAlloy, 500));
            requirements(Category.effect, BuildVisibility.campaignOnly, with(Items.lead, 5000, Items.silicon, 5000, Items.titanium, 3500, Items.thorium, 2500, Items.phaseFabric, 1500, Items.surgeAlloy, 1500));
        }};
    }
}
