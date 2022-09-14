package fos.content;

import arc.graphics.*;
import arc.struct.*;
import fos.type.blocks.environment.*;
import fos.type.blocks.power.*;
import fos.type.blocks.production.*;
import fos.type.blocks.special.*;
import fos.type.blocks.storage.*;
import fos.type.blocks.units.*;
import fos.type.draw.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import multicraft.*;

import static fos.content.FOSItems.*;
import static mindustry.type.ItemStack.*;

public class FOSBlocks {
    public static Block
    //crafting
    mechSeparator, resourceExtractor,
    //production
    rockCrusher, drillBase2, tinDrill, oreDetectorSmall, oreDetector,
    //distribution
    spaceDuct, itemCatapult, tinBelt,
    //power
    windTurbine, heatGenerator, plasmaLauncher,
    //defense
    tinWall, tinWallLarge, silverWall, silverWallLarge, particulator, pulse, thunder,
    //environment & ores
    cyanium, cyaniumWall, crimsonStone, crimsonStoneWall, elithite, elithiteWall, elbium, elbiumWall, nethratium, nethratiumWall, annite, anniteWall, oreTin, oreTinSurface, oreSilver, oreLithium,
    //units
    upgradeCenter,
    //storage
    coreColony, coreFortress, coreCity, coreMetropolis,
    //special
    nukeLauncher, bigBoy, cliffDetonator;

    public static void load() {
        //region crafting
        mechSeparator = new Separator("mechanical-separator"){{
            hasItems = true;
            size = 2;
            itemCapacity = 10;
            requirements(Category.crafting, with(tin, 200, silver, 50));
            craftTime = 120;
            spinnerSpeed = 1f;
            results = with(tin, 3, silver, 1, Items.silicon, 2);
        }};
        resourceExtractor = new MultiCrafter("resource-extractor"){{
            itemCapacity = 15;
            size = 3;
            hasItems = acceptsItems = true;
            configurable = true;
            drawer = new DrawMulti(
                new DrawDiagonalPistons(){{
                    sides = 8;
                    sinScl = 6f;
                    lenOffset = 7f;
                }},
                new DrawDefault()
            );
            requirements(Category.crafting, with(rawNethratium, 50));
            consumePower(2f);

            resolvedRecipes = Seq.with(
                new Recipe(
                    new IOEntry(
                        Seq.with(ItemStack.with(rawNethratium, 2)),
                        Seq.with()
                    ),
                    new IOEntry(
                        Seq.with(ItemStack.with(aluminium, 1)),
                        Seq.with()
                    ),
                    60f
                ),
                new Recipe(
                    new IOEntry(
                        Seq.with(ItemStack.with(rawElbium, 5)),
                        Seq.with()
                    ),
                    new IOEntry(
                        Seq.with(ItemStack.with(tin, 1, lithium, 1)),
                        Seq.with()
                    ),
                    90f
                ),
                new Recipe(
                    new IOEntry(
                        Seq.with(ItemStack.with(rawElithite, 8)),
                        Seq.with()
                    ),
                    new IOEntry(
                        Seq.with(ItemStack.with(silver, 1, Items.titanium, 1)),
                        Seq.with()
                    ),
                    120f
                )
            );
        }};
        //endregion
        //region production
        rockCrusher = new HeatProducerDrill("rock-crusher"){{
            health = 300;
            size = 2;
            tier = 2;
            heatOutput = 4f;
            requirements(Category.production, with(rawNethratium, 30));
            envRequired = Env.space;
        }};
        drillBase2 = new DrillBase("drill-base-2"){{
            health = 120;
            size = 2;
            requirements(Category.production, with(tin, 10));
        }};
        tinDrill = new UndergroundDrill("tin-drill"){{
            health = 480;
            size = 2;
            tier = 3;
            requirements(Category.production, with(tin, 5));
        }};
        oreDetectorSmall = new OreDetector("ore-detector-small"){{
            health = 480;
            size = 2;
            range = 8*8f;
            requirements(Category.production, with(rawNethratium, 25, lithium, 30));
            consumePower(0.3f);
        }};
        oreDetector = new OreDetector("ore-detector"){{
            health = 960;
            size = 3;
            requirements(Category.production, with(tin, 50));
            consumePower(0.5f);
        }};
        //endregion
        //region defense
        tinWall = new Wall("tin-wall"){{
            scaledHealth = 400;
            size = 1;
            requirements(Category.defense, with(tin, 6));
        }};
        tinWallLarge = new Wall("tin-wall-large"){{
            scaledHealth = 400;
            size = 2;
            requirements(Category.defense, with(tin, 24));
        }};
        silverWall = new Wall("silver-wall"){{
            scaledHealth = 600;
            size = 1;
            requirements(Category.defense, with(silver, 6));
        }};
        silverWallLarge = new Wall("silver-wall-large"){{
            scaledHealth = 600;
            size = 2;
            requirements(Category.defense, with(silver, 24));
        }};

        particulator = new ItemTurret("particulator"){{
            health = 2400;
            size = 3;
            range = 120;
            targetAir = targetGround = true;
            recoil = 2;
            reload = 40;
            inaccuracy = 5;
            shootSound = Sounds.pew;
            ammo(
                tin, new BasicBulletType(2f, 80){{
                    lifetime = 60f;
                    width = 16f; height = 24f;
                    backColor = Color.valueOf("347043");
                    frontColor = trailColor = lightColor = Color.valueOf("85b374");
                    trailEffect = Fx.artilleryTrail;
                    trailWidth = 16;
                    trailLength = 20;
                    ammoMultiplier = 1;
                    splashDamage = 10f;
                    splashDamageRadius = 24f;
                    knockback = 3.2f;
                    fragOnHit = true;
                    hitEffect = despawnEffect = Fx.explosion;
                    fragBullets = 6;
                    fragBullet = new BasicBulletType(0.8f, 10){{
                        lifetime = 60f * 30; //frags will stay for pretty long
                        drag = 0.024f;
                        width = height = 6f;
                        backColor = Color.valueOf("347043");
                        frontColor = trailColor = Color.valueOf("85b374");
                        trailEffect = Fx.artilleryTrail;
                        trailLength = 8;
                        pierceArmor = true;
                        collidesAir = false;
                        collidesTiles = true;
                        hitEffect = Fx.hitBulletSmall;
                        despawnEffect = Fx.none;
                    }};
                }},
                silver, new BasicBulletType(2f, 120){{
                    lifetime = 60f;
                    width = 16f; height = 24f;
                    backColor = Color.valueOf("813ba1");
                    frontColor = trailColor = lightColor = Color.valueOf("b38bb3");
                    trailEffect = Fx.artilleryTrail;
                    trailWidth = 16;
                    trailLength = 20;
                    ammoMultiplier = 1;
                    splashDamage = 25f;
                    splashDamageRadius = 24f;
                    knockback = 4f;
                    fragOnHit = true;
                    hitEffect = despawnEffect = Fx.explosion;
                    fragBullets = 7;
                    fragBullet = new BasicBulletType(0.8f, 16){{
                        lifetime = 60f * 30; //frags will stay for pretty long
                        drag = 0.024f;
                        width = height = 6f;
                        backColor = Color.valueOf("813ba1");
                        frontColor = trailColor = Color.valueOf("b38bb3");
                        trailEffect = Fx.artilleryTrail;
                        trailLength = 8;
                        pierceArmor = true;
                        collidesAir = false;
                        collidesTiles = true;
                        hitEffect = Fx.hitBulletSmall;
                        despawnEffect = Fx.none;
                    }};
                }}
            );
            requirements(Category.turret, with(tin, 200, silver, 75));
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
            requirements(Category.distribution, with(rawNethratium, 1));
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
            requirements(Category.distribution, with(rawNethratium, 120, lithium, 50));
            envRequired = Env.space;
        }};
        tinBelt = new StackConveyor("tin-belt"){{
            health = 10;
            size = 1;
            speed = 0.2f;
            itemCapacity = 5;
            consumesPower = true;
            conductivePower = true;
            baseEfficiency = 1f;
            consumePower(1f / 60f);
            requirements(Category.distribution, with(tin, 1));
        }};
        //endregion
        //region power
        windTurbine = new WindTurbine("wind-turbine"){{
            health = 480;
            size = 2;
            powerProduction = 0.25f;
            requirements(Category.power, with(tin, 40));
        }};
        heatGenerator = new HeatGenerator("heat-generator"){{
            health = 480;
            size = 2;
            heatInput = 14f;
            powerProduction = 3f;
            envEnabled |= Env.space;
            requirements(Category.power, with(rawNethratium, 45));
        }};
        plasmaLauncher = new PlasmaLauncher("plasma-launcher"){{
            health = 1500;
            size = 3;
            envEnabled |= Env.space;
            requirements(Category.power, with(rawNethratium, 125, lithium, 90, Items.titanium, 75));
        }};
        //endregion
        //region environment & ores
        cyanium = new Floor("cyanium"){{
            variants = 4;
        }};
        cyaniumWall = new StaticWall("cyanium-wall"){{
            variants = 4;
        }};
        crimsonStone = new Floor("crimson-stone"){};
        crimsonStoneWall = new StaticWall("crimson-stone-wall"){{
            variants = 1;
        }};
        elithite = new Floor("elithite"){{
            itemDrop = rawElithite;
            variants = 4;
        }};
        elithiteWall = new StaticWall("elithite-wall"){};
        elbium = new Floor("elbium"){{
            itemDrop = rawElbium;
            variants = 4;
        }};
        elbiumWall = new StaticWall("elbium-wall"){};
        nethratium = new Floor("nethratium"){{
            itemDrop = rawNethratium;
            variants = 4;
        }};
        nethratiumWall = new StaticWall("nethratium-wall"){};
        annite = new Floor("annite"){{
            variants = 4;
        }};
        anniteWall = new StaticWall("annite-wall"){};
        oreTin = new UndergroundOreBlock("ore-tin"){{
            itemDrop = tin;
        }};
        oreTinSurface = new OreBlock("ore-tin-surface"){{
            itemDrop = tin;
        }};
        oreSilver = new UndergroundOreBlock("ore-silver"){{
            itemDrop = silver;
        }};
        oreLithium = new OreBlock("ore-lithium"){{
            itemDrop = lithium;
        }};
        //endregion
        //region units
        upgradeCenter = new UpgradeCenter("upgrade-center"){{
            health = 1500;
            size = 3;
            consumePower(3f);
            requirements(Category.units, with(tin, 250, silver, 200));
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
            requirements(Category.effect, with(tin, 1500));
        }};
        coreFortress = new LuminaCoreBlock("core-fortress"){{
            health = 2800;
            size = 3;
            unitCapModifier = 5;
            itemCapacity = 2500;
            unitType = FOSUnits.temp;
            requirements(Category.effect, with(tin, 2000, silver, 1250));
        }};
        coreCity = new LuminaCoreBlock("core-city"){{
            health = 4600;
            size = 4;
            unitCapModifier = 7;
            itemCapacity = 5000;
            unitType = FOSUnits.temp;
            requirements(Category.effect, with(tin, 2500, silver, 2000 /*TODO more items soon(tm)*/));
        }};
        coreMetropolis = new LuminaCoreBlock("core-metropolis"){{
            health = 8000;
            size = 5;
            unitCapModifier = 10;
            itemCapacity = 8000;
            unitType = FOSUnits.temp;
            requirements(Category.effect, with(tin, 4500, silver, 3500 /*TODO*/));
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
            consumeItems(with(Items.titanium, 5000, tin, 5000, silver, 5000));
            requirements(Category.effect, with(tin, 10000, silver, 10000));
        }};
        cliffDetonator = new CliffExplosive("cliff-detonator"){{
            health = 40;
            size = 1;
            requirements(Category.effect, with(Items.titanium, 75, lithium, 150));
        }};
    }
}
