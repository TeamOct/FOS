package fos.content;

import arc.struct.Seq;
import fos.type.units.LuminaBossType;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSItems.*;
import static fos.content.FOSObjectives.*;
import static fos.content.FOSUnits.*;
import static fos.content.FOSWeaponModules.*;
import static mindustry.content.Items.*;
import static mindustry.content.TechTree.*;

public class LuminaTechTree {
    public static void load() {
        FOSPlanets.lumina.techTree = nodeRoot("@planet.fos-lumina.name", FOSBlocks.coreFortress, true, () -> {
            node(tinBelt, () -> {});
            node(windTurbine, () -> {});
            node(tinWall, () -> {
                node(tinWallLarge);
                node(diamondWall, () -> {
                    node(diamondWallLarge);
                });
            });
            node(hovercraftFactory, () -> {
                node(vulture);
            });
            node(drillBase2, () -> {
                node(tinDrill, () -> {
                    node(silverDrill, () -> {
                        node(siliconSynthesizer);
                    });
                    node(oreDetector);
                    node(upgradeCenter);
                });
            });
            node(FOSUnits.temp, () -> {
                node(standard1, () ->
                    node(standard2, Seq.with(new DefeatBoss((LuminaBossType) testBoss)), () -> {}));
            });
            nodeProduce(tin, () -> {
                nodeProduce(silver, () -> {
                    nodeProduce(diamond, () -> {
                        nodeProduce(silicon, () -> {});
                        nodeProduce(vanadium, () -> {
                            nodeProduce(iridium, () -> {
                                nodeProduce(luminium, () -> {});
                            });
                        });
                    });
                });
            });
        });
    }
}
