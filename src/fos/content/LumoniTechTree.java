package fos.content;

import arc.struct.Seq;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSFluids.tokicite;
import static fos.content.FOSItems.*;
import static fos.content.FOSSectors.*;
import static fos.content.FOSUnitTypes.*;
import static fos.content.FOSWeaponModules.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.TechTree.*;

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
            node(tinBelt, () -> {
                node(tinJunction, () -> {
                    node(tinRouter, () -> {
                        node(tinBridge);
                    });
                });
            });

            // POWER. TODO
            node(windTurbine, () -> {
                node(tinWire, () -> {
                    node(tinWirePole);
                    node(copperWire, () -> {
                        node(copperWirePole);
                        node(brassWire, () -> {
                            node(brassWirePole);
                        });
                    });
                }
                );
                node(copperBattery, () ->
                    node(brassBattery)
                );
            });

            // WALLS
            node(tinWall, () -> {
                node(tinWallLarge);
                node(diamondWall, () -> {
                    node(diamondWallLarge);
                    node(vanadiumWall, () -> {
                        node(vanadiumWallLarge);
                        soontm();
                    });
                });
            });

            // UNIT FACTORIES. TODO
            node(destroyerFactory, Seq.with(new Objectives.SectorComplete(intruders)), () -> {
                node(assault, ItemStack.with(), Seq.with(new Objectives.Research(destroyerFactory)), () -> {
                    node(abrupt, Seq.with(new Objectives.Research(simpleReconstructor)), () -> {
                        soontm();
                    });
                });
            });
            node(eliminatorFactory, ItemStack.with(), Seq.with(new Objectives.Research(destroyerFactory)), () -> {
                node(radix, ItemStack.with(), Seq.with(new Objectives.Research(eliminatorFactory)), () -> {
                    node(foetus, Seq.with(new Objectives.Research(simpleReconstructor)), () -> {
                        soontm();
                    });
                });
            });
            node(injectorFactory, ItemStack.with(), Seq.with(new Objectives.Research(destroyerFactory)), () -> {
                node(sergeant, ItemStack.with(), Seq.with(new Objectives.Research(injectorFactory)), () -> {
                    node(lieutenant, Seq.with(new Objectives.Research(simpleReconstructor)), () -> {
                        soontm();
                    });
                });
            });
            node(simpleReconstructor, Seq.with(new Objectives.OnSector(conflict)), () -> {
                soontm();
            });

            // DRILLS
            node(crudeDrill, () -> {
                node(improvedDrill, Seq.with(new Objectives.OnSector(ruins)), () -> {
                    node(draugFactory, Seq.with(new Objectives.SectorComplete(tinMiningSite)), () ->
                        node(draug, ItemStack.with(), Seq.with(new Objectives.Research(draugFactory)), () -> {})
                    );
                    soontm();
/*
                    node(proficientDrill));
*/
                });
            });

            // UNDERGROUND DRILLS
            node(tinDrill, () ->
                node(silverDrill, () -> {
                    node(diamondDrill, Seq.with(new Objectives.OnSector(intruders)), () -> {
                        soontm();
                    });
                })
            );

            // TURRETS
            node(helix, Seq.with(new Objectives.OnSector(ruins)), () -> {
                node(sticker, () -> {
                    node(particulator, () -> {
                        soontm();
                        //node(cluster)
                    });
                });
                node(dot, () -> {
                    soontm();
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

            // FLUID PUMP(S?)
            node(pumpjack);

            // ORE DETECTORS
            node(oreDetector, () -> {
                node(oreDetectorOverclocked);
                node(oreDetectorReinforced);
            });

            // FACTORIES. TODO
            node(siliconSynthesizer, Seq.with(new Objectives.OnSector(ruins)), () -> {
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
                    node(standard2, ItemStack.with(tin, 750, silver, 600), () ->
                        node(standard3, ItemStack.with(tin, 1250, silicon, 1000, vanadium, 500), () ->
                            soontm())
                    )
                );
/*
                            node(standard4, TODO: sector handicap,
                                ItemStack.with(tin, 3000, silver, 2500, silicon, 4000, nickel, 2500), () ->
                                node(standard5, ItemStack.with(tin, 5000, silver, 5000, diamond, 2500, silicon, 5000, nickel, 3500, luminium, 2500), () -> {}
                                )
                            )
                        )
                    )
                );
*/

                //SHOTGUNS
                node(shotgun1, Seq.with(new Objectives.SectorComplete(FOSSectors.citadel)), () ->
                    node(shotgun2, ItemStack.with(tin, 750, silver, 600), () ->
                        node(shotgun3, ItemStack.with(tin, 1000, silver, 250, silicon, 1000, vanadium, 500), () ->
                            soontm()
                        )
                    )
                );
/*
                            node(shotgun4, *//* TODO: sector handicap *//* ItemStack.with(tin, 3000, silver, 2500, silicon, 4000, nickel, 2500), () ->
                                node(shotgun5, ItemStack.with(tin, 5000, silver, 5000, diamond, 2500, silicon, 5000, nickel, 3500, luminium, 2500), () -> {}
                                )
                            )
                        )
                    )
                );
*/
                //TODO: artillery
                soontm();
                //TODO: support
                soontm();
                // BOSS-SPECIFIC WEAPONS
                node(legionFabricator, Seq.with(new FOSObjectives.DefeatBoss(legion)), () -> {
                    //TODO: citadel shotgun? or stickybomb launcher?
                    soontm();
                });
            });

            // ITEMS
            nodeProduce(tin, () -> {
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
                node(ruins, Seq.with(new Objectives.SectorComplete(awakening)), () -> {
                    node(intruders, Seq.with(new Objectives.SectorComplete(ruins)), () -> {
                        node(conflict, Seq.with(new Objectives.SectorComplete(intruders)), () -> {
                            soontm();
                        });
                    });
                    node(FOSSectors.citadel, Seq.with(new Objectives.SectorComplete(ruins)), () -> {
                        soontm();
                    });
                    node(tinMiningSite, Seq.with(new Objectives.SectorComplete(ruins)), () -> {
                        soontm();
                    });
                });
            });
        });
    }
    
    public static void soontm() {
        node(soontm, Seq.with(new FOSObjectives.TBDObjective()), () -> {});
    }
}
