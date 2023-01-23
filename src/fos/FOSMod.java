package fos;

import arc.*;
import arc.discord.DiscordRPC;
import arc.discord.DiscordRPC.RichPresence;
import arc.func.Prov;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import fos.content.*;
import fos.type.blocks.campaign.OrbitalAccelerator.*;
import fos.ui.menus.*;
import mindustry.ai.Pathfinder;
import mindustry.content.SectorPresets;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.ui.*;
import mindustry.ui.fragments.MenuFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static arc.Core.*;
import static arc.graphics.g2d.Font.*;
import static fos.ui.menus.FOSMenus.*;
import static mindustry.Vars.*;
import static mindustry.game.EventType.*;

public class FOSMod extends Mod {
    private boolean editorChanged = false;

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

            ui.showOkText("@fos.earlyaccesstitle", bundle.get("fos.earlyaccess"), () -> {});

            LoadedMod ost = mods.locateMod("fosost");
            if (ost == null) ui.showCustomConfirm("@fos.noosttitle", bundle.get("fos.noost"), "@mods.browser.add", "@no", () -> {}, () -> {});

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

            //loadTeamIcons();
        });

        Events.run(Trigger.update, () -> {
            if (!mobile) {
                boolean useDiscord = !OS.hasProp("nodiscord");
                if (useDiscord) {
                    RichPresence presence = new RichPresence();
                    if (!state.isCampaign()) return;
                    if (state.rules.sector.planet == FOSPlanets.uxerd) {
                        Building a = indexer.findTile(Team.sharded, 350 * 8, 350 * 8, 4000, b -> b instanceof OrbitalAcceleratorBuild);

                        presence.state = "Orbital Accelerator Progress: " + (a != null ? Mathf.round((float) a.items().total() / (float) a.block().itemCapacity * 100) : "0") + "%";
                        presence.details = "Uxerd (FOS)";

                        presence.largeImageKey = "logo";

                        try {
                            DiscordRPC.send(presence);
                        } catch (Exception ignored) {}
                    }
                }
            }

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
        if (headless) return;

        LoadedMod mod = mods.locateMod("fos");

        SplashTexts.load(13);
        int n = Mathf.floor((float) Math.random() * SplashTexts.splashes.size);
        Calendar c = new GregorianCalendar();
        boolean isNewYear = c.get(Calendar.MONTH) == Calendar.JANUARY && c.get(Calendar.DAY_OF_MONTH) == 1;
        mod.meta.subtitle =
            isNewYear ? bundle.get("splashnewyear")
            : SplashTexts.splashes.get(n);
        boolean isAprilFools = c.get(Calendar.MONTH) == Calendar.APRIL && c.get(Calendar.DAY_OF_MONTH) == 1;
        if (isAprilFools) Musics.menu = tree.loadMusic("mistake");

        mod.meta.description += "\n\n" + bundle.get("mod.currentversion") + "\n" + mod.meta.version;

        FOSIcons.load();
        FOSMenus.load();
        FOSVars.load();

        ui.editor.shown(() -> {
            if (!editorChanged) {
                addEditorTeams();
                editorChanged = true;
            }
        });

        LoadedMod xf = mods.list().find(m ->
            /* some mods don't even have the author field apparently */ m.meta.author != null &&
            (m.meta.author.equals("XenoTale") || m.meta.author.equals("goldie")));
        if (xf != null) {
            ui.showOkText("@fos.errortitle", bundle.format("fos.errortext", xf.meta.displayName), () -> app.exit());
        }

        //required for modded AIs
        Pathfinder.Flowfield pt = FOSVars.fpos;
        Reflect.<Seq<Prov<Pathfinder.Flowfield>>>get(pathfinder, "fieldTypes").add(() -> pt);
        Events.on(WorldLoadEvent.class, e -> {
            if (!net.client()) {
                Reflect.invoke(pathfinder, "preloadPath", new Object[]{pt}, Pathfinder.Flowfield.class);
            }
        });

    }

    @Override
    public void loadContent() {
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
            t.checkPref("fos-realisticmode", false);
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

    private void loadTeamIcons() {
        // FIXME idk how to do this
        // Original code from Dusted Lands
        // Author: @KayyAyeAre
        final int[] id = {63001};

        Seq<Team> teams = Seq.with(
            FOSTeam.corru, FOSTeam.bessin
        );

        //actually start loading the emojis
        Seq<Font> fonts = Seq.with(Fonts.def, Fonts.outline);

        teams.each(t -> {
            TextureRegion region = atlas.find("fos-team-" + t.name);

            Reflect.<ObjectIntMap<String>>get(Fonts.class, "unicodeIcons").put(t.name, id[0]);
            Reflect.<ObjectMap<String, String>>get(Fonts.class, "stringIcons").put(t.name, ((char) id[0]) + "");

            int size = (int) (Fonts.def.getData().lineHeight / Fonts.def.getData().scaleY);

            Vec2 out = Scaling.fit.apply(region.width, region.height, size, size);

            Glyph glyph = new Glyph();
            glyph.id = id[0];
            glyph.srcX = 0;
            glyph.srcY = 0;
            glyph.width = (int) out.x;
            glyph.height = (int) out.y;
            glyph.u = region.u;
            glyph.v = region.v2;
            glyph.u2 = region.u2;
            glyph.v2 = region.v;
            glyph.xoffset = 0;
            glyph.yoffset = -size;
            glyph.xadvance = size;
            glyph.kerning = null;
            glyph.fixedWidth = true;
            glyph.page = 0;
            fonts.each(f -> f.getData().setGlyph(id[0], glyph));

            t.emoji = Reflect.<ObjectMap<String, String>>get(Fonts.class, "stringIcons").get(t.name);

            id[0]--;
        });
    }
}
