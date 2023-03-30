package fos.content;

import mindustry.type.ItemStack;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSFluids.tokicite;
import static fos.content.FOSItems.*;
import static fos.content.FOSUnits.vulture;
import static fos.content.FOSWeaponModules.standard1;
import static fos.content.FOSWeaponModules.standard2;
import static mindustry.content.Items.copper;
import static mindustry.content.Items.silicon;
import static mindustry.content.Liquids.water;
import static mindustry.content.TechTree.*;

public class LumoniTechTree {
    public static void load() {
        FOSPlanets.lumoni.techTree = nodeRoot("@planet.fos-lumoni.name", FOSBlocks.coreFortress, true, () -> {
            node(coreCity, () ->
                node(coreMetropolis)
            );
            node(tinBelt, () -> {});
            node(windTurbine, () ->
                node(tinWire, () ->
                    node(copperWire, () ->
                        node(brassWire)
                    )
                )
            );
            node(tinWall, () -> {
                node(tinWallLarge);
                node(diamondWall, () -> {
                    node(diamondWallLarge);
                    node(vanadiumWall, () ->
                        node(vanadiumWallLarge)
                    );
                });
            });
            node(hovercraftFactory, () ->
                node(vulture));
            node(drillBase, () -> {
                node(tinDrill, () ->
                    node(silverDrill, () ->
                        node(siliconSynthesizer, () ->
                            node(brassSmelter)
                        )
                    )
                );
                node(oreDetector);
                node(upgradeCenter);
                node(hovercraftFactory, () ->
                    node(vulture, () ->
                        node(mechResearchCore)
                    )
                );
            });
            node(FOSUnits.lord, () ->
                node(standard1, () ->
                    node(standard2, ItemStack.with(tin, 750, silver, 600), () -> {})
                )
            );
            nodeProduce(tin, () -> {
                nodeProduce(silver, () -> nodeProduce(diamond, () -> {
                    nodeProduce(silicon, () -> {});
                    nodeProduce(vanadium, () ->
                            nodeProduce(iridium, () ->
                                nodeProduce(luminium, () -> {})
                            )
                        );
                    })
                );
                nodeProduce(copper, () ->
                    nodeProduce(brass, () -> {})
                );
            });
            nodeProduce(water, () ->
                nodeProduce(tokicite, () -> {})
            );
        });
    }
}
