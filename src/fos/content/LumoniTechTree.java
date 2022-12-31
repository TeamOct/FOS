package fos.content;

import mindustry.type.ItemStack;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSItems.*;
import static fos.content.FOSUnits.*;
import static fos.content.FOSWeaponModules.*;
import static mindustry.content.Items.*;
import static mindustry.content.TechTree.*;

public class LumoniTechTree {
    public static void load() {
        FOSPlanets.lumoni.techTree = nodeRoot("@planet.fos-lumoni.name", FOSBlocks.coreFortress, true, () -> {
            node(tinBelt, () -> {
                node(lightUnloader, () -> {

                });
            });
            node(windTurbine, () -> {});
            node(tinWall, () -> {
                node(tinWallLarge);
                node(diamondWall, () ->
                    node(diamondWallLarge));
            });
            node(hovercraftFactory, () ->
                node(vulture));
            node(drillBase, () -> node(tinDrill, () -> {
                node(silverDrill, () ->
                    node(siliconSynthesizer));
                node(oreDetector);
                node(upgradeCenter);
                node(hovercraftFactory, () ->
                    node(vulture, () ->
                        node(mechResearchCore)
                    )
                );
            }));
            node(FOSUnits.lord, () ->
                node(standard1, () ->
                    node(standard2, ItemStack.with(tin, 750, silver, 600), () -> {})
                )
            );
            nodeProduce(tin, () ->
                nodeProduce(silver, () -> nodeProduce(diamond, () -> {
                    nodeProduce(silicon, () -> {});
                    nodeProduce(vanadium, () ->
                        nodeProduce(iridium, () ->
                            nodeProduce(luminium, () -> {})
                            )
                        );
                    })
                )
            );
            node(helix, () -> {
                node(particulator, () -> {
                    node(cluster, () -> {

                    });
                });
                node(sticker, () -> {

                });
            });
        });
    }
}
