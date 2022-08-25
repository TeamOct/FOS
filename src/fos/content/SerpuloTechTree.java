package fos.content;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.TechTree.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives.*;
import mindustry.type.ItemStack;

public class SerpuloTechTree {
    public static void load() {
        newNode(FOSBlocks.nukeLauncher, Blocks.launchPad, null, Seq.with(new SectorComplete(FOSSectors.siloTerminal)));
    }

    private static void newNode(UnlockableContent content, UnlockableContent parent, ItemStack[] req, Seq<Objective> objectives) {
        TechNode parentNode = parent.techNode;
        TechNode node = new TechNode(parentNode, content, req != null ? req : content.researchRequirements());
        node.objectives = objectives;
    }
}
