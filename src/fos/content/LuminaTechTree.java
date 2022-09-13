package fos.content;

import arc.struct.Seq;
import fos.type.units.LuminaBossType;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSItems.*;
import static fos.content.FOSObjectives.*;
import static fos.content.FOSUnits.*;
import static fos.content.FOSWeaponModules.*;
import static mindustry.content.TechTree.*;

public class LuminaTechTree {
    public static void load() {
        FOSPlanets.lumina.techTree = nodeRoot("@planet.fos-lumina.name", FOSBlocks.coreFortress, true, () -> {
            node(tinBelt, () -> {});
            node(windTurbine, () -> {});
            node(tinWall, () -> {
                node(tinWallLarge);
                node(silverWall, () -> {
                    node(silverWallLarge);
                });
            });
            node(drillBase2, () -> {
                node(tinDrill, () -> {
                    node(oreDetector);
                    node(upgradeCenter);
                });
            });
            node(FOSUnits.temp, () -> {
                node(standard1, () ->
                    node(standard2, Seq.with(new DefeatBoss((LuminaBossType) testBoss)), () -> {}));
            });
            nodeProduce(tin, () -> {
                nodeProduce(silver, () -> {});
            });
        });
    }
}
