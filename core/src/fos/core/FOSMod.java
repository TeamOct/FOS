package fos.core;

import arc.*;
import arc.func.Prov;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.*;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.*;
import arc.struct.Seq;
import arc.util.*;
import fos.content.*;
import fos.controllers.CapsulesController;
import fos.gen.FosEntityMapping;
import fos.graphics.*;
import fos.net.FOSPackets;
import fos.ui.*;
import fos.ui.menus.*;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.Mod;
import mindustry.mod.Mods.LoadedMod;
import mindustry.ui.*;
import mindustry.ui.dialogs.PlanetDialog;
import mma.annotations.ModAnnotations;

import static arc.Core.settings;

@ModAnnotations.RootDirectoryPath(rootDirectoryPath = "core")
@ModAnnotations.AnnotationSettings(
        rootPackage = "fos",
        modInfoPath = "res/mod.json"
)
public class FOSMod extends Mod {
    public FOSMod() {
        Log.debug("[FOS] main class construction");
        if (FOSVars.debug)
            Log.level = Log.LogLevel.debug;

        FOSPackets.register();
        FosEntityMapping.init();

        Events.on(EventType.ClientLoadEvent.class, e -> {
            clientLoaded();
        });

        Events.run(EventType.Trigger.update, () -> {
            /* not sure if it will ever be useful now?
            if (!mobile) {
                boolean useDiscord = !OS.hasProp("nodiscord");
                if (useDiscord) {
                    RichPresence presence = new RichPresence();
                    if (!state.isCampaign()) return;
                    if (state.rules.sector.planet == FOSPlanets.uxerd) {
                        Building a = indexer.findTile(Team.sharded, 350 * 8, 350 * 8, 4000, b -> b instanceof OrbitalAcceleratorBuild);

                        presence.details = "Uxerd (FOS)";
                        presence.state = "Orbital Accelerator Progress: " + (a != null ? Mathf.round((float) a.items().total() / (float) a.block().itemCapacity * 100) : "0") + "%";

                        presence.largeImageKey = "logo";

                        try {
                            DiscordRPC.send(presence);
                        } catch (Exception ignored) {}
                    }
                }
            }
            */

            //realistic mode - no sound FX in places with no atmosphere, such as asteroids
            // FIXME запхнуть в таймер и менять звук только при изменении настроек и загрузке
            if (settings.getBool("fos-realisticmode") && Vars.state.rules.sector != null && !Vars.state.rules.sector.planet.hasAtmosphere) {
                Core.audio.soundBus.setVolume(0f);
            } else {
                Core.audio.soundBus.setVolume(settings.getInt("sfxvol") / 100f);
            }
        });
    }

    @Override
    public void loadContent() {
        Log.debug("[FOS] loading content");
        FOSVars.mod = Vars.mods.getMod(getClass());

        ConveyorSpritesPacker.pack(); // generate conveyor regions

        FOSVars.oreRenderer = new FOSOreRenderer();

        SplashTexts.load();

        FOSShaders.init();
        FOSCommands.init();

        FOSAttributes.load();
        FOSWeathers.load();
        FOSItems.load();
        FOSFluids.load();
        FOSWeaponModules.load();
        FOSBullets.load();
        FOSStatuses.load();
        FOSUnitTypes.load();
        FOSBlocks.load();
        FOSSchematics.load();
        FOSPlanets.load();
        FOSSectors.load();

        LumoniTechTree.load();
        UxerdTechTree.load();

        FOSVars.capsulesController = new CapsulesController();
        FOSVars.capsulesController.load();
    }

    @Override
    public void init() {
        Log.debug("[FOS] initialization");
        //initialize mod variables
        FOSVars.load();

        //this flowfield is required for modded AIs
        Pathfinder.Flowfield pt = FOSVars.fpos;
        Reflect.<Seq<Prov<Pathfinder.Flowfield>>>get(Vars.pathfinder, "fieldTypes").add(() -> pt);
        Events.on(EventType.WorldLoadEvent.class, e -> {
            if (!Vars.net.client()) {
                // FIXME: sometimes breaks for unknown reason
                Reflect.invoke(Vars.pathfinder, "preloadPath", new Object[]{pt}, Pathfinder.Flowfield.class);
            }
        });

        //anything after this should not be initialized on dedicated servers.
        if (Vars.headless) return;

        //an anti-cheat system from long ago, is it really necessary now?
        LoadedMod xf = Vars.mods.list().find(m ->
                /* some mods don't even have the author field, apparently. how stupid. */ m.meta.author != null &&
                (m.meta.author.equals("XenoTale") || m.meta.author.equals("goldie")));
        if (xf != null) {
            Vars.ui.showOkText("@fos.errortitle", Core.bundle.format("fos.errortext", xf.meta.displayName), () -> Core.app.exit());
        }

        SplashTexts.init();

        if (FOSVars.isAprilFools)
            Musics.menu = Core.audio.newMusic(FOSVars.internalTree.child("music/mistake.mp3"));

        //display the mod version
        FOSVars.mod.meta.description += "\n\n" + Core.bundle.get("mod.currentversion") + "\n" + FOSVars.mod.meta.version;

        //load icons and menu themes
        FOSIcons.load();
        FOSMenus.load();

        //add a couple of buttons to in-game editor
        Vars.ui.editor.shown(this::addEditorTeams);

        //damage display
        new ClassicDamageDisplay();
        new DamageDisplay();

        //add a new font page for... reasons
        Seq<Font> fonts = Seq.with(Fonts.def, Fonts.outline);
        fonts.each(f -> {
            var regions = f.getRegions();
            regions.add(new TextureRegion());
        });

        //init modded teams
        FOSTeam.load();
    }

    public void clientLoaded() {
        //load this mod's settings
        constructSettings();

        //add unit types to their descriptions
        Vars.content.units().each(u ->
                u.description += ("\n" + Core.bundle.get("unittype") + (
                u.constructor.get() instanceof MechUnit ? Core.bundle.get("unittype.infantry") :
                u.constructor.get() instanceof UnitEntity ? Core.bundle.get("unittype.flying") :
                u.constructor.get() instanceof LegsUnit ? Core.bundle.get("unittype.spider") :
                u.constructor.get() instanceof UnitWaterMove ? Core.bundle.get("unittype.ship") :
                u.constructor.get() instanceof PayloadUnit ? Core.bundle.get("unittype.payload") :
                u.constructor.get() instanceof TimedKillUnit ? Core.bundle.get("unittype.timedkill") :
                u.constructor.get() instanceof TankUnit ? Core.bundle.get("unittype.tank") :
                u.constructor.get() instanceof ElevationMoveUnit ? Core.bundle.get("unittype.hover") :
                u.constructor.get() instanceof BuildingTetherPayloadUnit ? Core.bundle.get("unittype.tether") :
                u.constructor.get() instanceof CrawlUnit ? Core.bundle.get("unittype.crawl") :
                "")
                + (u.weapons.contains(w -> w.bullet.heals()) ? Core.bundle.get("unittype.support") : ""))
        );

        //disclaimer for non-debug
        if (FOSVars.earlyAccess && !FOSVars.debug)
            Vars.ui.showOkText("@fos.earlyaccesstitle", Core.bundle.get("fos.earlyaccess"), () -> {});

        //unlock every planet if debug
        if (FOSVars.debug)
            PlanetDialog.debugSelect = true;

        //check for "Fictional Octo System OST" mod. if it doesn't exist, prompt to download from GitHub
        LoadedMod ost = Vars.mods.getMod("fosost");
        if (ost == null) {
            if (!settings.getBool("fos-ostdontshowagain")) {
                Vars.ui.showCustomConfirm("@fos.noosttitle", Core.bundle.get("fos.noost"),
                        "@mods.browser.add", "@no",
                        () -> Vars.ui.mods.githubImportMod("TeamOct/FOS-OST", true), () -> {});
            }
        } else if (!ost.enabled()) {
            Vars.ui.showCustomConfirm("@fos.ostdisabledtitle", Core.bundle.get("fos.ostdisabled"),
                    "@yes", "@no",
                    () -> {
                        Vars.mods.setEnabled(ost, true);
                        Vars.ui.showInfoOnHidden("@mods.reloadexit", () -> Core.app.exit());
                    }, () -> {});
        }

        Element menu = ((Element) Reflect.get(Vars.ui.menufrag, "container")).parent.parent;
        Group menuCont = menu.parent;
        menuCont.addChildBefore(menu, new Element(){
            @Override
            public void draw() {
                FOSVars.menuRenderer.render();
            }
        });

        int tn = settings.getInt("fos-menutheme");
        MenuBackground bg = (
                tn == 2 ? FOSMenus.uxerdSpace :
                tn == 3 ? FOSMenus.lumoniSpace :
                tn == 4 ? FOSMenus.random :
                tn == 5 ? FOSMenus.solarSystem :
                tn == 6 ? FOSMenus.caldemoltSystem :
                tn == 7 ? FOSMenus.lumoniTerrain : null);
        if (bg != null) {
            FOSVars.menuRenderer.changeBackground(bg);
        }
    }

    public void constructSettings() {
        Vars.ui.settings.addCategory("@setting.fos-title", "fos-settings-icon", t -> {
            t.sliderPref("fos-menutheme", 2, 1, 7, s ->
                s == 2 ? "@setting.fos-menutheme.uxerdspace" :
                s == 3 ? "@setting.fos-menutheme.lumonispace" :
                s == 4 ? "@setting.fos-menutheme.randomplanet" :
                s == 5 ? "@setting.fos-menutheme.solarsystem" :
                s == 6 ? "@setting.fos-menutheme.caldemoltsystem" :
                s == 7 ? "@setting.fos-menutheme.lumoniterrain" :
                "@setting.fos-menutheme.default");
            t.checkPref("fos-rotatemenucamera", true);
            t.checkPref("fos-animatedore", true);
            t.checkPref("fos-classicdamagedisplay", false, b -> {
                if (b) {
                    settings.put("fos-damagedisplay", false);
                }
            });
            t.checkPref("fos-damagedisplay", true, b -> {
                if (b) {
                    settings.put("fos-classicdamagedisplay", false);
                }
            });
            t.sliderPref("fos-damagedisplayfrequency", 30, 3, 120, 3, s ->
                Core.bundle.format("setting.seconds", s / 60f));
            t.checkPref("fos-ostdontshowagain", false);
            t.checkPref("fos-realisticmode", false);
            t.checkPref("fos-refreshsplash", false, b -> {
                Time.run(60f, () ->
                        settings.put("fos-refreshsplash", false)
                );
                int n = Mathf.floor((float) Math.random() * SplashTexts.splashes.size);
                Vars.mods.locateMod("fos").meta.subtitle = SplashTexts.splashes.get(n);
            });
            t.checkPref("fos-debugmode", false, b -> {
                if (b) {
                    Vars.ui.showConfirm("@warning", "@fos-dangerzone", () -> {
                        settings.put("fos-debugmode", true);
                    });
                } else
                    settings.put("fos-debugmode", false);
            });
        });
    }

    public void addEditorTeams() {
        //java sucks
        WidgetGroup teambuttons = (WidgetGroup)Vars.ui.editor.getChildren().get(0);
        teambuttons = (WidgetGroup)teambuttons.getChildren().get(0);
        teambuttons = (WidgetGroup)teambuttons.getChildren().get(0);

        ((Table)teambuttons).row();

        for (int i = 69; i <= 70; i++) {
            Team team = Team.get(i);

            ImageButton button = new ImageButton(Tex.whiteui, Styles.clearNoneTogglei);
            button.margin(4f);
            button.getImageCell().grow();
            button.getStyle().imageUpColor = team.color;
            button.clicked(() -> Vars.editor.drawTeam = team);
            button.update(() -> button.setChecked(Vars.editor.drawTeam == team));

            ((Table)teambuttons).add(button);
        }
    }
}
