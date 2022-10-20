package fos.content;

import arc.graphics.gl.FrameBuffer;
import fos.type.audio.MusicHandler;
import fos.ui.ResearchCoreDialog;
import mindustry.content.TechTree;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSUnits.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.*;
import static mindustry.content.TechTree.node;
import static mindustry.type.ItemStack.*;

public class FOSVars {
    public static ResearchCoreDialog rcdialog;
    public static MusicHandler handler;
    public static TechTree.TechNode mechTree, bioTree;
    public static FrameBuffer menuBuffer;

    public static void load() {
        rcdialog = new ResearchCoreDialog();
        handler = new MusicHandler();

        mechTree = TechTree.nodeRoot("", mechResearchCore, true, () -> {
            node(hovercraftFactory, with(scrap, 700), () ->
                node(vulture, with(scrap, 1400000), () -> {}));
        });

        TechTree.roots.remove(mechTree);
    }
}
