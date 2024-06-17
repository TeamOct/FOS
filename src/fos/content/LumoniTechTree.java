package fos.content;

import arc.struct.Seq;
import mindustry.type.ItemStack;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSFluids.tokicite;
import static fos.content.FOSItems.*;
import static fos.content.FOSObjectives.*;
import static fos.content.FOSSectors.*;
import static fos.content.FOSUnitTypes.*;
import static fos.content.FOSWeaponModules.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.TechTree.*;
import static mindustry.game.Objectives.*;

public class LumoniTechTree {
    public static void load() {
        FOSPlanets.lumoni.techTree = nodeRoot("@planet.fos-lumoni.name", coreFortress, true, () -> {
            // CORES
            node(coreCity, () ->
                soontm()
/*
                node(coreMetropolis)
*/
            );

            // TRANSPORT. TODO
            node(zincBelt, () ->
                node(zincJunction, () ->
                    node(zincRouter, () -> {
                        node(zincDistributor);
                        node(zincSorter, () ->
                            node(flowGate)
                        );
                        node(zincBridge);
                    })
                )
            );

            // POWER. TODO
            node(windTurbine, () -> {
                node(zincWire, () -> {
                    node(zincWirePole);
                    node(copperWire, () -> {
                        node(copperWirePole);
                        node(brassWire, () -> {
                            node(brassWirePole);
                        });
                    });
                });
                node(copperBattery, () ->
                    node(brassBattery)
                );
                node(beamMender, () -> {
                    soontm();
                });
                soontm();
            });

            // WALLS
            node(zincWall, () -> {
                node(zincWallLarge);
                node(diamondWall, () -> {
                    node(diamondWallLarge);
                    node(vanadiumWall, () -> {
                        node(vanadiumWallLarge);
                        soontm();
                    });
                });
            });

            // UNIT FACTORIES. TODO
            node(destroyerFactory, Seq.with(new SectorComplete(intruders)), () -> {
                node(assault, ItemStack.with(), Seq.with(new Research(destroyerFactory)), () -> {
                    node(abrupt, Seq.with(new Research(simpleReconstructor)), () -> {
                        soontm();
                    });
                });
            });
            node(eliminatorFactory, ItemStack.with(), Seq.with(new Research(destroyerFactory)), () -> {
                node(radix, ItemStack.with(), Seq.with(new Research(eliminatorFactory)), () -> {
                    node(foetus, Seq.with(new Research(simpleReconstructor)), () -> {
                        soontm();
                    });
                });
            });
            node(injectorFactory, ItemStack.with(), Seq.with(new Research(destroyerFactory)), () -> {
                node(sergeant, ItemStack.with(), Seq.with(new Research(injectorFactory)), () -> {
                    node(lieutenant, Seq.with(new Research(simpleReconstructor)), () -> {
                        soontm();
                    });
                });
            });
            node(simpleReconstructor, Seq.with(new OnSector(conflict)), () -> {
                soontm();
            });

            // DRILLS
            node(crudeDrill, () -> {
                node(improvedDrill, Seq.with(new OnSector(ruins)), () -> {
                    node(draugFactory, Seq.with(new SectorComplete(zincMiningSite)), () ->
                        node(draug, ItemStack.with(), Seq.with(new Research(draugFactory)), () -> {})
                    );
                    soontm();
/*
                    node(proficientDrill));
*/
                });
            });

            // UNDERGROUND DRILLS
            node(zincDrill, () ->
                node(silverDrill, () -> {
                    node(diamondDrill, Seq.with(new OnSector(intruders)), () -> {
                        node(surfaceDetonator);
                        soontm();
                    });
                })
            );

            // DEFENCE
            node(helix, Seq.with(new OnSector(ruins)), () -> {
                node(sticker, () -> {
                    //node(firefly, /* TODO: sector handicap */ () -> {});
                    node(particulator, () -> {
                        soontm();
                        //node(cluster)
                    });
                });
                node(dot, () -> {
                    soontm();
                    node(landMine, () -> {
                        node(matrixShieldProj);
                    });
/*
                    node(pulse);
                    node(thunder, () -> {
                        //TODO: make them researchable at the same time
                        node(judge);
                        node(newJudge);
                    });
*/
                });
            });

            // FLUIDS
            node(pumpjack, () ->
                node(copperPipe, () -> {
                    node(brassPipe);
                    node(fluidRouter, () -> {
                        node(fluidBarrel, () -> {
                            node(fluidTank);
                        });
                    });
                    node(fluidJunction, () ->
                        node(fluidBridge)
                    );
                })
            );

            // ORE DETECTORS
            node(oreDetector, () -> {
                node(oreDetectorOverclocked);
                node(oreDetectorReinforced);
            });

            // FACTORIES. TODO
            node(siliconSynthesizer, Seq.with(new OnSector(ruins)), () -> {
                node(brassSmelter, () -> {
                    soontm();
/*
                    node(arkyciteRefinery, (sector handicap), () -> {
                        // TODO: a certain other refinery block
                        soontm();
                    });
*/
                });
            });

            // CORE UNIT WEAPON MODULES
            node(upgradeCenter, () -> {
                //RIFLES
                node(standard1, () ->
                    node(standard2, () ->
                        node(standard3, () ->
                            soontm())
                    )
                );
/*
                            node(standard4, TODO: sector handicap,
                                () ->
                                node(standard5, () -> {}
                                )
                            )
                        )
                    )
                );
*/

                //SHOTGUNS
                node(shotgun1, Seq.with(new SectorComplete(FOSSectors.citadel)), () ->
                    node(shotgun2, () ->
                        node(shotgun3, () ->
                            soontm()
                        )
                    )
                );
/*
                            node(shotgun4, *//* TODO: sector handicap *//* () ->
                                node(shotgun5, () -> {}
                                )
                            )
                        )
                    )
                );
*/
                //TODO: artillery
                soontm();
                // SUPPORT
                node(support1, Seq.with(new SectorComplete(ruins)), () ->
                    node(support2, () ->
                        node(support3, () ->
                            soontm()
                        )
                    )
                );
                // BOSS-SPECIFIC WEAPONS
                node(legionFabricator, Seq.with(new DefeatBoss(legion)), () -> {
                    node(citadelStickyLauncher, Seq.with(new DefeatBoss(FOSUnitTypes.citadel)), () -> {
                        soontm();
                    });
                });
            });

            // ITEMS
            nodeProduce(zinc, () -> {
                nodeProduce(silver, () ->
                    nodeProduce(diamond, () -> {
                        nodeProduce(silicon, () -> {});
                        nodeProduce(vanadium, () ->
                            soontm()
/*
                            nodeProduce(nickel, () ->
                                nodeProduce(luminium, () -> {})
                            )
*/
                        );
                        soontm();
/*
                        nodeProduce(sulphur, Seq.with(new Research(arkyciteRefinery)), () -> {});
*/
                    })
                );
                nodeProduce(copper, () ->
                    nodeProduce(brass, () -> {})
                );
            });

            // FLUIDS
            nodeProduce(water, () -> {
                nodeProduce(tokicite, () -> {});
                nodeProduce(arkycite, () -> {
                    soontm();
                    //nodeProduce(oil, () -> {});
                });
            });

            // SECTORS
            node(awakening, () -> {
                node(ruins, Seq.with(new SectorComplete(awakening)), () -> {
                    node(intruders, Seq.with(new SectorComplete(ruins)), () -> {
                        node(conflict, Seq.with(new SectorComplete(intruders)), () -> {
                            soontm();
                        });
                        node(zincMiningSite, Seq.with(new SectorComplete(intruders)), () -> {
                            soontm();
                        });
                        node(intrudersChallenge, Seq.with(new SectorComplete(intruders)), () -> {});
                    });
                    node(FOSSectors.citadel, Seq.with(new SectorComplete(ruins)), () -> {
                        soontm();
                    });
                });
            });
        });
    }
    
    public static void soontm() {
        node(soontm, Seq.with(new TBDObjective()), () -> {});
    }
}
