package fos.content;

import arc.graphics.*;
import arc.struct.*;
import fos.type.blocks.defense.*;
import fos.type.blocks.environment.*;
import fos.type.blocks.power.*;
import fos.type.blocks.production.*;
import fos.type.blocks.special.*;
import fos.type.blocks.storage.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
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
    meteoriteDrill, drillBase2, tinDrill, oreDetectorSmall, oreDetector,
    //distribution
    spaceDuct, itemCatapult,
    //power
    windTurbine, heatGenerator, plasmaLauncher,
    //defense
    meteoriteWall, meteoriteWallLarge, particulator, pulse, thunder,
    //environment & ores
    cyanium, cyaniumWall, elithite, elithiteWall, elbium, elbiumWall, meteoriteBlock, meteoriteFloor, oreTin, oreSilver, oreLithium,
    //units
    moonwalkerFactory, reconstructorArtillery, reconstructorShotgun,
    //storage
    coreColony, coreFortress, coreCity, coreMetropolis,
    //special
    nukeLauncher, bigBoy, stationPlatform, cliffDetonator;

    public static void load() {
        //region crafting
        mechSeparator = new Separator("mechanical-separator"){{
            hasItems = true;
            size = 2;
            itemCapacity = 10;
            requirements(Category.crafting, with(FOSItems.tin, 200, FOSItems.silver, 50));
            craftTime = 120;
            spinnerSpeed = 1f;
            results = with(FOSItems.tin, 3, FOSItems.silver, 1, Items.silicon, 2);
        }};
        //endregion
        //region production
        meteoriteDrill = new HeatProducerDrill("meteorite-drill"){{
            health = 960;
            size = 2;
            tier = 2;
            requirements(Category.production, with(FOSItems.meteorite, 30));
            envRequired = Env.space;
        }};
        drillBase2 = new DrillBase("drill-base-2"){{
            health = 120;
            size = 2;
            requirements(Category.production, with(FOSItems.tin, 10));
        }};
        tinDrill = new UndergroundDrill("tin-drill"){{
            health = 480;
            size = 2;
            tier = 102;
            requirements(Category.production, with(FOSItems.tin, 5));
        }};
        oreDetectorSmall = new OreDetector("ore-detector-small"){{
            health = 480;
            size = 2;
            range = 8*8f;
            requirements(Category.production, with(FOSItems.meteorite, 25, FOSItems.lithium, 30));
            consumePower(0.3f);
        }};
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

        particulator = new ItemTurret("particulator"){{
            health = 2400;
            size = 3;
            range = 96;
            targetAir = targetGround = true;
            recoil = 2;
            reload = 40;
            inaccuracy = 5;
            shootSound = Sounds.pew;
            ammo(
                FOSItems.tin, new BasicBulletType(1.6f, 240){{
                    lifetime = 60f;
                    width = 16f; height = 24f;
                    backColor = Color.valueOf("347043");
                    frontColor = trailColor = lightColor = Color.valueOf("85b374");
                    trailEffect = Fx.artilleryTrail;
                    trailInterval = 20f;
                    ammoMultiplier = 1;
                    splashDamage = 10f;
                    splashDamageRadius = 24f;
                    knockback = 3.2f;
                    fragOnHit = true;
                    hitEffect = despawnEffect = Fx.explosion;
                    fragBullets = 6;
                    fragBullet = new BasicBulletType(0.8f, 30){{
                        lifetime = 60f * 30; //frags will stay for pretty long
                        drag = 0.024f;
                        width = height = 6f;
                        backColor = Color.valueOf("347043");
                        frontColor = trailColor = Color.valueOf("85b374");
                        trailEffect = Fx.artilleryTrail;
                        pierceArmor = true;
                        collidesAir = false;
                        collidesTiles = true;
                        hitEffect = Fx.hitBulletSmall;
                        despawnEffect = Fx.none;
                    }};
                }},
                FOSItems.silver, new BasicBulletType(1.6f, 360){{
                    lifetime = 60f;
                    width = 16f; height = 24f;
                    backColor = Color.valueOf("813ba1");
                    frontColor = trailColor = lightColor = Color.valueOf("b38bb3");
                    trailEffect = Fx.artilleryTrail;
                    trailInterval = 20f;
                    ammoMultiplier = 1;
                    splashDamage = 25f;
                    splashDamageRadius = 24f;
                    knockback = 4f;
                    fragOnHit = true;
                    hitEffect = despawnEffect = Fx.explosion;
                    fragBullets = 7;
                    fragBullet = new BasicBulletType(0.8f, 48){{
                        lifetime = 60f * 30; //frags will stay for pretty long
                        drag = 0.024f;
                        width = height = 6f;
                        backColor = Color.valueOf("813ba1");
                        frontColor = trailColor = Color.valueOf("b38bb3");
                        trailEffect = Fx.artilleryTrail;
                        pierceArmor = true;
                        collidesAir = false;
                        collidesTiles = true;
                        hitEffect = Fx.hitBulletSmall;
                        despawnEffect = Fx.none;
                    }};
                }}
            );
            requirements(Category.turret, with(FOSItems.tin, 200, FOSItems.silver, 75));
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
        /*TODO currently crashes the game
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
        }};*/
        //endregion
        //region distribution
        spaceDuct = new Duct("space-duct"){{
            health = 10;
            size = 1;
            requirements(Category.distribution, with(FOSItems.meteorite, 1));
            envRequired = Env.space;
        }};
        itemCatapult = new MassDriver("item-catapult"){{
            health = 480;
            size = 2;
            range = 30f * 8;
            bullet = new MassDriverBolt(){{
                speed = 0.5f;
                damage = 1f;
            }};
            consumePower(1f / 6f);
            requirements(Category.distribution, with(FOSItems.meteorite, 120, FOSItems.lithium, 50));
            envRequired = Env.space;
        }};
        //endregion
        //region power
        windTurbine = new WindTurbine("wind-turbine"){{
            health = 480;
            size = 2;
            powerProduction = 3f;
            requirements(Category.power, with(FOSItems.tin, 80));
        }};
        heatGenerator = new HeatGenerator("heat-generator"){{
            health = 480;
            size = 2;
            heatInput = powerProduction = 3f;
            envEnabled |= Env.space;
            requirements(Category.power, with(FOSItems.meteorite, 45));
        }};
        plasmaLauncher = new PlasmaLauncher("plasma-launcher"){{
            health = 1500;
            size = 3;
            envEnabled |= Env.space;
            requirements(Category.power, with(FOSItems.meteorite, 125, FOSItems.lithium, 90, Items.titanium, 75));
        }};
        //endregion
        //region environment & ores
        cyanium = new Floor("cyanium"){{
            variants = 4;
        }};
        cyaniumWall = new StaticWall("cyanium-wall"){{
            variants = 4;
        }};
        elithite = new Floor("elithite"){{
            variants = 4;
        }};
        elithiteWall = new StaticWall("elithite-wall"){};
        elbium = new Floor("elbium"){{
            variants = 4;
        }};
        elbiumWall = new StaticWall("elbium-wall"){};
        meteoriteBlock = new StaticWall("meteorite-block"){{
            variants = 3;
        }};
        meteoriteFloor = new Floor("meteorite-floor"){{
            itemDrop = FOSItems.meteorite;
        }};
        oreTin = new UndergroundOreBlock("ore-tin"){{
            itemDrop = FOSItems.tin;
        }};
        oreSilver = new UndergroundOreBlock("ore-silver"){{
            itemDrop = FOSItems.silver;
        }};
        oreLithium = new OreBlock("ore-lithium"){{
            itemDrop = FOSItems.lithium;
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
        //region storage
        coreColony = new LuminaCoreBlock("core-colony"){{
            //no radar
            radarRange = 0f;
            configurable = false;

            health = 1920;
            size = 2;
            itemCapacity = 250;
            requirements(Category.effect, with(FOSItems.tin, 1500));
        }};
        coreFortress = new LuminaCoreBlock("core-fortress"){{
            health = 2800;
            size = 3;
            itemCapacity = 2500;
            requirements(Category.effect, with(FOSItems.tin, 2000, FOSItems.silver, 1250));
        }};
        coreCity = new LuminaCoreBlock("core-city"){{
            health = 4600;
            size = 4;
            itemCapacity = 5000;
            requirements(Category.effect, with(FOSItems.tin, 2500, FOSItems.silver, 2000 /*TODO more items soon(tm)*/));
        }};
        coreMetropolis = new LuminaCoreBlock("core-metropolis"){{
            health = 8000;
            size = 5;
            itemCapacity = 8000;
            requirements(Category.effect, with(FOSItems.tin, 4500, FOSItems.silver, 3500 /*TODO*/));
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
        bigBoy = new GiantNukeLauncher("big-boy"){{
            health = 20000;
            size = 9;
            hasItems = true;
            itemCapacity = 5000;
            breakable = true;
            solid = true;
            update = true;
            configurable = true;
            consumePower(60);
            consumeItems(with(FOSItems.tin, 5000, FOSItems.silver, 5000));
            requirements(Category.effect, with(FOSItems.tin, 10000, FOSItems.silver, 10000));
        }};
        /*TODO broken
        stationPlatform = new StationPlatform("station-platform"){{
            health = 160;
            size = 1;
            requirements(Category.effect, with(FOSItems.meteorite, 5, FOSItems.tin, 3));
        }};
        */
        cliffDetonator = new CliffExplosive("cliff-detonator"){{
            health = 40;
            size = 1;
            requirements(Category.effect, with(Items.titanium, 75, FOSItems.lithium, 150));
        }};
    }
}
