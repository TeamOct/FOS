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
            node(meteorite, Seq.with(new Produce(meteorite)), () -> {
                node(lithium, Seq.with(new Produce(lithium)), () -> {});
                node(tin, Seq.with(new Produce(tin), new Research(oreDetectorSmall)), () -> {});
                node(silver, Seq.with(new Produce(silver), new Research(oreDetectorSmall)), () -> {});
            });
            node(meteoriteDrill, Seq.with(new OnPlanet(FOSPlanets.uxerd)), () -> {
                node(heatGenerator, () -> {
                    node(oreDetectorSmall);
                    node(bigBoy);
                });
            });
            node(particulator, () -> {
                node(pulse);
            });
        });
    }
}
