package fos.content;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.game.Objectives.*;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSItems.*;
import static mindustry.content.TechTree.*;

public class UxerdTechTree {
    public static void load() {
        FOSPlanets.uxerd.techTree = nodeRoot("Uxerd", Blocks.coreNucleus, true, () -> {
            node(spaceDuct, Seq.with(new OnPlanet(FOSPlanets.uxerd)), () -> {
                node(itemCatapult, Seq.with(new Research(heatGenerator)), () -> {});
            });
            node(meteorite, Seq.with(new OnPlanet(FOSPlanets.uxerd)), () -> {
                nodeProduce(lithium, () -> {});
                nodeProduce(tin, () -> {});
                nodeProduce(silver, () -> {});
            });
            node(meteoriteDrill, Seq.with(new OnPlanet(FOSPlanets.uxerd)), () -> {
                node(heatGenerator, () -> {
                    node(plasmaLauncher);
                    node(oreDetectorSmall);
                    node(bigBoy);
                });
            });
            node(particulator, () -> {
                node(pulse);
            });
        });
        FOSPlanets.uxerd.unlockedOnLand = Seq.with(spaceDuct, meteorite, meteoriteDrill);
    }
}
