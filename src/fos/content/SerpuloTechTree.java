package fos.content;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.SectorPresets;
import mindustry.content.TechTree.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives.*;

public class SerpuloTechTree {
    public static void load() {
        newNode(FOSBlocks.nukeLauncher, Blocks.launchPad, Seq.with(new SectorComplete(FOSSectors.siloTerminal)));
        newNode(FOSSectors.siloTerminal, SectorPresets.planetaryTerminal, Seq.with(new SectorComplete(SectorPresets.planetaryTerminal)));
    }

    private static void newNode(UnlockableContent content, UnlockableContent parent, Seq<Objective> objectives) {
        TechNode parentNode = parent.techNode;
        TechNode node = new TechNode(parentNode, content, content.researchRequirements());
        if (objectives != null) node.objectives = objectives;
    }
}
