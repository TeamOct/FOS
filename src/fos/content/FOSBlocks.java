package fos.content;

import arc.Core;
import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.*;
import fos.graphics.*;
import fos.type.blocks.campaign.*;
import fos.type.blocks.crafting.ResourceExtractor;
import fos.type.blocks.defense.*;
import fos.type.blocks.distribution.*;
import fos.type.blocks.environment.*;
import fos.type.blocks.power.*;
import fos.type.blocks.production.*;
import fos.type.blocks.special.*;
import fos.type.blocks.storage.DetectorCoreBlock;
import fos.type.blocks.units.*;
import fos.type.bullets.*;
import fos.type.draw.DrawOutputLiquids;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.Sounds;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.ConsumeLiquidFlammable;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import multicraft.*;

import static fos.content.FOSFluids.*;
import static fos.content.FOSItems.*;
import static fos.content.FOSUnitTypes.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.type.ItemStack.with;

public class FOSBlocks {
    public static Block
    // CRAFTING
    resourceExtractor, cuberiumSynthesizer, sublimator, siliconSynthesizer, brassSmelter, arkyciteRefinery,

    // PRODUCTION
    crudeDrill, improvedDrill, proficientDrill,
    rockCrusher, tinDrill, silverDrill, diamondDrill, vanadiumDrill,
    oreDetectorSmall, oreDetector, oreDetectorReinforced, oreDetectorOverclocked,

    // DISTRIBUTION
    spaceDuct, spaceRouter, spaceBridge, itemCatapult, tinRouter, tinDistributor, tinJunction, tinBridge, tinBelt, tinSorter, flowGate, liquidConveyor,

    // FLUIDS
    fluidPipe, pumpjack,

    // POWER
    tinWire, copperWire, brassWire, tinWirePole, copperWirePole, brassWirePole, windTurbine, burnerGenerator, heatGenerator, plasmaLauncher, solarPanelMedium,
    copperBattery, brassBattery,

    // DEFENSE
    tinWall, tinWallLarge, diamondWall, diamondWallLarge, vanadiumWall, vanadiumWallLarge, cuberiumWall, cuberiumWallLarge,
    helix, sticker, dot, particulator, firefly, pulse, breakdown, rupture, thunder, cluster, judge, newJudge,
    matrixShieldProj,
    landMine,

    // ENVIRONMENT & ORES
    cyanium, cyaniumWall, crimsonStone, crimsonStoneWall, elithite, elithiteWall, elbium, elbiumWall, nethratium, nethratiumWall,
    annite, anniteWall, blublu, blubluWall, purpur, purpurWall,
    tokiciteFloor,
    cyaniumWater, crimsonStoneWater, anniteWater, blubluWater, purpurWater,
    alienMoss,
    oreTin, oreTinSurface, oreTinDeep, oreSilver, oreSilverDeep, oreLithium, oreDiamond, oreVanadium, oreVanadiumDeep, oreIridium, oreLuminium,
    hiveFloor, bugSpawn,

    // PROPS
    softbush,

    // UNITS
    upgradeCenter, destroyerFactory, eliminatorFactory, injectorFactory, simpleReconstructor, droidConstructor, draugFactory,

    // STORAGE & CORES
    coreColony, coreFortress, coreCity, coreMetropolis, lightUnloader,

    // SPECIAL
    nukeLauncher, bigBoy, cliffDetonator, surfaceDetonator, orbitalAccelerator, mechResearchCore, bioResearchCore,

    // NON-PLAYER STUFF
    soontm, citadelSpawner;

    public static void load() {
        //region crafting

        //fine, I will document this block right here.
        resourceExtractor = new ResourceExtractor("resource-extractor"){
            {
                //ResourceExtractor is a multi-crafter, and since MultiCrafter class extends Block, any field from Block class works here as well.
                itemCapacity = 15;
                size = 3;
                hasItems = acceptsItems = true;
                configurable = true;
                envRequired = envEnabled = Env.space;
                drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawPistons(){{
                        angleOffset = 45f;
                        sides = 4;
                        lenOffset = 7;
                        sinScl = 6f;
                    }},
                    new DrawDefault()
                );
                requirements(Category.crafting, with(rawNethratium, 50, rawElithite, 25));
                consumePower(2f);

                //list of recipes used by this multi-crafter, 3 in total here
                resolvedRecipes = Seq.with(
                    new Recipe(
                        //IOEntry input: this recipe's input goes here
                        new IOEntry(
                            //input items go here (can be empty if you wish to use fluids only)
                            Seq.with(ItemStack.with(rawNethratium, 2)),
                            //input fluids go here (there are no fluids required so this is empty)
                            Seq.with(/* example: LiquidStack.with(water, 10) */)
                        ),
                        //IOEntry output: this recipe's output goes here
                        new IOEntry(
                            //output items
                            Seq.with(ItemStack.with(aluminium, 1)),
                            //output fluids, again, it can be empty
                            Seq.with()
                        ),
                        //float craftTime: self-explanatory. measured in ticks
                        60f
                    ),
                    new Recipe(
                        new IOEntry(
                            Seq.with(ItemStack.with(rawElbium, 4)),
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
                            Seq.with(ItemStack.with(rawElithite, 6)),
                            Seq.with()
                        ),
                        new IOEntry(
                            Seq.with(ItemStack.with(silver, 1, titanium, 1)),
                            Seq.with()
                        ),
                        120f
                    )
                );
            }
        };
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
            requirements(Category.crafting, with(tin, 180, silver, 150, diamond, 100));
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawArcSmelt(),
                new DrawDefault()
            );
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
        arkyciteRefinery = new GenericCrafter("arkycite-refinery"){{
            health = 900;
            size = 4;
            rotate = true;
            rotateDraw = false;
            group = BlockGroup.liquids;
            invertFlip = true;
            hasItems = true;
            hasLiquids = true;
            hasPower = true;
            itemCapacity = 5;
            liquidCapacity = 60f;
            consumePower(5f);
            consumeLiquid(arkycite, 20f/60f);
            craftTime = 60f;
            outputItem = new ItemStack(sulphur, 1);
            outputLiquids = LiquidStack.with(oil, 14f/60f, water, 4f/60f);
            liquidOutputDirections = new int[]{0, 2};
            craftEffect = FOSFx.refinerySmoke;
            lightRadius = 32f;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
/*
                new DrawBubbles(arkycite.color.cpy().mul(1.2f)){{
                    spread = 12f;
                }},
*/
                new DrawPistons(){{
                    lenOffset = 5f;
                    sinScl = 3f;
                }},
                new DrawLiquidRegion(arkycite){{
                    suffix = "-input";
                }},
                new DrawGlowRegion("-glow"),
                new DrawDefault(),
                new DrawOutputLiquids() //not to be confused with DrawLiquidOutputs, this one's modded!
            );
            requirements(Category.crafting, with(tin, 150, diamond, 100, silicon, 75, vanadium, 125));
        }};
        //endregion
        //region production
        crudeDrill = new Drill("crude-drill"){{
            size = 2;
            tier = 2;
            drillTime = 360f;
            squareSprite = false;
            drawSpinSprite = false;

            drillMultipliers.put(tin, 2f);
            drillMultipliers.put(silver, 2f);
            drillMultipliers.put(vanadium, 2f);

            consumeLiquid(water, 0.08f).boost();
            requirements(Category.production, with(tin, 10));
            researchCost = with(tin, 25);
            //not usable in Uxerd
            envEnabled ^= Env.space;
        }};
        improvedDrill = new Drill("improved-drill"){{
            size = 3;
            tier = 5;
            drillTime = 200f;
            squareSprite = false;

            drillMultipliers.put(tin, 2f);
            drillMultipliers.put(silver, 2f);
            drillMultipliers.put(vanadium, 2f);

            consumePower(1f);
            consumeLiquid(water, 0.24f).boost();
            requirements(Category.production, with(tin, 45, silver, 30));
            envEnabled ^= Env.space;
        }};
        proficientDrill = new Drill("proficient-drill"){{
            size = 4;
            tier = 7;
            drillTime = 150f;
            squareSprite = false;

            drillMultipliers.put(tin, 2f);
            drillMultipliers.put(silver, 2f);
            drillMultipliers.put(vanadium, 2f);

            consumePower(6f);
            consumeLiquid(tokicite, 0.5f).boost();
            requirements(Category.production, with(tin, 50, brass, 25, silicon, 75, vanadium, 50, nickel, 45));
        }};

        rockCrusher = new HeatProducerDrill("rock-crusher"){{
            health = 300;
            size = 2;
            tier = 2;
            heatOutput = 4f;
            squareSprite = false;
            consumeLiquid(hydrogen, 0.01f).boost();
            requirements(Category.production, with(rawNethratium, 30));
            researchCost = with(rawNethratium, 150);
            envRequired = envEnabled = Env.space;
        }};
        tinDrill = new UndergroundDrill("tin-drill"){{
            size = 2;
            tier = 3;
            drillTime = 360f;
            envEnabled |= Env.space;
            requirements(Category.production, with(tin, 15));
            researchCost = with(tin, 50);
            consumeLiquid(water, 0.08f).boost();
        }};
        silverDrill = new UndergroundDrill("silver-drill"){{
            size = 2;
            tier = 4;
            drillTime = 300f;
            requirements(Category.production, with(tin, 10, silver, 10));
            consumeLiquid(water, 0.1f).boost();
        }};
        diamondDrill = new UndergroundDrill("diamond-drill"){{
            size = 3;
            tier = 5;
            drillTime = 210f;
            squareSprite = false;
            consumePower(2f);
            requirements(Category.production, with(silver, 45, silicon, 75, diamond, 40));
            consumeLiquid(tokicite, 0.2f).boost();
        }};
        vanadiumDrill = new UndergroundDrill("vanadium-drill"){{
            size = 3;
            tier = 6;
            drillTime = 165f;
            consumePower(3f);
            requirements(Category.production, with(tin, 30, silver, 95, silicon, 50, vanadium, 50));
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
            squareSprite = false;
            requirements(Category.production, with(tin, 50));
            consumePower(0.5f);
            researchCost = with(tin, 150);
        }};
        oreDetectorReinforced = new OreDetector("ore-detector-reinforced"){{
            health = 2880;
            armor = 3;
            size = 3;
            speed = 0.4f;
            squareSprite = false;
            requirements(Category.production, with(tin, 125, vanadium, 60));
            consumePower(0.5f);
        }};
        oreDetectorOverclocked = new OreDetector("ore-detector-overclocked"){{
            health = 480;
            size = 3;
            speed = 1.4f;
            effectColor = Color.valueOf("e37f36");
            drillEfficiencyMultiplier = 1.5f;
            squareSprite = false;
            requirements(Category.production, with(tin, 125, silicon, 60, vanadium, 30));
            consumePower(2f);
        }};
        //endregion
        //region defense
        tinWall = new Wall("tin-wall"){{
            scaledHealth = 800;
            size = 1;
            buildCostMultiplier = 3f;
            requirements(Category.defense, with(tin, 6));
        }};
        tinWallLarge = new Wall("tin-wall-large"){{
            scaledHealth = 800;
            size = 2;
            buildCostMultiplier = 3f;
            requirements(Category.defense, with(tin, 24));
        }};
        diamondWall = new Wall("diamond-wall"){{
            scaledHealth = 1250;
            size = 1;
            buildCostMultiplier = 3f;
            requirements(Category.defense, with(diamond, 6));
        }};
        diamondWallLarge = new Wall("diamond-wall-large"){{
            scaledHealth = 1250;
            size = 2;
            buildCostMultiplier = 3f;
            requirements(Category.defense, with(diamond, 24));
        }};
        vanadiumWall = new Wall("vanadium-wall"){{
            scaledHealth = 1600;
            size = 1;
            buildCostMultiplier = 3f;
            requirements(Category.defense, with(vanadium, 6));
        }};
        vanadiumWallLarge = new Wall("vanadium-wall-large"){{
            scaledHealth = 1600;
            size = 2;
            buildCostMultiplier = 3f;
            requirements(Category.defense, with(vanadium, 24));
        }};
        cuberiumWall = new Wall("cuberium-wall"){{
            scaledHealth = 1200;
            size = 1;
            absorbLasers = true;
            buildCostMultiplier = 3f;
            requirements(Category.defense, with(cuberium, 6));
        }};
        cuberiumWallLarge = new Wall("cuberium-wall-large"){{
            scaledHealth = 1200;
            size = 2;
            absorbLasers = true;
            buildCostMultiplier = 3f;
            requirements(Category.defense, with(cuberium, 24));
        }};

        helix = new ItemTurret("helix"){{
            scaledHealth = 480;
            size = 2;
            range = 132;
            targetAir = targetGround = true;
            recoil = 1f;
            reload = 30f;
            inaccuracy = 0f;
            shootY = 4f;
            shootSound = Sounds.pew;
            squareSprite = false;
            shoot = new ShootHelix(){{
                shots = 2;
                mag = 2.5f;
            }};
            drawer = new DrawTurret("lumoni-"){{
                parts.addAll(
                    new RegionPart(){{
                        mirror = false;
                    }},
                    new RegionPart("-side"){{
                        mirror = true;
                        moveX = 1f;
                    }}
                );
            }};
            ammo(
                tin, new BasicBulletType(3f, 20){{
                    width = 3f; height = 6f;
                    lifetime = 44f;
                    trailColor = frontColor = FOSPal.tin;
                    backColor = FOSPal.tinBack;
                    trailWidth = 1.5f;
                    trailLength = 8;
                    ammoMultiplier = 2f;
                    buildingDamageMultiplier = 0.3f;
                }},
                diamond, new BasicBulletType(3f, 36){{
                    width = 3f; height = 6f;
                    lifetime = 44f;
                    trailColor = frontColor = FOSPal.diamond;
                    backColor = FOSPal.diamondBack;
                    trailWidth = 1.5f;
                    trailLength = 8;
                    ammoMultiplier = 3f;
                    pierce = true;
                    pierceCap = 2;
                    buildingDamageMultiplier = 0.3f;
                }},
                silicon, new BasicBulletType(3f, 28){{
                    width = 3f; height = 6f;
                    lifetime = 44f;
                    trailColor = frontColor = Pal.unitFront;
                    backColor = Pal.unitBack;
                    trailWidth = 1.5f;
                    trailLength = 8;
                    ammoMultiplier = 3f;
                    homingRange = 56f;
                    homingPower = 0.1f;
                    buildingDamageMultiplier = 0.3f;
                }},
                vanadium, new BasicBulletType(4f, 40){{
                    width = 3f; height = 6f;
                    lifetime = 44f;
                    rangeChange = 44f;
                    trailColor = frontColor = Pal.gray;
                    backColor = Pal.darkerGray;
                    trailWidth = 1.5f;
                    trailLength = 8;
                    ammoMultiplier = 3f;
                    buildingDamageMultiplier = 0.3f;
                }},
                nickel, new BasicBulletType(4f, 44){{
                    width = 4f; height = 8f;
                    lifetime = 44f;
                    rangeChange = 44f;
                    trailColor = frontColor = Color.valueOf("a3bda7");
                    backColor = Color.valueOf("4e5b4c");
                    trailWidth = 1.5f;
                    trailLength = 8;
                    ammoMultiplier = 4f;
                    buildingDamageMultiplier = 0.3f;
                }},
                //TODO: trail shader?
                luminium, new BasicBulletType(){{
                    width = 4f; height = 8f;
                    speed = 4.5f;
                    damage = 52.5f;
                    lifetime = 44f;
                    rangeChange = 66f;
                    trailColor = frontColor = FOSPal.luminium1;
                    backColor = FOSPal.luminium2;
                    trailWidth = 1.5f;
                    trailLength = 12;
                    ammoMultiplier = 3f;
                    lightColor = FOSPal.luminium1.cpy().a(0.6f);
                    pierce = true;
                    pierceCap = 2;
                    hitEffect = Fx.hitEmpSpark;
                    status = StatusEffects.blasted;
                    buildingDamageMultiplier = 0.3f;
                }}
            );
            consumeCoolant(0.25f).boost();
            requirements(Category.turret, with(tin, 60, silver, 50));
        }};
        sticker = new ItemTurret("sticker"){{
            scaledHealth = 480;
            size = 2;
            range = 360;
            targetAir = false;
            targetGround = true;
            recoil = 0f;
            reload = 45f;
            inaccuracy = 4f;
            outlineColor = Color.valueOf("302326");
            shootSound = Sounds.mud;
            squareSprite = false;
            consumeLiquid(tokicite, 0.1f);
            ammo(
                tin, new StickyBulletType(8f, 20, 90){{
                    lifetime = 45f;
                    width = height = 10f;
                    trailColor = FOSPal.tinBack;
                    backColor = FOSPal.tin;
                    frontColor = FOSPal.tokicite;
                    ammoMultiplier = 2f;
                    scaleLife = true;
                    splashDamage = 80;
                    splashDamageRadius = 12f;
                    buildingDamageMultiplier = 0.3f;
                }},
                diamond, new StickyBulletType(8f, 60, 90){{
                    lifetime = 45f;
                    width = height = 10f;
                    trailColor = FOSPal.diamondBack;
                    backColor = FOSPal.diamond;
                    frontColor = FOSPal.tokicite;
                    ammoMultiplier = 3f;
                    scaleLife = true;
                    splashDamage = 100;
                    splashDamageRadius = 16f;
                    buildingDamageMultiplier = 0.3f;
                }}
            );
            drawer = new DrawTurret("lumoni-"){{
                parts.add(
                    new RegionPart(),
                    new RegionPart("-barrel"){{
                        moveY -= 3f;
                        progress = PartProgress.recoil;
                    }}
                );
            }};
            requirements(Category.turret, with(tin, 75, silver, 50, silicon, 50));
        }};
        dot = new PowerTurret("dot"){{
            scaledHealth = 480;
            size = 2;
            range = 225f;
            rotateSpeed = 7.5f;
            shootCone = 2f;
            reload = 20f;
            targetAir = targetGround = true;
            squareSprite = false;

            shootSound = Sounds.tractorbeam;
            shootEffect = FOSFx.dotLaserEnd;

            consumePower(2f);
            drawer = new DrawTurret("lumoni-");
            shootType = new RailBulletType(){{
                damage = 16f;
                length = 225f;
                lifetime = 10f;
                pierceDamageFactor = 1f;
                pierceArmor = true;

                lineEffect = FOSFx.dotLaserLine;
                endEffect = FOSFx.dotLaserEnd;
                shootSound = Sounds.bolt;

/*
                lightning = 1;
                lightningType = new LightningBulletType(){{
                    lightningLength = 6;
                    lightningLengthRand = 4;
                    damage = 3f;
                    lightningColor = Color.scarlet.cpy().mul(1.2f);
                }};
*/

                buildingDamageMultiplier = 0.3f;
            }};
            requirements(Category.turret, with(silver, 50, diamond, 75, vanadium, 50));
        }};
        particulator = new ItemTurret("particulator"){{
            health = 2400;
            size = 3;
            range = 240;
            targetAir = targetGround = true;
            recoil = 2;
            reload = 40;
            inaccuracy = 5;
            outlineColor = Color.valueOf("302326");
            shootSound = Sounds.shootBig;
            squareSprite = false;
            ammo(
                silver, new BasicBulletType(4f, 80){{
                    lifetime = 60f;
                    width = 16f; height = 24f;
                    backColor = FOSPal.silverBack;
                    frontColor = trailColor = lightColor = FOSPal.silver;
                    trailEffect = Fx.artilleryTrail;
                    trailWidth = 4;
                    trailLength = 20;
                    ammoMultiplier = 1;
                    splashDamage = 10f;
                    splashDamageRadius = 24f;
                    knockback = 3.2f;
                    fragOnHit = true;
                    hitEffect = despawnEffect = Fx.explosion;
                    buildingDamageMultiplier = 0.3f;
                    fragBullets = 6;
                    fragBullet = new BasicBulletType(0.8f, 10){{
                        lifetime = 60f * 5; //frags will stay for pretty long
                        drag = 0.024f;
                        width = height = 6f;
                        backColor = FOSPal.silverBack;
                        frontColor = trailColor = FOSPal.silver;
                        trailEffect = Fx.artilleryTrail;
                        trailLength = 8;
                        pierceArmor = true;
                        collidesAir = false;
                        collidesTiles = true;
                        collideTerrain = false;
                        hitEffect = Fx.hitBulletSmall;
                        despawnEffect = Fx.none;
                        buildingDamageMultiplier = 0.3f;
                    }};
                }},
                vanadium, new BasicBulletType(6f, 120){{
                    lifetime = 40f;
                    width = 16f; height = 24f;
                    backColor = vanadium.color.cpy().mul(0.8f);
                    frontColor = trailColor = lightColor = vanadium.color;
                    trailEffect = Fx.artilleryTrail;
                    trailWidth = 4;
                    trailLength = 20;
                    ammoMultiplier = 1;
                    splashDamage = 25f;
                    splashDamageRadius = 24f;
                    knockback = 4f;
                    fragOnHit = true;
                    hitEffect = despawnEffect = Fx.explosion;
                    buildingDamageMultiplier = 0.3f;
                    fragBullets = 7;
                    fragBullet = new BasicBulletType(0.8f, 16){{
                        lifetime = 60f * 5; //frags will stay for pretty long
                        drag = 0.024f;
                        width = height = 6f;
                        backColor = vanadium.color.cpy().mul(0.8f);
                        frontColor = trailColor = vanadium.color;
                        trailEffect = Fx.artilleryTrail;
                        trailLength = 8;
                        pierceArmor = true;
                        collidesAir = false;
                        collidesTiles = true;
                        collideTerrain = false;
                        hitEffect = Fx.hitBulletSmall;
                        despawnEffect = Fx.none;
                        buildingDamageMultiplier = 0.3f;
                    }};
                }}
            );
            drawer = new DrawTurret("lumoni-");
            consumeCoolant(0.5f).boost();
            coolantMultiplier = 2f;
            requirements(Category.turret, with(tin, 200, silver, 125, silicon, 175, vanadium, 150));
        }};
        firefly = new LiquidTurret("firefly"){{
            health = 2400;
            size = 3;
            range = 200;
            reload = 1f;
            inaccuracy = 5f;
            shootCone = 30f;
            targetAir = false;
            targetGround = true;
            extinguish = false;
            minWarmup = 0.99f;
            shootSound = Sounds.flame2;

            var fire = new MultiEffect(Fx.fire, Fx.fireSmoke);
            var fireLong = new MultiEffect(FOSFx.fireLong, FOSFx.fireSmokeLong);

            ammo(
                arkycite, new LiquidBulletType(arkycite){{
                    lifetime = 50f;
                    speed = 4f;
                    //incendAmount = 5;
                    damage = 15f;
                    pierce = true;
                    pierceCap = 2;
                    despawnHit = true;
                    hitEffect = fireLong;
                    //scaleLife = true;

                    puddleSize = 5f;
                    puddleAmount = 10f;
                    puddles = 3;
                    orbSize = 4f;

                    trailEffect = fire;
                    //trailColor = arkycite.color;
                    trailParam = 6f;
                    trailChance = 0.25f;

                    status = StatusEffects.burning;
                    statusDuration = 180f;
                }},
                oil, new LiquidBulletType(oil){{
                    lifetime = 50f;
                    speed = 4f;
                    incendAmount = 10;
                    incendChance = 1f;
                    damage = 20f;
                    pierce = true;
                    pierceCap = 2;
                    despawnHit = true;
                    hitEffect = fireLong;
                    //scaleLife = true;

                    puddleSize = 5f;
                    puddleAmount = 10f;
                    puddles = 3;
                    orbSize = 4f;

                    trailEffect = fire;
                    //trailColor = oil.color;
                    trailParam = 6f;
                    trailChance = 0.25f;

                    status = StatusEffects.melting;
                    statusDuration = 180f;
                }}
            );
            drawer = new DrawTurret("lumoni-"){{
                parts.add(
                    new RegionPart("-blades"){{
                        progress = PartProgress.warmup;
                        minWarmup = 0.5f;
                        moveY = 2f;
                    }},
                    new RegionPart("-side"){{
                        mirror = true;
                        progress = PartProgress.warmup;
                        minWarmup = 0.5f;
                        moveX = moveY = 1.25f;
                    }}
                );
            }};
            requirements(Category.turret, with(tin, 100, copper, 75, vanadium, 100));
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
            requirements(Category.turret, with(silver, 300, diamond, 250, silicon, 250, vanadium, 175));
        }};
        breakdown = new ItemTurret("breakdown"){{
            health = 2400;
            size = 3;
            range = 320f;
            targetAir = targetGround = true;
            recoil = 3f;
            shake = 2f;
            reload = 90f;
            ammo(
                nickel, new ShieldPierceBulletType(0.1f){{
                    damage = 400f;
                    shootEffect = Fx.shootBig;
                    hitEffect = Fx.hitBulletColor;
                    smokeEffect = Fx.smokeCloud;
                    trailEffect = Fx.artilleryTrailSmoke;
                    despawnEffect = Fx.shootSmallSmoke;
                    trailSpacing = 20f;
                    speed = 320f;
                    hitShake = 4f;
                    ammoMultiplier = 1f;
                    buildingDamageMultiplier = 0.3f;
                }}
            );
            drawer = new DrawTurret("lumoni-");
            requirements(Category.turret, with(silver, 150, silicon, 100, diamond, 75, nickel, 100));
        }};
        rupture = new ItemTurret("rupture"){{
            requirements(Category.turret, with(tin, 1));
            health = 8000;
            size = 4;
            minRange = 300f;
            range = 380f;
            reload = 150;
            recoil = 0f;
            cooldownTime = reload;
            minWarmup = 0.999f;
            rotateSpeed = 1f;
            targetAir = false;
            targetGround = true;
            consumePower(150f);
            shake = 5f;
            squareSprite = false;
            shoot = new ShootMulti(
                    new ShootAlternate(){{
                        shots = 3;
                        shotDelay = 10f;
                        barrels = 1;
                    }},
                    new ShootHelix(){{
                        scl = 1f;
                        mag = 4f;
                    }}
            );
            shootSound = Sounds.shootSmite;
            ammoPerShot = 2;
            ammo(
                    luminium, new RailBulletType(){{
                        length = 400f;
                        damage = 50f;
                        hitColor = Color.valueOf("ff6214");
                        lineEffect = Fx.chainLightning;
                        hitEffect = endEffect = Fx.railHit;
                        shootEffect = Fx.shootTitan;
                        pierceEffect = Fx.railHit;
                        pointEffect = Fx.railTrail;
                        pointEffectSpace = 30f;
                    }}
            );
            drawer = new DrawTurret("lumoni-"){{
                parts.add(new RegionPart("-wing"){{
                    mirror = true;
                    under = true;
                    moveX = -3f;
                    moveY = 2f;
                    moveRot = -25f;
                    progress = PartProgress.warmup;
                    heatProgress = PartProgress.warmup.add(1f).min(PartProgress.warmup);
                    heatColor = Color.red;
                }},
                new RegionPart("-barrel"){{
                    mirror = false;
                    under = true;
                    moveX = 0f;
                    moveY = -5f;
                    progress = PartProgress.recoil;
                    heatProgress = PartProgress.recoil.add(4f).min(PartProgress.recoil);
                    heatColor = Color.red;
                }},
                new RegionPart("-blade"){{
                    mirror = true;
                    under = true;
                    moveX = 5f;
                    moveY = 8f;
                    moveRot = -10f;
                    progress = PartProgress.warmup;
                    heatProgress = PartProgress.warmup.add(1f).min(PartProgress.warmup);
                    heatColor = Color.red;
                }});
            }};
        }};
        thunder = new PowerTurret("thunder"){
            {
                health = 9000;
                size = 5;
                shake = 4f;
                recoil = 7.5f;
                range = 240f;
                reload = 60f;
                targetAir = false;
                targetGround = true;
                minWarmup = 0.99f;
                shootWarmupSpeed = 0.05f;
                shootCone = 20f;
                shootType = tLaser(0.1f, 5, "3030ff",
                    tLaser(0.15f, 4, "25d5ff",
                        tLaser(0.25f, 3, "ffff30",
                            tLaser(0.2f, 2, "dc5b2e",
                                tLaser(0.3f, 1, "ff3030", null)
                            )
                        )
                    )
                );
                shootEffect = Fx.lightningShoot;
                shootSound = Sounds.laser;
                chargeSound = Sounds.lasercharge;
                consumePower(10f);
                consumeCoolant(2f).boost();
                coolantMultiplier = 0.5f;
                squareSprite = false;
                drawer = new DrawTurret("lumoni-"){{
                    parts.addAll(
                        new RegionPart("-back"){{
                            mirror = true;
                            moveY = 5f;
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.recoil.add(2f).min(PartProgress.recoil);
                            heatColor = Color.red;
                        }},
                        new RegionPart("-mid"){{
                            mirror = false;
                            moveY = -6f;
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.warmup.add(1f).min(PartProgress.warmup);
                            heatColor = Color.red;
                        }},
                        new RegionPart("-back-glow"){{
                            mirror = true;
                            moveY = 5f;
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.warmup.add(1f).min(PartProgress.warmup);
                            heatColor = Color.red;
                        }},
                        new ShapePart(){{
                            circle = true;
                            hollow = true;
                            color = Color.red;
                            radius = 0f;
                            radiusTo = 3f;
                            stroke = 0f;
                            strokeTo = 3f;
                            x = 0f; y = 18f;
                            layer = Layer.effect;
                        }}
                    );
                }};
                requirements(Category.turret, with(tin, 300, silver, 300, diamond, 400, silicon, 250, vanadium, 250));
            }

            //hard-coding time
            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.ammo);

                StatValue stat = table -> {
                    table.row();

                    table.table(Styles.grayPanel, bt -> {
                        bt.left().top().defaults().padRight(3).left();

                        bt.add(Core.bundle.format("bullet.damage", shootType.damage + "~" + (shootType.damage * 10)));
                        bt.row();
                        int val = (int)(shootType.buildingDamageMultiplier * 100 - 100);
                        bt.add(Core.bundle.format("bullet.buildingdamage", (val > 0 ? "[stat]+" : "[negstat]") + Strings.autoFixed(val, 1)));
                        bt.row();
                        bt.add("@bullet.infinitepierce");
                    }).padLeft(0).padTop(5).padBottom(5).growX().margin(10);

                    table.row();
                };

                stats.add(Stat.ammo, stat);
            }

            BulletType tLaser(float dmgMultiplier, int lenMultiplier, String color, BulletType frag) {
                return new LaserBulletType(750f * dmgMultiplier){{
                    speed = 0f; //just in case
                    lifetime = 1f;
                    width = 48f * (1f / lenMultiplier);
                    length = 48f * lenMultiplier;
                    colors = new Color[]{
                        Color.valueOf(color).mul(1, 1, 1, 0.4f),
                        Color.valueOf(color),
                        Color.white
                    };
                    buildingDamageMultiplier = 0.3f;
                    displayAmmoMultiplier = false;
                    collidesAir = false;

                    lightningColor = colors[1];
                    lightningDamage = this.damage * 0.2f;
                    lightningLength = 9;
                    lightningSpacing = 19f + (38f * (lenMultiplier - 1));
                    lightningDelay = 3f;
                    lightningAngle = 20f;
                    lightningAngleRand = 0f;

                    intervalAngle = 0f;
                    intervalSpread = intervalRandomSpread = 0f;
                    bulletInterval = 1f;
                    intervalBullets = 1;
                    intervalBullet = frag;

                    fragOnHit = false;
                    fragAngle = 0f;
                    fragSpread = fragRandomSpread = 0f;
                    fragBullets = 1;
                    fragBullet = this.copy();
                    fragBullet.lifetime = 30;
                    fragBullet.fragBullet = fragBullet.intervalBullet = null;
                }};
            }
        };
        cluster = new ItemTurret("cluster"){{
            scaledHealth = 480;
            size = 4;
            range = 280f;
            reload = 6f;
            inaccuracy = 20f;
            targetAir = targetGround = true;
            recoil = 3f;
            shootSound = Sounds.missile;
            shoot = new ShootAlternate(){{
                barrels = 8;
                spread = 2f;
            }};
            ammo(
                diamond, new MissileBulletType(5f, 5){{
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
                    buildingDamageMultiplier = 0.3f;
                    fragBullets = 3;
                    fragBullet = new BasicBulletType(1f, 2.5f){{
                        lifetime = 20f;
                        width = height = 8f;
                        frontColor = FOSPal.diamond;
                        backColor = FOSPal.diamondBack;
                        hitEffect = Fx.explosion;
                        splashDamage = 80f;
                        splashDamageRadius = 38f;
                        buildingDamageMultiplier = 0.3f;
                    }};
                }}
            );
            consumeCoolant(5f).boost();
            coolantMultiplier = 0.25f;
            requirements(Category.turret, with(silicon, 500, vanadium, 300, nickel, 250, luminium, 200));
        }};
        judge = new DeathrayTurret("judge1"){{
            scaledHealth = 480;
            size = 8;
            minRange = 200f;
            range = 600f;
            reload = 900f;
            minWarmup = 0.99f;
            hasLiquids = true;
            targetAir = targetGround = true;
            loopSound = Sounds.techloop;
            loopSoundVolume = 4f;
            consumePower(150f);
            consumeCoolant(5f);
            liquidCapacity = 450f;
            shootDuration = 600f;
            coolantMultiplier = 0.1f;
            shake = 10f;
            outlineIcon = false;
            squareSprite = false;
            shootType = new OhioBeamBulletType(3600f, 18f){{
                buildingDamageMultiplier = 0.3f;
            }};
            lightRadius = 96f;
            drawer = new DrawTurret("e-"){{
                parts.addAll(
                    new RegionPart("-glow"){{
                        color = Color.valueOf("306082");
                        colorTo = Pal.turretHeat;
                        progress = PartProgress.warmup;
                    }},
                    new ShapePart(){{
                        x = -8; y = -8;
                        rotation = 45f;
                        color = Pal.accent;
                        colorTo = Color.clear;
                        progress = PartProgress.reload;
                    }},
                    new ShapePart(){{
                        x = -8; y = 8;
                        rotation = 315f;
                        color = Pal.accent;
                        colorTo = Color.clear;
                        progress = PartProgress.reload;
                    }},
                    new ShapePart(){{
                        x = 8; y = 8;
                        rotation = 225f;
                        color = Pal.accent;
                        colorTo = Color.clear;
                        progress = PartProgress.reload;
                    }},
                    new ShapePart(){{
                        x = 8; y = -8;
                        rotation = 135f;
                        color = Pal.accent;
                        colorTo = Color.clear;
                        progress = PartProgress.reload;
                    }}
                );
            }};
            requirements(Category.turret, with(tin, 3000, silver, 3000, diamond, 2500, silicon, 3000, vanadium, 1500, nickel, 1500, luminium, 1500));
        }};
        newJudge = new NewDeathRayTurret("judge2"){{
            scaledHealth = 480;
            size = 8;
            minRange = 200f;
            range = 600f;
            hasLiquids = true;
            targetAir = targetGround = true;
            loopSound = Sounds.laserbig;
            loopSoundVolume = 4f;
            consumePower(200f);
            consumeLiquid(cryofluid, 5f);
            liquidCapacity = 450f;
            shake = 10f;
            outlineIcon = false;
            squareSprite = false;
            lightRadius = 96f;
            shootDuration = 600f;
            chargeTime = 180f;
            shootType = new DeathRayBulletType(){{
                damage = 1000f / 60f;
                raySize = 20f;
                buildingDamageMultiplier = 0.3f;
            }};
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawPower("-glow"){{
                        emptyLightColor = Color.valueOf("30608200");
                        fullLightColor = Color.valueOf("306082");
                    }},
                    new DrawLiquidRegion(),
                    new DrawDefault()
            );
            requirements(Category.turret, with(tin, 3000, silver, 3000, diamond, 2500, silicon, 3000, vanadium, 1500, nickel, 1500, luminium, 1500));
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

        landMine = new CamoMine("land-mine"){{
            tileDamage = health; //this is a one-time use mine.
            teamAlpha = 0.1f;
            tendrils = 0;
            shots = 1;
            bullet = new ExplosionBulletType(240f, 20f){{
                shootEffect = Fx.blastExplosion;
            }};
            requirements(Category.effect, with(copper, 30, vanadium, 40));
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
        tinBelt = new PipeConveyor("tin-belt"){{
            health = 10;
            speed = 0.05f;
            displayedSpeed = 6.9f;
            researchCost = with(tin, 30);
            requirements(Category.distribution, with(tin, 1));
        }};
        tinJunction = new Junction("tin-junction"){{
            speed = 16f;
            researchCost = with(tin, 60);
            ((Conveyor)tinBelt).junctionReplacement = this;
            requirements(Category.distribution, with(tin, 2));
        }};
        tinRouter = new Router("tin-router"){{
            researchCost = with(tin, 90);
            requirements(Category.distribution, with(tin, 3));
        }};
        tinDistributor = new Router("tin-distributor"){{
            size = 2;
            researchCost = with(tin, 120);
            requirements(Category.distribution, with(tin, 4));
        }};
        tinSorter = new Sorter("tin-sorter"){{
            researchCost = with(tin, 60, silver, 60);
            requirements(Category.distribution, with(tin, 2, silver, 2));
        }};
        flowGate = new FlowGate("flow-gate"){{
            researchCost = with(tin, 60, silver, 60);
            requirements(Category.distribution, with(tin, 2, silver, 2));
        }};
        tinBridge = new BufferedItemBridge("tin-bridge"){{
            fadeIn = moveArrows = false;
            range = 4;
            speed = 44f;
            arrowSpacing = 6f;
            bufferCapacity = 14;
            researchCost = with(tin, 150);
            ((Conveyor)tinBelt).bridgeReplacement = this;
            requirements(Category.distribution, with(tin, 10));
        }};
        //endregion
        //region liquids
        fluidPipe = new Conduit("fluid-pipe"){{
            health = 5;
            size = 1;
            liquidCapacity = 20f;
            requirements(Category.liquid, with(tin, 1, silver, 1));
        }};
        pumpjack = new Pump("pumpjack"){{
            scaledHealth = 60;
            size = 3;
            pumpAmount = 0.25f;
            liquidCapacity = 45f;
            requirements(Category.liquid, with(tin, 150, silicon, 50, copper, 100, vanadium, 75));
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
            requirements(Category.power, with(copper, 2));
        }};
        brassWire = new PowerWire("brass-wire"){{
            health = 8;
            //does not consume any power
            requirements(Category.power, with(brass, 1));
        }};
        tinWirePole = new PowerWireNode("tin-wire-pole"){{
            health = 30;
            consumesPower = true;
            consumePower(4f / 60f);
            range = 4;
            fogRadius = 1;
            squareSprite = false;
            requirements(Category.power, with(tin, 5));

            ((PowerWire)tinWire).bridgeReplacement = this;
        }};
        copperWirePole = new PowerWireNode("copper-wire-pole"){{
            health = 50;
            consumesPower = true;
            consumePower(1f / 60f);
            range = 7;
            fogRadius = 1;
            squareSprite = false;
            researchCostMultiplier = 0.5f;
            requirements(Category.power, with(copper, 10));

            ((PowerWire)copperWire).bridgeReplacement = this;
        }};
        brassWirePole = new PowerWireNode("brass-wire-pole"){{
            health = 80;
            //no power consumption
            range = 10;
            fogRadius = 1;
            squareSprite = false;
            researchCostMultiplier = 0.25f;
            requirements(Category.power, with(brass, 20));

            ((PowerWire)brassWire).bridgeReplacement = this;
        }};
        windTurbine = new WindTurbine("wind-turbine"){{
            health = 480;
            size = 2;
            powerProduction = 0.8f;
            requirements(Category.power, with(tin, 40));
            researchCost = with(tin, 160);
            rotateSpeed = 1.8f;
            squareSprite = false;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawRegion("-rotator", 15f, true)
            );
        }};
        burnerGenerator = new ConsumeGenerator("burner-generator"){{
            health = 900;
            size = 3;
            liquidCapacity = 60f;
            powerProduction = 15f;
            generateEffect = FOSFx.generatorSmoke;
            effectChance = 0.05f;
            consume(new ConsumeLiquidFlammable(0.4f, 0.5f){{
                filter = l -> l.flammability >= minFlammability && !l.gas;
            }});
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(arkycite, 8f),
                new DrawLiquidTile(oil, 8f),
                new DrawDefault()
            );
            requirements(Category.power, with(tin, 75, brass, 150, vanadium, 100));
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
/*
        plasmaLauncher = new PlasmaLauncher("plasma-launcher"){{
            health = 1500;
            size = 3;
            envRequired = envEnabled = Env.space;
            requirements(Category.power, with(aluminium, 125, lithium, 90, titanium, 75, cuberium, 50));
            drawer = new DrawMulti(
                new DrawRegion("-base"),
                new DrawDefault()
            );
        }};
*/
        solarPanelMedium = new SolarGenerator("solar-panel-medium"){{
            size = 2;
            powerProduction = 0.5f;
            requirements(Category.power, with(lithium, 50, silver, 75, rawElithite, 50));
        }};

        copperBattery = new Battery("copper-battery"){{
            scaledHealth = 120;
            size = 1;
            consumePowerBuffered(3000);
            baseExplosiveness = 1f;
            drawer = new DrawMulti(
                new DrawPower(),
                new DrawDefault()
            );
            requirements(Category.power, with(tin, 5, copper, 15));
        }};
        brassBattery = new Battery("brass-battery"){{
            scaledHealth = 120;
            size = 2;
            consumePowerBuffered(20000);
            baseExplosiveness = 3f;
            drawer = new DrawMulti(
                new DrawPower(),
                new DrawDefault()
            );
            requirements(Category.power, with(tin, 25, brass, 60));
        }};
        //endregion
        //region environment & ores
        tokiciteFloor = new Floor("tokicite-floor"){{
            drownTime = 360f;
            status = FOSStatuses.tokiciteSlowed;
            speedMultiplier = 0.15f;
            variants = 0;
            liquidDrop = tokicite;
            isLiquid = true;
            cacheLayer = CacheLayer.tar;
            albedo = 1f;
        }};
        cyaniumWater = new Floor("cyanium-water"){{
            isLiquid = true;
            status = StatusEffects.wet;
            liquidDrop = water;
            cacheLayer = CacheLayer.water;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            shallow = true;
            supportsOverlay = true;
            variants = 4;
        }};
        crimsonStoneWater = new Floor("crimson-stone-water"){{
            isLiquid = true;
            status = StatusEffects.wet;
            liquidDrop = water;
            cacheLayer = CacheLayer.water;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            shallow = true;
            supportsOverlay = true;
        }};
        anniteWater = new Floor("annite-water"){{
            variants = 4;
            isLiquid = true;
            status = StatusEffects.wet;
            liquidDrop = water;
            cacheLayer = CacheLayer.water;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            shallow = true;
            supportsOverlay = true;
        }};
        blubluWater = new Floor("blublu-water"){{
            variants = 4;
            isLiquid = true;
            status = StatusEffects.wet;
            liquidDrop = water;
            cacheLayer = CacheLayer.water;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            shallow = true;
            supportsOverlay = true;
        }};
        purpurWater = new Floor("purpur-water"){{
            variants = 4;
            isLiquid = true;
            status = StatusEffects.wet;
            liquidDrop = water;
            cacheLayer = CacheLayer.water;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            shallow = true;
            supportsOverlay = true;
        }};
        cyanium = new Floor("cyanium"){{
            variants = 4;
        }};
        cyaniumWall = new StaticWall("cyanium-wall"){{
            variants = 4;
            cyaniumWater.asFloor().wall = this;
        }};
/*      TODO: this sucks
        cyaniumAlt = new Floor("cyanium-alt"){{
            variants = 4;
            wall = cyaniumWall;
        }};
*/
        crimsonStone = new Floor("crimson-stone");
        crimsonStoneWall = new StaticWall("crimson-stone-wall"){{
            variants = 1;
            crimsonStoneWater.asFloor().wall = this;
        }};
        elithite = new Floor("elithite"){{
            itemDrop = rawElithite;
            variants = 4;
        }};
        elithiteWall = new StaticWall("elithite-wall");
        elbium = new Floor("elbium"){{
            itemDrop = rawElbium;
            variants = 4;
        }};
        elbiumWall = new StaticWall("elbium-wall");
        nethratium = new Floor("nethratium"){{
            itemDrop = rawNethratium;
            variants = 4;
        }};
        nethratiumWall = new StaticWall("nethratium-wall");
        annite = new Floor("annite"){{
            variants = 4;
        }};
        anniteWall = new StaticWall("annite-wall"){{
            anniteWater.asFloor().wall = this;
        }};
        blublu = new Floor("blublu"){{
            variants = 4;
        }};
        blubluWall = new StaticWall("blublu-wall"){{
            blubluWater.asFloor().wall = this;
        }};
/*      TODO: this sucks
        blubluAlt = new Floor("blublu-alt"){{
            variants = 4;
            wall = blubluWall;
        }};
*/
        purpur = new Floor("purpur"){{
            variants = 4;
        }};
        purpurWall = new StaticWall("purpur-wall"){{
            purpurWater.asFloor().wall = this;
        }};
        alienMoss = new OverlayFloor("alien-moss");
        oreTin = new UndergroundOreBlock("ore-tin"){{
            drop = tin;
        }};
        oreTinSurface = new OreBlock("ore-tin-surface"){{
            itemDrop = tin;
        }};
        oreTinDeep = new OreBlock("ore-tin-deep"){{
            itemDrop = tin;
        }};
        oreSilver = new UndergroundOreBlock("ore-silver"){{
            drop = silver;
        }};
        oreSilverDeep = new OreBlock("ore-silver-deep"){{
            itemDrop = silver;
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
        oreVanadiumDeep = new OreBlock("ore-vanadium-deep"){{
            itemDrop = vanadium;
        }};
        oreIridium = new UndergroundOreBlock("ore-iridium"){{
            drop = nickel;
        }};
        oreLuminium = new AnimatedOreBlock("ore-luminium", FOSShaders.los){{
            itemDrop = luminium;
            variants = 3;
            emitLight = true;
            lightColor = FOSPal.luminium1.cpy().a(0.3f);
            lightRadius = 18f;
        }};
        hiveFloor = new Floor("hive"){{
            variants = 5;
            walkSound = Sounds.plantBreak;
            placeableOn = false;
        }};
        bugSpawn = new BugSpawn("bug-spawn"){{
            scaledHealth = 400;
            size = 3;
            interval = 30 * 60;
            drawTeamOverlay = false;
            customShadow = true;
            createRubble = false;
            unitCapModifier = 15;
        }};

        softbush = new Prop("softbush"){{
            variants = 3;
            blublu.asFloor().decoration = this;
            breakSound = Sounds.plantBreak;
        }};
        //endregion
        //region units
        upgradeCenter = new UpgradeCenter("upgrade-center"){{
            health = 1500;
            size = 3;
            solid = true;
            squareSprite = false;
            consumePower(3f);
            researchCost = with(tin, 125, silver, 100);
            requirements(Category.units, with(tin, 250, silver, 200));
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawFlame()
                // -top region is drawn inside draw() method
            );
        }};
        destroyerFactory = new UnitFactory("destroyer-factory"){{
            scaledHealth = 120;
            size = 3;
            configurable = false;
            consumePower(5f);
            requirements(Category.units, with(tin, 100, silver, 75, silicon, 150));
            plans.add(
                new UnitPlan(FOSUnitTypes.assault, 20f * 60, with(silicon, 15, silver, 15))
            );
        }};
        eliminatorFactory = new UnitFactory("eliminator-factory"){{
            scaledHealth = 120;
            size = 3;
            configurable = false;
            consumePower(5f);
            requirements(Category.units, with(tin, 100, silver, 75, silicon, 150));
            plans.add(
                new UnitPlan(FOSUnitTypes.radix, 20f * 60, with(silicon, 15, diamond, 10))
            );
        }};
        injectorFactory = new UnitFactory("injector-factory"){{
            scaledHealth = 120;
            size = 3;
            configurable = false;
            consumePower(5f);
            requirements(Category.units, with(tin, 100, silver, 75, silicon, 150));
            plans.add(
                new UnitPlan(FOSUnitTypes.sergeant, 20f * 60, with(silicon, 15, tin, 20))
            );
        }};
        simpleReconstructor = new Reconstructor("simple-reconstructor"){{
            scaledHealth = 160;
            size = 4;
            consumePower(8f);
            consumeItems(with(silicon, 50, diamond, 35));
            upgrades.addAll(
                new UnitType[]{assault, abrupt},
                new UnitType[]{radix, foetus},
                new UnitType[]{sergeant, lieutenant}
            );
            constructTime = 60f * 25;
            requirements(Category.units, with(silver, 150, silicon, 150, vanadium, 100));
        }};
        droidConstructor = new OverdriveDroneCenter("droid-constructor"){{
            scaledHealth = 120;
            size = 3;
            unitsSpawned = 2;
            droneConstructTime = 1 * Time.toMinutes;
            droneRange = 80f;
            droneType = testOverdrive;
            requirements(Category.units, BuildVisibility.debugOnly, with(brass, 275, silicon, 400, nickel, 150));
        }};
        draugFactory = new MinerUnitFactory("draug-factory"){{
            scaledHealth = 60;
            size = 4;
            consumePower(2);
            unitRequirements = with();
            maxSpawn = 1;
            unitType = draug;
            produceTime = 1200f;
            requirements(Category.units, with(tin, 150, silver, 250, diamond, 50));
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
            itemCapacity = 0;
            unitCapModifier = 0;
            squareSprite = false;
            requirements(Category.effect, with(tin, 1500));
        }};
        coreFortress = new DetectorCoreBlock("core-fortress"){{
            health = 2800;
            size = 3;
            unitCapModifier = 7;
            itemCapacity = 2500;
            unitType = FOSUnitTypes.lord;
            squareSprite = false;
            alwaysUnlocked = true;
            requirements(Category.effect, with(tin, 2000, silver, 1250));
        }};
        coreCity = new DetectorCoreBlock("core-city"){{
            health = 4600;
            size = 4;
            unitCapModifier = 10;
            itemCapacity = 5000;
            unitType = FOSUnitTypes.king;
            squareSprite = false;
            requirements(Category.effect, with(tin, 2500, silver, 2000, diamond, 750, silicon, 2500, vanadium, 1500));
        }};
        coreMetropolis = new DetectorCoreBlock("core-metropolis"){{
            health = 8000;
            size = 5;
            unitCapModifier = 14;
            itemCapacity = 8000;
            unitType = FOSUnitTypes.king; //TODO: replace
            squareSprite = false;
            requirements(Category.effect, with(tin, 150000));
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
            requirements(Category.effect, BuildVisibility.debugOnly, with(lead, 5000, silicon, 5000, titanium, 3500, thorium, 2500, phaseFabric, 1500, surgeAlloy, 1500));
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
        surfaceDetonator = new SurfaceExplosive("surface-detonator"){{
            health = 160;
            size = 3;
            requirements(Category.effect, with(diamond, 100, brass, 75, vanadium, 100)); // TODO: temporary recipe
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
            requirements(Category.effect, BuildVisibility.debugOnly, with(tin, 250, silver, 300, silicon, 150));
        }};

        liquidConveyor = new LiquidConveyor("l-c"){{
            requirements(Category.distribution, BuildVisibility.debugOnly, with(Items.copper, 1));
            health = 45;
            speed = 0.03f;
            displayedSpeed = 4.2f;
            researchCost = with(Items.copper, 5);
        }};

        soontm = new PlaceholderBlock();
        citadelSpawner = new WaveSpawnerBlock("citadel-spawner"){{
            size = 4;
            unitType = citadel;
            wave = 20;
            spawnDelay = 300f;
            customShadow = true;
            buildVisibility = BuildVisibility.editorOnly;
            animationEffects = new Effect[]{FOSFx.citadelSteam, Fx.smokeCloud};
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawRegion("-cannon"){{
                    x = -8; y = -10;
                }},
                new DrawRegion("-cannon"){{
                    x = 8; y = -10;
                }},
                new DrawPistons(){{
                    suffix = "-arm";
                    sides = 2;
                    lenOffset = 5f;
                    sinScl = 1f;
                    sinMag = 16f;
                }}
            );
        }};
    }
}
