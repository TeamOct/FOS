package fos.core;

import arc.Events;
import arc.func.Prov;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.*;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.*;
import arc.struct.Seq;
import arc.util.*;
import fos.SplashTexts;
import fos.content.*;
import fos.game.EndlessBoostHandler;
import fos.graphics.FOSShaders;
import fos.ui.DamageDisplay;
import fos.ui.menus.*;
import mindustry.ai.Pathfinder;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.mod.Mod;
import mindustry.mod.Mods.LoadedMod;
import mindustry.ui.*;
import mindustry.ui.dialogs.PlanetDialog;

import java.util.Calendar;

import static arc.Core.*;
import static fos.ui.menus.FOSMenus.*;
import static mindustry.Vars.*;
import static mindustry.game.EventType.*;

/**
 * This mod's main class.
 * @author Slotterleet
 * @author nekit508
 */
public abstract class FOSMod extends Mod {
    /** This mod's damage display system. */
    public DamageDisplay dd;

    public FOSMod() {
        Events.on(ClientLoadEvent.class, e -> {
            //load this mod's settings
            loadSettings();

            //add unit types to their descriptions
            content.units().each(u ->
                u.description += ("\n" + bundle.get("unittype") + (
                    u.constructor.get() instanceof MechUnit ? bundle.get("unittype.infantry") :
                    u.constructor.get() instanceof UnitEntity ? bundle.get("unittype.flying") :
                    u.constructor.get() instanceof LegsUnit ? bundle.get("unittype.spider") :
                    u.constructor.get() instanceof UnitWaterMove ? bundle.get("unittype.ship") :
                    u.constructor.get() instanceof PayloadUnit ? bundle.get("unittype.payload") :
                    u.constructor.get() instanceof TimedKillUnit ? bundle.get("unittype.timedkill") :
                    u.constructor.get() instanceof TankUnit ? bundle.get("unittype.tank") :
                    u.constructor.get() instanceof ElevationMoveUnit ? bundle.get("unittype.hover") :
                    u.constructor.get() instanceof BuildingTetherPayloadUnit ? bundle.get("unittype.tether") :
                    u.constructor.get() instanceof CrawlUnit ? bundle.get("unittype.crawl") :
                    ""
                    )
                + (u.weapons.contains(w -> w.bullet.heals()) ? bundle.get("unittype.support") : ""))
            );

            //disclaimer for non-debug
            if (FOSVars.earlyAccess && !FOSVars.debug)
                ui.showOkText("@fos.earlyaccesstitle", bundle.get("fos.earlyaccess"), () -> {});

            //unlock every planet if debug
            if (FOSVars.debug)
                PlanetDialog.debugSelect = true;

            //check for "Fictional Octo System OST" mod. if it doesn't exist, prompt to download from GitHub
            LoadedMod ost = mods.getMod("fosost");
            if (ost == null) {
                if (!settings.getBool("fos-ostdontshowagain")) {
                    ui.showCustomConfirm("@fos.noosttitle", bundle.get("fos.noost"),
                        "@mods.browser.add", "@no",
                        () -> ui.mods.githubImportMod("TeamOct/FOS-OST", true), () -> {});
                }
            } else if (!ost.enabled()) {
                ui.showCustomConfirm("@fos.ostdisabledtitle", bundle.get("fos.ostdisabled"),
                    "@yes", "@no",
                    () -> {
                        mods.setEnabled(ost, true);
                        ui.showInfoOnHidden("@mods.reloadexit", () -> app.exit());
                    }, () -> {});
            }

            Element menu = ((Element) Reflect.get(ui.menufrag, "container")).parent.parent;
            Group menuCont = menu.parent;
            menuCont.addChildBefore(menu, new Element(){
                @Override
                public void draw() {
                    FOSVars.menuRenderer.render();
                }
            });

            int tn = settings.getInt("fos-menutheme");
            MenuBackground bg = (
                tn == 2 ? uxerdSpace :
                tn == 3 ? lumoniSpace :
                tn == 4 ? random :
                tn == 5 ? solarSystem :
                tn == 6 ? caldemoltSystem :
                tn == 7 ? lumoniTerrain :
                null);
            if (bg != null) {
                FOSVars.menuRenderer.changeBackground(bg);
            }
        });

        Events.run(Trigger.update, () -> {
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
            if (settings.getBool("fos-realisticmode") && state.rules.sector != null && !state.rules.sector.planet.hasAtmosphere) {
                audio.soundBus.setVolume(0f);
            } else {
                audio.soundBus.setVolume(settings.getInt("sfxvol") / 100f);
            }
        });
    }

    @Override
    public void init() {
        //initialize mod variables
        FOSVars.load();

        //this flowfield is required for modded AIs
        Pathfinder.Flowfield pt = FOSVars.fpos;
        Reflect.<Seq<Prov<Pathfinder.Flowfield>>>get(pathfinder, "fieldTypes").add(() -> pt);
        Events.on(WorldLoadEvent.class, e -> {
            if (!net.client()) {
                Reflect.invoke(pathfinder, "preloadPath", new Object[]{pt}, Pathfinder.Flowfield.class);
            }
        });

        //anything after this should not be initialized on dedicated servers.
        if (headless) return;

        //an anti-cheat system from long ago, is it really necessary now?
        LoadedMod xf = mods.list().find(m ->
            /* some mods don't even have the author field, apparently. how stupid. */ m.meta.author != null &&
            (m.meta.author.equals("XenoTale") || m.meta.author.equals("goldie")));
        if (xf != null) {
            ui.showOkText("@fos.errortitle", bundle.format("fos.errortext", xf.meta.displayName), () -> app.exit());
        }

        //locate this mod, for later use
        LoadedMod mod = mods.locateMod("fos");

        //load splash texts
        SplashTexts.load();
        int n = Mathf.floor((float) Math.random() * SplashTexts.splashes.size);

        //change something on certain days
        var date = FOSVars.date;

        //get a random splash text
        boolean isNewYear = date.get(Calendar.MONTH) == Calendar.JANUARY && date.get(Calendar.DAY_OF_MONTH) == 1;
        mod.meta.subtitle = isNewYear ? bundle.get("splashnewyear") : SplashTexts.splashes.get(n);

        //mistake.mp3
        boolean isAprilFools = date.get(Calendar.MONTH) == Calendar.APRIL && date.get(Calendar.DAY_OF_MONTH) == 1;
        if (isAprilFools) Musics.menu = tree.loadMusic("mistake");

        //display the mod version
        mod.meta.description += "\n\n" + bundle.get("mod.currentversion") + "\n" + mod.meta.version;

        //load icons and menu themes
        FOSIcons.load();
        FOSMenus.load();

        //add a couple of buttons to in-game editor
        ui.editor.shown(this::addEditorTeams);

        //damage display
        dd = new DamageDisplay();

        //endless boost handler
        new EndlessBoostHandler();

        //add a new font page for... reasons
        Seq<Font> fonts = Seq.with(Fonts.def, Fonts.outline);
        fonts.each(f -> {
            var regions = f.getRegions();
            regions.add(new TextureRegion());
        });

        //init modded teams
        FOSTeam.load();
    }

    @Override
    public void loadContent() {
        FOSShaders.init();
        FOSCommands.init();

        FOSAttributes.load();
        FOSWeathers.load();
        FOSItems.load();
        FOSFluids.load();
        FOSWeaponModules.load();
        FOSBullets.load();
        FOSStatuses.load();
        FOSUnits.load();
        FOSBlocks.load();
        FOSSchematics.load();
        FOSPlanets.load();
        FOSSectors.load();

        LumoniTechTree.load();
        SerpuloTechTree.load();
        UxerdTechTree.load();
    }

    private void loadSettings() {
        ui.settings.addCategory("@setting.fos-title", "fos-settings-icon", t -> {
            t.sliderPref("fos-menutheme", 2, 1, 7, s ->
                s == 2 ? "@setting.fos-menutheme.uxerdspace" :
                s == 3 ? "@setting.fos-menutheme.lumonispace" :
                s == 4 ? "@setting.fos-menutheme.randomplanet" :
                s == 5 ? "@setting.fos-menutheme.solarsystem" :
                s == 6 ? "@setting.fos-menutheme.caldemoltsystem" :
                s == 7 ? "@setting.fos-menutheme.lumoniterrain" :
                "@setting.fos-menutheme.default");
            t.checkPref("fos-rotatemenucamera", true);
            t.checkPref("fos-damagedisplay", true);
            t.sliderPref("fos-damagedisplayfrequency", 30, 3, 120, 3, s ->
                bundle.format("setting.seconds", s / 60f));
            t.checkPref("fos-ostdontshowagain", false);
            t.checkPref("fos-realisticmode", false);
            t.checkPref("fos-refreshsplash", false, b -> {
                Time.run(60f, () ->
                    settings.put("fos-refreshsplash", false)
                );
                int n = Mathf.floor((float) Math.random() * SplashTexts.splashes.size);
                mods.locateMod("fos").meta.subtitle = SplashTexts.splashes.get(n);
            });
            t.checkPref("fos-debugmode", false, b -> {
                if (b) {
                    ui.showConfirm("@warning", "@fos-dangerzone", () -> {
                        settings.put("fos-debugmode", true);
                    });
                } else
                    settings.put("fos-debugmode", false);
            });
        });
    }

    private void addEditorTeams() {
        //thanks java.
        WidgetGroup teambuttons = (WidgetGroup)ui.editor.getChildren().get(0);
        teambuttons = (WidgetGroup)teambuttons.getChildren().get(0);
        teambuttons = (WidgetGroup)teambuttons.getChildren().get(0);

        ((Table)teambuttons).row();

        for (int i = 69; i <= 70; i++) {
            Team team = Team.get(i);

            ImageButton button = new ImageButton(Tex.whiteui, Styles.clearNoneTogglei);
            button.margin(4f);
            button.getImageCell().grow();
            button.getStyle().imageUpColor = team.color;
            button.clicked(() -> editor.drawTeam = team);
            button.update(() -> button.setChecked(editor.drawTeam == team));

            ((Table)teambuttons).add(button);
        }
    }
}
