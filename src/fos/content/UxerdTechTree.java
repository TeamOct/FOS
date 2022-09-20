package fos.content;

import arc.struct.*;
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
        costs.put(titanium, 0.12f); //should be abundant in Serpulo anyway

        FOSPlanets.uxerd.techTree = nodeRoot("@planet.fos-uxerd.name", coreNucleus, true, () -> {
            context().researchCostMultipliers = costs;

            node(coreColony, () -> node(coreFortress));
            node(spaceDuct, Seq.with(new OnPlanet(FOSPlanets.uxerd)), () -> {
                node(itemCatapult, Seq.with(new Research(heatGenerator)), () -> {});
            });
            nodeProduce(rawNethratium, () -> {
                nodeProduce(aluminium, () -> {});
            });
            nodeProduce(rawElbium, () -> {
                nodeProduce(tin, () -> {});
                nodeProduce(lithium, () -> {});
            });
            nodeProduce(rawElithite, () -> {
                nodeProduce(titanium, () -> {});
                nodeProduce(silver, () -> {});
            });
            node(rockCrusher, Seq.with(new OnPlanet(FOSPlanets.uxerd)), () -> {
                node(heatGenerator, () -> {
                    node(resourceExtractor, () -> {
                        node(orbitalAccelerator, Seq.with(new Research(coreFortress)), () -> {});
                        node(bigBoy);
                    });
                    node(plasmaLauncher);
                    node(oreDetectorSmall, () -> node(drillBase2, () -> node(tinDrill)));
                });
            });
        });
        FOSPlanets.uxerd.unlockedOnLand = Seq.with(spaceDuct, rockCrusher, heatGenerator);
    }
}
