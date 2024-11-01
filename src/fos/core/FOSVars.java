package fos.core;

import arc.Core;
import arc.struct.Seq;
import fos.ai.FOSPathfinder;
import fos.content.FOSSectors;
import fos.files.InternalFileTree;
import fos.graphics.FOSOreRenderer;
import fos.mod.*;
import fos.ui.*;
import fos.ui.menus.FOSMenuRenderer;
import mindustry.ai.Pathfinder;
import mindustry.content.TechTree;
import mindustry.entities.units.BuildPlan;
import mindustry.mod.Mods;

import java.util.*;

import static mindustry.Vars.*;

public class FOSVars {
    /** A research dialog that shows one of the two tech trees declared below. */
    public static ResearchCoreDialog researchCoreDialog;
    /** Special tech trees accessed only by certain blocks. */
    public static TechTree.TechNode mechTree, bioTree;
    /** Capsules creator **/
    public static CapsulesController capsulesController;

    /** Contains the map for recent unit deaths. Used in insect AI. */
    public static DeathMapController deathMapController = new DeathMapController();
    /** An additional modded pathfinder for insect AI. Should not affect vanilla {@link Pathfinder}. */
    public static FOSPathfinder pathfinder = new FOSPathfinder();

    /** Enabling debug mode debug **/
    public static final boolean debug = Core.settings.getBool("fos-debugmode");
    // TODO disable in release version
    /** Early access **/
    public static final boolean earlyAccess = true;

    /** Current date & time. */
    public static Calendar date = new GregorianCalendar();

    public static boolean isAprilFools = FOSVars.date.get(Calendar.MONTH) == Calendar.APRIL && FOSVars.date.get(Calendar.DAY_OF_MONTH) == 1;

    /** JAR internal navigation **/
    public static InternalFileTree internalTree = new InternalFileTree(FOSMod.class);

    /** Main menu renderer **/
    public static FOSMenuRenderer menuRenderer = new FOSMenuRenderer();

    /** Ore cache renderer **/
    public static FOSOreRenderer oreRenderer;

    /** Mod reference **/
    public static Mods.LoadedMod mod;

    /** Placement plans stored for wire bridging. DO NOT MODIFY! */
    public static Seq<BuildPlan> wirePlans = new Seq<>();

    /** Insect evolution controller. */
    public static EvolutionController evoController = new EvolutionController();
    /** Wind power controller. */
    public static WindPowerController windController = new WindPowerController();

    /** Hint handler. */
    public static FOSHints hints = new FOSHints();

    public static void load() {
/*
        TODO: probably scrapped.
        if (!headless) researchCoreDialog = new ResearchCoreDialog();

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
*/
    }

    public static boolean mapStarted() {
        return state.rules.sector != null && state.rules.sector == FOSSectors.awakening.sector ?
            state.tick >= 160 :
            renderer.getLandTime() <= 0;
    }
}
