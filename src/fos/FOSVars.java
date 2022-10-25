package fos;

import arc.graphics.gl.FrameBuffer;
import fos.type.audio.MusicHandler;
import fos.ui.ResearchCoreDialog;
import mindustry.content.TechTree;
import mindustry.graphics.g3d.PlanetParams;

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

    /** Used in modded menu renderer. A buffer for rendering planets. */
    public static FrameBuffer menuBuffer;
    /** Used in modded menu renderer. Planet params used in menuBuffer. */
    public static PlanetParams menuParams;

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
