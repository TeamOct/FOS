package fos.content;

import arc.graphics.*;
import arc.struct.*;
import fos.graphics.*;
import fos.type.blocks.campaign.*;
import fos.type.blocks.defense.*;
import fos.type.blocks.distribution.*;
import fos.type.blocks.environment.*;
import fos.type.blocks.power.*;
import fos.type.blocks.production.*;
import fos.type.blocks.special.*;
import fos.type.blocks.storage.*;
import fos.type.blocks.units.*;
import fos.type.bullets.*;
import fos.type.draw.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.CacheLayer;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import multicraft.*;

import static fos.content.FOSItems.*;
import static fos.content.FOSFluids.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.type.ItemStack.*;

public class FOSBlocks {
    public static Block
    //crafting
    resourceExtractor, cuberiumSynthesizer, sublimator, siliconSynthesizer, brassSmelter,
    //production
    rockCrusher, drillBase, drillBaseLarge, tinDrill, silverDrill, diamondDrill, vanadiumDrill, oreDetectorSmall, oreDetector,
    //distribution
    spaceDuct, spaceRouter, spaceBridge, itemCatapult, tinBelt,
    //liquids
    fluidPipe,
    //power
    tinWire, copperWire, brassWire, windTurbine, heatGenerator, plasmaLauncher, solarPanelMedium,
    //defense
    tinWall, tinWallLarge, diamondWall, diamondWallLarge, vanadiumWall, vanadiumWallLarge, helix, sticker, particulator, pulse, thunder, cluster, matrixShieldProj,
    //environment & ores
    cyanium, cyaniumWall, crimsonStone, crimsonStoneWall, elithite, elithiteWall, elbium, elbiumWall, nethratium, nethratiumWall,
    annite, anniteWall, blublu, blubluWall, purpur, purpurWall,
    tokiciteFloor,
    cyaniumWater, crimsonStoneWater, anniteWater, blubluWater, purpurWater,
    alienMoss,
    oreCopper, oreTin, oreTinSurface, oreSilver, oreLithium, oreDiamond, oreVanadium, oreIridium, oreLuminium,
    bugSpawn,
    //units
    upgradeCenter, hovercraftFactory,
    //storage
    coreColony, coreFortress, coreCity, coreMetropolis, lightUnloader,
    //special
    nukeLauncher, bigBoy, cliffDetonator, orbitalAccelerator, mechResearchCore, bioResearchCore;

    public static void load() {
        //region crafting
        resourceExtractor = new MultiCrafter("resource-extractor"){{
            itemCapacity = 15;
            size = 3;
            hasItems = acceptsItems = true;
            configurable = true;
            envRequired = envEnabled = Env.space;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawDiagonalPistons(){{
                    sides = 8;
                    sinScl = 6f;
                    lenOffset = 7f;
                }},
                new DrawDefault()
            );
            requirements(Category.crafting, with(rawNethratium, 50, rawElithite, 25));
            consumePower(2f);

            resolvedRecipes = Seq.with(
                new Recipe(
                    new IOEntry(
                        Seq.with(with(rawNethratium, 2)),
                        Seq.with()
                    ),
                    new IOEntry(
                        Seq.with(with(aluminium, 1)),
                        Seq.with()
                    ),
                    60f
                ),
                new Recipe(
                    new IOEntry(
                        Seq.with(with(rawElbium, 4)),
                        Seq.with()
                    ),
                    new IOEntry(
                        Seq.with(with(tin, 1, lithium, 1)),
                        Seq.with()
                    ),
                    90f
                ),
                new Recipe(
                    new IOEntry(
                        Seq.with(with(rawElithite, 6)),
                        Seq.with()
                    ),
                    new IOEntry(
                        Seq.with(with(silver, 1, titanium, 1)),
                        Seq.with()
                    ),
                    120f
                )
            );
        }};
        cuberiumSynthesizer = new GenericCrafter("cuberium-synthesizer"){{
            scaledHealth = 10;
            size = 3;
            hasItems = true;
            hasPower = consumesPower = true;
            envEnabled = envRequired = Env.space;
            consumePower(4f);
            consumeItems(with(tin, 10, silver, 10, titanium, 5));
            consumeLiquid(oxygen, 3f/60f);
            outputItems = with(cuberium, 5);
            craftTime = 60f;
            requirements(Category.crafting, with(aluminium, 100, tin, 75, silver, 75, titanium, 100));
        }};
        sublimator = new AttributeCrafter("sublimator"){{
            scaledHealth = 10;
            size = 3;
            rotate = invertFlip = true;
            attribute = Attribute.water;
            baseEfficiency = 0f;
            minEfficiency = 0.1f;
            boostScale = 0.3f;
            consumePower(0.8f);
            outputLiquids = LiquidStack.with(hydrogen, 6f/60f, oxygen, 3f/60f);
            liquidOutputDirections = new int[]{1, 3};
            craftTime = 10f;
            envEnabled = envRequired = Env.space;
            requirements(Category.crafting, with(aluminium, 150, tin, 100, titanium, 100));
        }};
        siliconSynthesizer = new GenericCrafter("silicon-synthesizer"){{
            scaledHealth = 40;
            size = 4;
            craftTime = 120f;
            itemCapacity = 16;
            consumePower(8f);
            consumeItems(with(diamond, 1, sand, 8));
            outputItems = with(silicon, 8);
            requirements(Category.crafting, with(tin, 360, silver, 300, diamond, 200));
        }};
        brassSmelter = new GenericCrafter("brass-smelter"){{
            scaledHealth = 40;
            size = 3;
            craftTime = 40f;
            hasItems = true;
            hasPower = true;
            hasLiquids = true;
            liquidCapacity = 60f;
            squareSprite = false;
            consumePower(4.5f);
            consumeItems(with(copper, 3, tin, 1));
            consumeLiquid(tokicite, 0.5f);
            outputItems = with(brass, 1);
            requirements(Category.crafting, with(tin, 120, copper, 80, silicon, 160));
            craftEffect = FOSFx.brassSmelterCraft;
            drawer = new DrawMulti(
                //new DrawRegion("-bottom"),
                //new DrawLiquidRegion(tokicite),
                new DrawDefault()
            );
        }};
        //endregion
        //region production
        rockCrusher = new HeatProducerDrill("rock-crusher"){{
            health = 300;
            size = 2;
            tier = 2;
            heatOutput = 4f;
            squareSprite = false;
            requirements(Category.production, with(rawNethratium, 30));
            researchCost = with(rawNethratium, 150);
            envRequired = envEnabled = Env.space;
        }};
        drillBase = new DrillBase("drill-base"){{
            scaledHealth = 30;
            size = 2;
            envEnabled |= Env.space;
            requirements(Category.production, with(tin, 10));
            researchCost = with(tin, 100);
        }};
        drillBaseLarge = new DrillBase("drill-base-large"){{
            scaledHealth = 30;
            size = 3;
            requirements(Category.production, with(silver, 45, silicon, 50));
        }};
        tinDrill = new UndergroundDrill("tin-drill"){{
            size = 2;
            tier = 3;
            drillTime = 360f;
            envEnabled |= Env.space;
            requirements(Category.production, with(tin, 5));
            researchCost = with(tin, 50);
            consumeLiquid(water, 0.08f).boost();
        }};
        silverDrill = new UndergroundDrill("silver-drill"){{
            size = 2;
            tier = 4;
            drillTime = 300f;
            requirements(Category.production, with(silver, 10));
            consumeLiquid(water, 0.1f).boost();
        }};
        diamondDrill = new UndergroundDrill("diamond-drill"){{
            size = 3;
            tier = 5;
            drillTime = 300f;
            consumePower(2f);
            requirements(Category.production, with(silicon, 25, diamond, 40));
            consumeLiquid(tokicite, 0.2f).boost();
        }};
        vanadiumDrill = new UndergroundDrill("vanadium-drill"){{
            size = 3;
            tier = 6;
            drillTime = 270f;
            consumePower(3f);
            requirements(Category.production, with(tin, 30, silver, 50, vanadium, 50));
            consumeLiquid(tokicite, 0.25f).boost();
        }};
        oreDetectorSmall = new OreDetector("ore-detector-small"){{
            health = 480;
            size = 2;
            range = 8*8f;
            envRequired = envEnabled = Env.space;
            requirements(Category.production, with(aluminium, 25, lithium, 30));
            consumePower(0.3f);
        }};
        oreDetector = new OreDetector("ore-detector"){{
            health = 960;
            size = 3;
            requirements(Category.production, with(tin, 50));
            consumePower(0.5f);
            researchCost = with(tin, 150);
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
        diamondWall = new Wall("diamond-wall"){{
            scaledHealth = 625;
            size = 1;
            requirements(Category.defense, with(diamond, 6));
        }};
        diamondWallLarge = new Wall("diamond-wall-large"){{
            scaledHealth = 625;
            size = 2;
            requirements(Category.defense, with(diamond, 24));
        }};
        vanadiumWall = new Wall("vanadium-wall"){{
            scaledHealth = 800;
            size = 1;
            requirements(Category.defense, with(vanadium, 6));
        }};
        vanadiumWallLarge = new Wall("vanadium-wall-large"){{
            scaledHealth = 800;
            size = 2;
            requirements(Category.defense, with(vanadium, 24));
        }};

        helix = new ItemTurret("helix"){{
            scaledHealth = 480;
            size = 2;
            range = 132;
            targetAir = targetGround = true;
            recoil = 1f;
            reload = 30f;
            inaccuracy = 0f;
            shootSound = Sounds.pew;
            shoot = new ShootHelix(){{
                shots = 2;
                mag = 2.5f;
            }};
            ammo(
                tin, new BasicBulletType(3f, 20){{
                    width = 3f; height = 6f;
                    lifetime = 44f;
                    trailColor = frontColor = FOSPal.tin;
                    backColor = FOSPal.tinBack;
                    trailWidth = 1.5f;
                    trailLength = 8;
                    ammoMultiplier = 4f;
                }},
                silver, new BasicBulletType(3f, 25){{
                    width = 3f; height = 6f;
                    lifetime = 44f;
                    trailColor = frontColor = FOSPal.silver;
                    backColor = FOSPal.silverBack;
                    trailWidth = 1.5f;
                    trailLength = 8;
                    ammoMultiplier = 4f;
                }},
                diamond, new BasicBulletType(3f, 35){{
                    width = 3f; height = 6f;
                    lifetime = 44f;
                    trailColor = frontColor = FOSPal.diamond;
                    backColor = FOSPal.diamondBack;
                    trailWidth = 1.5f;
                    trailLength = 8;
                    ammoMultiplier = 6f;
                    pierce = true;
                    pierceCap = 2;
                }}
            );
            requirements(Category.turret, with(tin, 60, silver, 50));
        }};
        sticker = new ItemTurret("sticker"){{
            scaledHealth = 480;
            size = 2;
            range = 150;
            targetAir = true;
            targetGround = false;
            recoil = 2f;
            reload = 30f;
            inaccuracy = 4f;
            outlineColor = Color.valueOf("302326");
            shootSound = Sounds.mud;
            consumeLiquid(tokicite, 0.1f);
            ammo(
                tin, new StickyBulletType(3f, 10, 300){{
                    lifetime = 50f;
                    width = height = 12f;
                    trailColor = FOSPal.tin;
                    frontColor = backColor = FOSPal.tokicite;
                    trailWidth = 3f;
                    trailLength = 8;
                    ammoMultiplier = 2f;
                    splashDamage = 40;
                    splashDamageRadius = 12f;
                }},
                diamond, new StickyBulletType(3f, 30, 300){{
                    lifetime = 50f;
                    width = height = 12f;
                    trailColor = FOSPal.diamond;
                    frontColor = backColor = FOSPal.tokicite;
                    trailWidth = 3f;
                    trailLength = 8;
                    ammoMultiplier = 3f;
                    splashDamage = 50;
                    splashDamageRadius = 16f;
                }}
            );
            requirements(Category.turret, with(tin, 75, silver, 100));
        }};
        particulator = new ItemTurret("particulator"){{
            health = 2400;
            size = 3;
            range = 120;
            targetAir = targetGround = true;
            recoil = 2;
            reload = 40;
            inaccuracy = 5;
            outlineColor = Color.valueOf("302326");
            shootSound = Sounds.shootBig;
            squareSprite = false;
            ammo(
                tin, new BasicBulletType(2f, 80){{
                    lifetime = 60f;
                    width = 16f; height = 24f;
                    backColor = FOSPal.tinBack;
                    frontColor = trailColor = lightColor = FOSPal.tin;
                    trailEffect = Fx.artilleryTrail;
                    trailWidth = 4;
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
                        backColor = FOSPal.tinBack;
                        frontColor = trailColor = FOSPal.tin;
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
                    backColor = FOSPal.silverBack;
                    frontColor = trailColor = lightColor = FOSPal.silver;
                    trailEffect = Fx.artilleryTrail;
                    trailWidth = 4;
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
                        backColor = FOSPal.silverBack;
                        frontColor = trailColor = FOSPal.silver;
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
            outlineColor = Color.valueOf("302326");
            squareSprite = false;
            consumePower(4);
            requirements(Category.turret, BuildVisibility.editorOnly, with());
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
        cluster = new ItemTurret("cluster"){{
            scaledHealth = 480;
            size = 4;
            range = 280f;
            reload = 6f;
            inaccuracy = 20f;
            targetAir = targetGround = true;
            recoil = 3f;
            shootSound = Sounds.missile;
            shoot = new ShootBarrel(){{
                barrels = new float[]{
                    -7f, 0f, 0f,
                    -5f, 0f, 0f,
                    -3f, 0f, 0f,
                    -1f, 0f, 0f,
                    1f, 0f, 0f,
                    3f, 0f, 0f,
                    5f, 0f, 0f,
                    7f, 0f, 0f
                };
            }};
            ammo(
                diamond, new MissileBulletType(5f, 10){{
                    lifetime = 56f;
                    width = height = 8f;
                    trailWidth = 3f;
                    trailLength = 15;
                    frontColor = trailColor = FOSPal.diamond;
                    backColor = FOSPal.diamondBack;
                    hitEffect = Fx.blastExplosion;
                    ammoMultiplier = 1f;
                    splashDamage = 120f;
                    splashDamageRadius = 50f;
                    homingPower = 0f;
                    fragBullets = 3;
                    fragBullet = new BasicBulletType(1f, 5){{
                        lifetime = 20f;
                        width = height = 8f;
                        frontColor = FOSPal.diamond;
                        backColor = FOSPal.diamondBack;
                        hitEffect = Fx.explosion;
                        splashDamage = 80f;
                        splashDamageRadius = 38f;
                    }};
                }}
            );
            requirements(Category.turret, with(silicon, 500, vanadium, 300, iridium, 250, luminium, 200));
        }};

        matrixShieldProj = new PolyForceProjector("matrix-shield-projector"){{
            health = 480;
            size = 3;
            shieldHealth = 500f;
            cooldownNormal = 2f;
            cooldownLiquid = 1.6f;
            cooldownBrokenBase = 0.5f;
            polygon = new float[]{
                -120, -40,
                120, -40,
                120, 40,
                -120, 40
            };
            consumePower(4f);
            requirements(Category.effect, with(diamond, 150, silicon, 200, vanadium, 125));
        }};
        //endregion
        //region distribution
        spaceDuct = new SpaceDuct("space-duct"){{
            health = 10;
            size = 1;
            requirements(Category.distribution, with(rawNethratium, 1));
            envRequired = Env.space;
        }};
        spaceRouter = new DuctRouter("space-router"){{
            health = 10;
            size = 1;
            requirements(Category.distribution, with(rawNethratium, 3));
            envRequired = Env.space;
        }};
        spaceBridge = new DuctBridge("space-bridge"){{
            health = 10;
            size = 1;
            requirements(Category.distribution, with(rawNethratium, 5, rawElithite, 3));
            envRequired = Env.space;
        }};
        itemCatapult = new MassDriver("item-catapult"){{
            health = 480;
            size = 2;
            range = 120f * 8;
            bullet = new MassDriverBolt(){{
                speed = 0.2f;
                lifetime = 2400f;
                damage = 1f;
            }};
            consumePower(1f / 6f);
            requirements(Category.distribution, with(aluminium, 120, lithium, 75, silver, 100, titanium, 125));
            envRequired = envEnabled = Env.space;
        }};
        //TODO something???
        tinBelt = new Duct("tin-belt"){{
            health = 10;
            requirements(Category.distribution, with(tin, 1));
        }};
        //endregion
        //region liquids
        fluidPipe = new Conduit("fluid-pipe"){{
            health = 5;
            size = 1;
            liquidCapacity = 20f;
            requirements(Category.liquid, with(tin, 1, silver, 1));
        }};
        //endregion
        //region power
        tinWire = new PowerWire("tin-wire"){{
            health = 3;
            consumesPower = true;
            consumePower(2f / 60f);
            requirements(Category.power, with(tin, 1));
        }};
        copperWire = new PowerWire("copper-wire"){{
            health = 5;
            consumesPower = true;
            consumePower(0.5f / 60f);
            requirements(Category.power, with(copper, 1));
        }};
        brassWire = new PowerWire("brass-wire"){{
            health = 8;
            //does not consume any power
            requirements(Category.power, with(brass, 1));
        }};
        windTurbine = new WindTurbine("wind-turbine"){{
            health = 480;
            size = 2;
            powerProduction = 0.8f;
            rotateSpeed = 1.8f;
            requirements(Category.power, with(tin, 40));
            researchCost = with(tin, 160);
        }};
        heatGenerator = new HeatGenerator("heat-generator"){{
            health = 480;
            size = 2;
            heatInput = 14f;
            powerProduction = 3f;
            envEnabled |= Env.space;
            requirements(Category.power, with(rawNethratium, 45));
            researchCost = with(rawNethratium, 135);
        }};
        plasmaLauncher = new PlasmaLauncher("plasma-launcher"){{
            health = 1500;
            size = 3;
            envRequired = envEnabled = Env.space;
            requirements(Category.power, with(aluminium, 125, lithium, 90, titanium, 75, cuberium, 50));
        }};
        solarPanelMedium = new SolarGenerator("solar-panel-medium"){{
            size = 2;
            powerProduction = 0.5f;
            requirements(Category.power, with(lithium, 50, silver, 75, rawElithite, 50));
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
        blublu = new Floor("blublu"){{
            variants = 4;
        }};
        blubluWall = new StaticWall("blublu-wall"){};
        purpur = new Floor("purpur"){{
            variants = 4;
        }};
        purpurWall = new StaticWall("purpur-wall"){};
        tokiciteFloor = new Floor("tokicite-floor"){{
            drownTime = 360f;
            status = StatusEffects.slow;
            speedMultiplier = 0.15f;
            variants = 0;
            liquidDrop = tokicite;
            isLiquid = true;
            cacheLayer = CacheLayer.tar;
            albedo = 1f;
        }};
        cyaniumWater = new Floor("cyanium-water"){{
            variants = 4;
            isLiquid = true;
            status = StatusEffects.wet;
            liquidDrop = water;
            cacheLayer = CacheLayer.water;
            wall = cyaniumWall;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
        crimsonStoneWater = new Floor("crimson-stone-water"){{
            isLiquid = true;
            status = StatusEffects.wet;
            liquidDrop = water;
            cacheLayer = CacheLayer.water;
            wall = crimsonStoneWall;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
        anniteWater = new Floor("annite-water"){{
            variants = 4;
            isLiquid = true;
            status = StatusEffects.wet;
            liquidDrop = water;
            cacheLayer = CacheLayer.water;
            wall = anniteWall;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
        blubluWater = new Floor("blublu-water"){{
            variants = 4;
            isLiquid = true;
            status = StatusEffects.wet;
            liquidDrop = water;
            cacheLayer = CacheLayer.water;
            wall = blubluWall;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
        purpurWater = new Floor("purpur-water"){{
            variants = 4;
            isLiquid = true;
            status = StatusEffects.wet;
            liquidDrop = water;
            cacheLayer = CacheLayer.water;
            wall = purpurWall;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
        alienMoss = new OverlayFloor("alien-moss"){};
        oreCopper = new OreBlock("ore-copper-surface"){{
            itemDrop = copper;
        }};
        oreTin = new UndergroundOreBlock("ore-tin"){{
            drop = tin;
            variants = 3;
        }};
        oreTinSurface = new OreBlock("ore-tin-surface"){{
            itemDrop = tin;
        }};
        oreSilver = new UndergroundOreBlock("ore-silver"){{
            drop = silver;
            variants = 3;
        }};
        oreLithium = new OreBlock("ore-lithium"){{
            itemDrop = lithium;
        }};
        oreDiamond = new OreBlock("ore-diamond"){{
            itemDrop = diamond;
        }};
        oreVanadium = new UndergroundOreBlock("ore-vanadium"){{
            drop = vanadium;
        }};
        oreIridium = new UndergroundOreBlock("ore-iridium"){{
            drop = iridium;
        }};
        //TODO we haven't decided yet, should it be underground or not
        oreLuminium = new OreBlock("ore-luminium"){{
            itemDrop = luminium;
        }};
        bugSpawn = new BugSpawn("bug-spawn"){{
            size = 3;
            interval = 20 * 60;
        }};
        //endregion
        //region units
        upgradeCenter = new UpgradeCenter("upgrade-center"){{
            health = 1500;
            size = 3;
            consumePower(3f);
            requirements(Category.units, with(tin, 250, silver, 200));
        }};
        hovercraftFactory = new UnitFactory("hovercraft-factory"){{
            scaledHealth = 120;
            size = 3;
            consumePower(5f);
            requirements(Category.units, with(tin, 100, silver, 75));
            plans.add(
                new UnitPlan(FOSUnits.vulture, 20f * 60, with(tin, 35))
            );
        }};
        //endregion
        //region storage
        coreColony = new DetectorCoreBlock("core-colony"){{
            //no radar
            radarRange = 0f;
            configurable = false;

            health = 1920;
            size = 2;
            breakable = true;
            itemCapacity = 250;
            unitCapModifier = 0;
            squareSprite = false;
            requirements(Category.effect, with(tin, 1500));
        }};
        coreFortress = new DetectorCoreBlock("core-fortress"){{
            health = 2800;
            size = 3;
            unitCapModifier = 5;
            itemCapacity = 2500;
            unitType = FOSUnits.lord;
            squareSprite = false;
            requirements(Category.effect, with(tin, 2000, silver, 1250));
        }};
        coreCity = new DetectorCoreBlock("core-city"){{
            health = 4600;
            size = 4;
            unitCapModifier = 7;
            itemCapacity = 5000;
            unitType = FOSUnits.lord;
            requirements(Category.effect, with(tin, 2500, silver, 2000, diamond, 1500));
        }};
        coreMetropolis = new DetectorCoreBlock("core-metropolis"){{
            health = 8000;
            size = 5;
            unitCapModifier = 10;
            itemCapacity = 8000;
            unitType = FOSUnits.lord;
            requirements(Category.effect, with(tin, 4500, silver, 3500, diamond, 3000));
        }};
        lightUnloader = new Unloader("light-unloader"){{
            health = 60;
            size = 1;
            speed = 60f / 5f;
            requirements(Category.effect, with(tin, 20, silver, 25));
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
            consumeItems(with(lead, 500, graphite, 500, silicon, 500, thorium, 500, surgeAlloy, 500));
            requirements(Category.effect, BuildVisibility.campaignOnly, with(lead, 5000, silicon, 5000, titanium, 3500, thorium, 2500, phaseFabric, 1500, surgeAlloy, 1500));
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
            buildCostMultiplier = 0.5f;
            consumePower(60);
            consumeItems(with(titanium, 5000, tin, 5000, silver, 5000));
            requirements(Category.effect, with(aluminium, 10000, rawElithite, 8000, tin, 16000, silver, 15000, titanium, 10000, cuberium, 8000));
        }};
        cliffDetonator = new CliffExplosive("cliff-detonator"){{
            health = 40;
            size = 1;
            requirements(Category.effect, with(titanium, 75, lithium, 150));
        }};
        orbitalAccelerator = new OrbitalAccelerator("orbital-accelerator"){{
            health = 5000;
            size = 5;
            envEnabled |= Env.space;
            hasPower = true;
            hasLiquids = true;
            liquidCapacity = 300;
            buildCostMultiplier = 0.2f;
            consumePower(10f);
            consumeLiquid(hydrogen, 300);
            launching = coreFortress;
            requirements(Category.effect, BuildVisibility.campaignOnly, with(aluminium, 5000, titanium, 3000, lithium, 2500, tin, 2500, silver, 2000, cuberium, 2000));
        }};
        mechResearchCore = new ResearchCore("mech-research-core"){{
            scaledHealth = 160;
            size = 3;
            hasPower = true;
            acceptsPayload = true;
            outputsPayload = false;
            consumePower(3f);
            requirements(Category.effect, with(tin, 250, silver, 300, silicon, 150));
        }};
    }
}
