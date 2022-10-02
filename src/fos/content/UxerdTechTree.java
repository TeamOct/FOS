package fos.content;

import arc.struct.*;
import mindustry.content.Liquids;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSItems.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.*;
import static mindustry.content.TechTree.*;

public class UxerdTechTree {
    static ObjectFloatMap<Item> costs = new ObjectFloatMap<>();

    public static void load() {
        costs.put(aluminium, 0.08f);
        costs.put(lithium, 0.06f);
        costs.put(tin, 0.05f);
        costs.put(silver, 0.04f);
        costs.put(cuberium, 0.03f);
        costs.put(titanium, 0.2f); //should be abundant in Serpulo anyway

        FOSPlanets.uxerd.techTree = nodeRoot("@planet.fos-uxerd.name", coreNucleus, true, () -> {
            context().researchCostMultipliers = costs;

            node(coreColony, () -> node(coreFortress));
            node(spaceDuct, Seq.with(new OnPlanet(FOSPlanets.uxerd)), () -> {
                node(spaceRouter);
                node(spaceBridge);
                node(itemCatapult, Seq.with(new Research(heatGenerator)), () -> {});
            });
            nodeProduce(rawNethratium, () ->
                nodeProduce(aluminium, () -> {
                    nodeProduce(Liquids.hydrogen, () -> {});
                    nodeProduce(FOSLiquids.oxygen, () -> {});
            }));
            nodeProduce(rawElbium, () -> {
                nodeProduce(tin, () -> {});
                nodeProduce(lithium, () -> {});
            });
            nodeProduce(rawElithite, () -> {
                nodeProduce(titanium, () -> {});
                nodeProduce(silver, () -> nodeProduce(cuberium, Seq.with(new Research(tin), new Research(titanium), new Research(FOSLiquids.oxygen)), () -> {}));
            });
            node(rockCrusher, Seq.with(new OnPlanet(FOSPlanets.uxerd)), () -> {
                node(heatGenerator, () -> {
                    node(solarPanelMedium);
                    node(resourceExtractor, () -> {
                        node(sublimer, () -> node(gasPipe));
                        node(cuberiumSynthesizer, () -> {
                            node(orbitalAccelerator, Seq.with(new Research(coreFortress)), () -> {});
                            node(bigBoy);
                        });
                    });
                    node(plasmaLauncher);
                    node(oreDetectorSmall, () ->
                        node(drillBase2, () ->
                            node(tinDrill)));
                });
            });
        });
        FOSPlanets.uxerd.unlockedOnLand = Seq.with(spaceDuct, rockCrusher, heatGenerator);
    }
}
