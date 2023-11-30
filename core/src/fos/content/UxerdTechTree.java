package fos.content;

import arc.struct.*;
import mindustry.content.Liquids;
import mindustry.game.Objectives.*;
import mindustry.type.Item;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSItems.*;
import static mindustry.content.Blocks.coreNucleus;
import static mindustry.content.Items.titanium;
import static mindustry.content.TechTree.*;

public class UxerdTechTree {
    static ObjectFloatMap<Item> costs = new ObjectFloatMap<>();

    public static void load() {
        costs.put(aluminium, 0.05f);
        costs.put(lithium, 0.08f);
        costs.put(tin, 0.06f);
        costs.put(silver, 0.06f);
        costs.put(cuberium, 0.03f);
        costs.put(titanium, 0.08f);

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
                    nodeProduce(FOSFluids.oxygen, () -> {});
            }));
            nodeProduce(rawElbium, () -> {
                nodeProduce(tin, () -> {});
                nodeProduce(lithium, () -> {});
            });
            nodeProduce(rawElithite, () -> {
                nodeProduce(titanium, () -> {});
                nodeProduce(silver, () -> nodeProduce(cuberium, Seq.with(new Research(tin), new Research(titanium), new Research(FOSFluids.oxygen)), () -> {}));
            });
            node(rockCrusher, Seq.with(new OnPlanet(FOSPlanets.uxerd)), () -> node(heatGenerator, () -> {
                node(solarPanelMedium);
                node(resourceExtractor, () -> {
                    node(cliffDetonator);
                    node(sublimator, () -> node(fluidPipe));
                    node(cuberiumSynthesizer, () -> {
                        /* TODO: move to Lumoni */ node(orbitalAccelerator, Seq.with(new Research(coreFortress)), () -> {});
                        node(bigBoy);
                    });
                });
                node(plasmaLauncher);
                node(oreDetectorSmall, () ->
                    node(tinDrill)
                );
            }));
        });
        FOSPlanets.uxerd.unlockedOnLand = Seq.with(spaceDuct, rockCrusher, heatGenerator);
    }
}
