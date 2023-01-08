package fos;

import arc.graphics.gl.FrameBuffer;
import arc.struct.Seq;
import fos.content.FOSFluids;
import fos.type.content.WeaponModule;
import fos.ui.ResearchCoreDialog;
import mindustry.Vars;
import mindustry.content.TechTree;
import mindustry.game.Objectives;
import mindustry.graphics.g3d.PlanetParams;

import static fos.content.FOSBlocks.*;
import static mindustry.content.Items.*;
import static mindustry.content.TechTree.node;
import static mindustry.type.ItemStack.*;

public class FOSVars {
    public static ResearchCoreDialog rcdialog;
    public static TechTree.TechNode mechTree, bioTree;

    /** Used in modded menu renderer. A buffer for rendering planets. */
    public static FrameBuffer menuBuffer;
    /** Used in modded menu renderer. Planet params used in menuBuffer. */
    public static PlanetParams menuParams;

    public static Seq<WeaponModule> weaponModules = Vars.content.statusEffects().copy().filter(s -> s instanceof WeaponModule).as();

    public static void load() {
        rcdialog = new ResearchCoreDialog();

        mechTree = TechTree.nodeRoot("", mechResearchCore, true, () -> {
            node(helix, with(scrap, 250), () -> {
                node(particulator, with(scrap, 1500), () -> {
                    //TODO more mid-tier turrets
                    node(cluster, with(scrap, 5250), () -> {
                    });
                });
            });
            node(sticker, with(scrap, 400), Seq.with(new Objectives.Research(FOSFluids.tokicite)), () -> {});
        });

        TechTree.roots.remove(mechTree);
    }
}
