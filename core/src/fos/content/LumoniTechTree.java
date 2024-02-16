package fos.content;

import arc.struct.Seq;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSFluids.tokicite;
import static fos.content.FOSItems.*;
import static fos.content.FOSSectors.*;
import static fos.content.FOSUnitTypes.legion;
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
            node(windTurbine, () ->
                node(tinWire, () ->
                    node(copperWire, () ->
                        node(brassWire)
                    )
                )
            );

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
            soontm();
/*
            node(hovercraftFactory, () ->
                node(vulture));
*/

            // DRILLS
            node(crudeDrill, () -> {
                node(improvedDrill, Seq.with(new Objectives.OnSector(ruins)), () -> {
                    soontm();
/*
                    node(proficientDrill));
*/
                });
            });

            // TURRETS
            node(helix, Seq.with(new Objectives.OnSector(ruins)), () -> {
                node(sticker, () -> {
                    soontm();
/*
                    node(particulator, () ->
                        node(cluster));
*/
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

            // UNDERGROUND DRILLS
            node(tinDrill, () ->
                node(silverDrill, () -> {
                    //TODO: T2 drill base
                    soontm();
                })
            );

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
                        soontm()
/*
                        node(standard3, ItemStack.with(tin, 2000, diamond, 1000, silicon, 2500), () ->
                            node(standard4, TODO: sector handicap,
                                ItemStack.with(tin, 3000, silver, 2500, silicon, 4000, nickel, 2500), () ->
                                node(standard5, ItemStack.with(tin, 5000, silver, 5000, diamond, 2500, silicon, 5000, nickel, 3500, luminium, 2500), () -> {}
                                )
                            )
                        )
*/
                    )
                );

                //TODO: SHOTGUNS
                soontm();
 /*               node(shotgun1, Seq.with(new DefeatBoss(citadel)), () ->
                    node(shotgun2, ItemStack.with(tin, 750, silver, 600), () ->
                        node(shotgun3, ItemStack.with(tin, 2000, diamond, 1000, silicon, 2500), () ->
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
            node(crashLanding, () -> {
                node(ruins, Seq.with(new Objectives.SectorComplete(crashLanding)), () -> {
                    soontm();
                    soontm();
                    soontm();
                });
            });
        });
    }
    
    public static TechNode soontm() {
        return node(soontm, Seq.with(new FOSObjectives.TBDObjective()), () -> {});
    }
}
