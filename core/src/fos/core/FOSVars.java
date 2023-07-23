package fos.core;

import arc.Core;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import fos.content.FOSFluids;
import fos.controllers.CapsulesController;
import fos.files.InternalFileTree;
import fos.type.units.constructors.*;
import fos.ui.DamageDisplay;
import fos.ui.ResearchCoreDialog;
import fos.ui.menus.FOSMenuRenderer;
import mindustry.ai.Pathfinder;
import mindustry.content.TechTree;
import mindustry.game.Objectives;
import mindustry.gen.EntityMapping;

import java.util.*;

import static fos.content.FOSBlocks.*;
import static mindustry.content.Items.scrap;
import static mindustry.content.TechTree.node;
import static mindustry.type.ItemStack.with;

public class FOSVars {
    /** A research dialog that shows one of the two tech trees declared below. */
    public static ResearchCoreDialog researchCoreDialog;
    /** Special tech trees accessed only by certain blocks. */
    public static TechTree.TechNode mechTree, bioTree;
    /** This mod's damage display system. */
    public static DamageDisplay damageDisplay;

    /** Capsules creator **/
    public static CapsulesController capsulesController;

    /** ID of the {@link BugUnit} class. */
    public static int bugEntity;
    /** ID of the {@link BugFlyingUnit} class. */
    public static int bugFlyingEntity;
    /** ID of the {@link SubmarineUnit} class. */
    public static int subEntity;

    /** A flowfield used in certain custom AIs. */
    public static Pathfinder.Flowfield fpos = new Pathfinder.PositionTarget(new Vec2());

    // TODO disable in release version
    /** Enabling debug mode debug **/
    public static final boolean debug = Core.settings.getBool("fos-debugmode");

    // TODO disable in release version
    /** Early access **/
    public static final boolean earlyAccess = true;

    /** Current date & time. */
    public static Calendar date = new GregorianCalendar();

    /** JAR internal navigation **/
    public static InternalFileTree internalTree = new InternalFileTree(FOSMod.class);

    /** Main menu renderer **/
    public static FOSMenuRenderer menuRenderer = new FOSMenuRenderer();

    public static void load() {
        researchCoreDialog = new ResearchCoreDialog();

        mechTree = TechTree.nodeRoot("", mechResearchCore, true, () -> {
            node(helix, with(scrap, 250), () -> {
                node(sticker, with(scrap, 400), Seq.with(new Objectives.Research(FOSFluids.tokicite)), () -> {});
                node(particulator, with(scrap, 1500), () -> {
                    //TODO more mid-tier turrets
                    node(cluster, with(scrap, 5250), () -> {
                    });
                    node(judge, with(scrap, 20000), () -> {});
                });
            });
        });

        TechTree.roots.remove(mechTree);

        bugEntity = EntityMapping.register("FOSBugUnit", BugUnit::new);
        bugFlyingEntity = EntityMapping.register("FOSBugFlyingUnit", BugFlyingUnit::new);
        subEntity = EntityMapping.register("FOSSubmarineUnit", SubmarineUnit::new);
    }
}
