package fos.content;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.game.Objectives.*;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSItems.*;
import static mindustry.content.TechTree.*;

public class UxerdTechTree {
    public static void load() {
        FOSPlanets.uxerd.techTree = nodeRoot("@planet.fos-uxerd.name", Blocks.coreNucleus, true, () -> {
            node(coreFortress);
            node(spaceDuct, Seq.with(new OnPlanet(FOSPlanets.uxerd)), () -> {
                node(itemCatapult, Seq.with(new Research(heatGenerator)), () -> {});
            });
            nodeProduce(rawNethratium, () -> {
                nodeProduce(aluminium, () -> {});
                nodeProduce(rawElbium, () -> {
                    nodeProduce(tin, () -> {});
                    nodeProduce(lithium, () -> {});
                });
                nodeProduce(rawElithite, () -> {
                    nodeProduce(Items.titanium, () -> {});
                    nodeProduce(silver, () -> {});
                });
            });
            node(rockCrusher, Seq.with(new OnPlanet(FOSPlanets.uxerd)), () -> {
                node(heatGenerator, () -> {
                    node(resourceExtractor);
                    node(plasmaLauncher);
                    node(oreDetectorSmall);
                    node(bigBoy);
                });
            });
            //TODO what's the reason to research turrets without any threats?
            /*node(particulator, () -> {
                node(pulse);
            });*/
        });
        FOSPlanets.uxerd.unlockedOnLand = Seq.with(spaceDuct, rockCrusher, heatGenerator);
    }
}
