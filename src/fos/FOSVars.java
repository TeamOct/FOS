package fos;

import arc.graphics.gl.FrameBuffer;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import fos.content.FOSFluids;
import fos.type.content.WeaponModule;
import fos.type.units.BugUnit;
import fos.ui.ResearchCoreDialog;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.content.TechTree;
import mindustry.game.Objectives;
import mindustry.gen.EntityMapping;
import mindustry.graphics.g3d.PlanetParams;

import static fos.content.FOSBlocks.*;
import static mindustry.content.Items.*;
import static mindustry.content.TechTree.node;
import static mindustry.type.ItemStack.*;

public class FOSVars {
    /** A research dialog that shows one of the two tech trees declared below. */
    public static ResearchCoreDialog rcdialog;
    /** Special tech trees accessed only by certain blocks. */
    public static TechTree.TechNode mechTree, bioTree;

    /** Used in modded menu renderer. A buffer for rendering planets. */
    public static FrameBuffer menuBuffer;
    /** Used in modded menu renderer. Planet params used in menuBuffer. */
    public static PlanetParams menuParams;

    /** An array with all weapon modules. */
    public static Seq<WeaponModule> weaponModules = Vars.content.statusEffects().copy().filter(s -> s instanceof WeaponModule).as();

    /** ID of the {@link BugUnit} class. */
    public static int bugEntity;

    /** A flowfield used in certain custom AIs. */
    public static Pathfinder.Flowfield fpos = new Pathfinder.PositionTarget(new Vec2());

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

        bugEntity = EntityMapping.register("FOSBugUnit", BugUnit::new);
    }
}
