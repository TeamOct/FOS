package fos.content;

import fos.type.audio.MusicHandler;
import fos.ui.ResearchCoreDialog;
import mindustry.content.TechTree;

import static fos.content.FOSBlocks.*;
import static mindustry.content.Blocks.*;

public class FOSVars {
    public static ResearchCoreDialog rcdialog;
    public static MusicHandler handler;
    public static TechTree.TechNode mechTree, bioTree;

    public static void load() {
        rcdialog = new ResearchCoreDialog();
        handler = new MusicHandler();

        mechTree = TechTree.nodeRoot("", mechResearchCore, false, () -> {

        });
    }
}
