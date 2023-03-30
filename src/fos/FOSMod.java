package fos;

import arc.Events;
import arc.func.Prov;
import arc.math.Mathf;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.scene.ui.layout.WidgetGroup;
import arc.struct.Seq;
import arc.util.Reflect;
import fos.content.*;
import fos.graphics.FOSShaders;
import fos.ui.DamageDisplay;
import fos.ui.menus.FOSMenuRenderer;
import fos.ui.menus.FOSMenus;
import fos.ui.menus.MenuBackground;
import mindustry.ai.Pathfinder;
import mindustry.content.SectorPresets;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.mod.Mod;
import mindustry.mod.Mods.LoadedMod;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.PlanetDialog;
import mindustry.ui.fragments.MenuFragment;

import java.util.Calendar;

import static arc.Core.*;
import static fos.ui.menus.FOSMenus.*;
import static mindustry.Vars.*;
import static mindustry.game.EventType.*;

public class FOSMod extends Mod {
    public FOSMod() {
        Events.on(ClientLoadEvent.class, e -> {
            loadSettings();

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

            if (FOSVars.earlyAccess && !FOSVars.debug)
                ui.showOkText("@fos.earlyaccesstitle", bundle.get("fos.earlyaccess"), () -> {});

            if (FOSVars.debug)
                PlanetDialog.debugSelect = true;

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

            int tn = settings.getInt("fos-menutheme");
            MenuBackground bg = (
                tn == 2 ? uxerdSpace :
                tn == 3 ? lumoniSpace :
                tn == 4 ? random :
                tn == 5 ? solarSystem :
                tn == 6 ? caldemoltSystem :
                tn == 7 ? lumoniTerrain :
                null);
            if (tn != 1) {
                Reflect.set(MenuFragment.class, ui.menufrag, "renderer", new FOSMenuRenderer(bg));
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

            if (SectorPresets.planetaryTerminal.sector.info.wasCaptured && !FOSPlanets.uxerd.unlocked()) {
                FOSPlanets.uxerd.unlock();
            }

            if (settings.getBool("fos-realisticmode") && state.rules.sector != null && !state.rules.sector.planet.hasAtmosphere) {
                audio.soundBus.setVolume(0f);
            } else {
                audio.soundBus.setVolume(settings.getInt("sfxvol") / 100f);
            }
        });
    }

    @Override
    public void init() {
        FOSVars.load();

        //required for modded AIs
        Pathfinder.Flowfield pt = FOSVars.fpos;
        Reflect.<Seq<Prov<Pathfinder.Flowfield>>>get(pathfinder, "fieldTypes").add(() -> pt);
        Events.on(WorldLoadEvent.class, e -> {
            if (!net.client()) {
                Reflect.invoke(pathfinder, "preloadPath", new Object[]{pt}, Pathfinder.Flowfield.class);
            }
        });

        if (headless) return;

        LoadedMod xf = mods.list().find(m ->
            /* some mods don't even have the author field apparently */ m.meta.author != null &&
            (m.meta.author.equals("XenoTale") || m.meta.author.equals("goldie")));
        if (xf != null) {
            ui.showOkText("@fos.errortitle", bundle.format("fos.errortext", xf.meta.displayName), () -> app.exit());
        }

        LoadedMod mod = mods.locateMod("fos");

        SplashTexts.load(13);
        int n = Mathf.floor((float) Math.random() * SplashTexts.splashes.size);

        var date = FOSVars.date;
        boolean isNewYear = date.get(Calendar.MONTH) == Calendar.JANUARY && date.get(Calendar.DAY_OF_MONTH) == 1;
        mod.meta.subtitle =
            isNewYear ? bundle.get("splashnewyear")
            : SplashTexts.splashes.get(n);
        boolean isAprilFools = date.get(Calendar.MONTH) == Calendar.APRIL && date.get(Calendar.DAY_OF_MONTH) == 1;
        if (isAprilFools) Musics.menu = tree.loadMusic("mistake");

        mod.meta.description += "\n\n" + bundle.get("mod.currentversion") + "\n" + mod.meta.version;

        FOSIcons.load();
        FOSMenus.load();

        ui.editor.shown(this::addEditorTeams);

        new DamageDisplay();
    }

    @Override
    public void loadContent() {
        FOSShaders.init();

        FOSTeam.load();
        FOSMusic.load();
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
            t.sliderPref("fos-menutheme", 2, 1, 7, i ->
                i == 2 ? "@setting.fos-menutheme.uxerdspace" :
                i == 3 ? "@setting.fos-menutheme.lumonispace" :
                i == 4 ? "@setting.fos-menutheme.randomplanet" :
                i == 5 ? "@setting.fos-menutheme.solarsystem" :
                i == 6 ? "@setting.fos-menutheme.caldemoltsystem" :
                i == 7 ? "@setting.fos-menutheme.lumoniterrain" :
                "@setting.fos-menutheme.default");
            t.checkPref("fos-rotatemenucamera", true);
            t.checkPref("fos-damagedisplay", true);
            t.checkPref("fos-ostdontshowagain", false);
            t.checkPref("fos-realisticmode", false);
            t.checkPref("fos-debugmode", false, b -> {
                if (b) {
                    settings.put("fos-debugmode", false);
                    ui.showConfirm("@warning", "@fos-dangerzone", () -> settings.put("fos-debugmode", true));
                }
            });
        });
    }

    private void addEditorTeams() {
        //thanks java.
        WidgetGroup teambuttons = (WidgetGroup) ui.editor.getChildren().get(0);
        teambuttons = (WidgetGroup) teambuttons.getChildren().get(0);
        teambuttons = (WidgetGroup) teambuttons.getChildren().get(0);

        ((Table) teambuttons).row();

        for(int i = 69; i <= 70; i++){
            Team team = Team.get(i);

            ImageButton button = new ImageButton(Tex.whiteui, Styles.clearNoneTogglei);
            button.margin(4f);
            button.getImageCell().grow();
            button.getStyle().imageUpColor = team.color;
            button.clicked(() -> editor.drawTeam = team);
            button.update(() -> button.setChecked(editor.drawTeam == team));

            ((Table) teambuttons).add(button);
        }
    }
}
