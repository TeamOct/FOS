package fos.content;

import arc.struct.Seq;
import mindustry.type.ItemStack;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSFluids.tokicite;
import static fos.content.FOSItems.*;
import static fos.content.FOSObjectives.DefeatBoss;
import static fos.content.FOSUnitTypes.*;
import static fos.content.FOSWeaponModules.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.TechTree.*;
import static mindustry.game.Objectives.Research;

public class LumoniTechTree {
    public static void load() {
        FOSPlanets.lumoni.techTree = nodeRoot("@planet.fos-lumoni.name", coreFortress, true, () -> {
            // CORES
            node(coreCity, () ->
                node(coreMetropolis)
            );

            // TRANSPORT. TODO
            node(tinBelt, () -> {});

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
                    node(vanadiumWall, () ->
                        node(vanadiumWallLarge)
                    );
                });
            });

            // UNIT FACTORIES. TODO
            node(hovercraftFactory, () ->
                node(vulture));

            // DRILLS
            node(crudeDrill, () ->
                node(improvedDrill, () ->
                    node(proficientDrill))
            );

            // TURRETS
            node(helix, () -> {
                node(sticker, () -> {
                    node(particulator, () ->
                        node(cluster));
                });
                node(dot, () -> {
                    node(pulse);
                    node(thunder, () -> {
                        //TODO: make them researchable at the same time
                        node(judge);
                        node(newJudge);
                    });
                });
            });

            // UNDERGROUND DRILLS
            node(drillBase, () -> {
                node(tinDrill, () ->
                    node(silverDrill, () ->
                        {} //TODO: T2 drill base
                    )
                );

                // ORE DETECTORS
                node(oreDetector, () -> {
                    node(oreDetectorOverclocked);
                    node(oreDetectorReinforced);
                });
            });

            // FACTORIES. TODO
            node(siliconSynthesizer, () -> {
                node(brassSmelter);
                node(arkyciteRefinery /* TODO: sector handicap */
                    /* TODO: a certain other refinery block */);
            });

            // CORE UNIT WEAPON MODULES
            node(upgradeCenter, () -> {
                //RIFLES
                node(standard1, () ->
                    node(standard2, ItemStack.with(tin, 750, silver, 600), () ->
                        node(standard3, ItemStack.with(tin, 2000, diamond, 1000, silicon, 2500), () ->
                            node(standard4, /* TODO: sector handicap */ ItemStack.with(tin, 3000, silver, 2500, silicon, 4000, nickel, 2500), () ->
                                node(standard5, ItemStack.with(tin, 5000, silver, 5000, diamond, 2500, silicon, 5000, nickel, 3500, luminium, 2500), () -> {}
                                )
                            )
                        )
                    )
                );

                //SHOTGUNS
                node(shotgun1, Seq.with(new DefeatBoss(citadel)), () ->
                    node(shotgun2, ItemStack.with(tin, 750, silver, 600), () ->
                        node(shotgun3, ItemStack.with(tin, 2000, diamond, 1000, silicon, 2500), () ->
                            node(shotgun4, /* TODO: sector handicap */ ItemStack.with(tin, 3000, silver, 2500, silicon, 4000, nickel, 2500), () ->
                                node(shotgun5, ItemStack.with(tin, 5000, silver, 5000, diamond, 2500, silicon, 5000, nickel, 3500, luminium, 2500), () -> {}
                                )
                            )
                        )
                    )
                );
                //TODO: artillery
                //TODO: support
                //TODO: boss-specific weapons
            });

            // ITEMS
            nodeProduce(tin, () -> {
                nodeProduce(silver, () ->
                    nodeProduce(diamond, () -> {
                        nodeProduce(silicon, () -> {});
                        nodeProduce(vanadium, () ->
                            nodeProduce(nickel, () ->
                                nodeProduce(luminium, () -> {})
                            )
                        );
                        nodeProduce(sulphur, Seq.with(new Research(arkyciteRefinery)), () -> {});
                    })
                );
                nodeProduce(copper, () ->
                    nodeProduce(brass, () -> {})
                );
            });

            // FLUIDS
            nodeProduce(water, () -> {
                nodeProduce(tokicite, () -> {
                });
                nodeProduce(arkycite, () -> {
                    nodeProduce(oil, () -> {});
                });
            });

            //TODO: sectors
        });
    }
}
