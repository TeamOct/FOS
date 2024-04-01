package fos.core;

import arc.*;
import arc.audio.Music;
import arc.backend.sdl.SdlApplication;
import arc.backend.sdl.jni.SDL;
import arc.func.Prov;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.scene.*;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.*;
import arc.struct.Seq;
import arc.util.*;
import fos.content.*;
import fos.controllers.CapsulesController;
import fos.gen.*;
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

import java.util.Calendar;

import static arc.Core.*;
import static mindustry.Vars.*;

@ModAnnotations.RootDirectoryPath(rootDirectoryPath = "")
@ModAnnotations.AnnotationSettings(
        rootPackage = "fos",
        modInfoPath = "res/mod.json",
        classPrefix = "FOS",
        revisionsPath = "revisions"
)
public class FOSMod extends Mod {
    public FOSMod() {
        Log.debug("[FOS] main class construction");
        if (FOSVars.debug)
            Log.level = Log.LogLevel.debug;

        FOSPackets.register();
        FOSCall.registerPackets();
        FOSEntityMapping.init();
        EntityMapping.nameMap.keys().toSeq().each(s -> {
            EntityMapping.nameMap.put("fos-" + s, EntityMapping.nameMap.get(s));
        });

        Events.on(EventType.ClientLoadEvent.class, e -> {
            clientLoaded();
        });

        Events.run(EventType.Trigger.update, () -> {
            /* not sure if it will ever be useful now?
            if (!mobile) {
                boolean useDiscord = !OS.hasProp("nodiscord");
                if (useDiscord) {
                    if (!state.isCampaign()) return;
                    if (state.rules.sector.planet == FOSPlanets.uxerd) {
                        RichPresence presence = new RichPresence();

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
                audio.soundBus.setVolume(0f);
            } else {
                audio.soundBus.setVolume(settings.getInt("sfxvol") / 100f);
            }
        });
    }

    @Override
    public void loadContent() {
        Log.debug("[FOS] loading content");
        FOSVars.mod = Vars.mods.getMod(getClass());

        if (!headless) {
            ConveyorSpritesPacker.pack(); // generate conveyor regions

            FOSVars.oreRenderer = new FOSOreRenderer();

            FOSShaders.init();
        }

        SplashTexts.load();

        FOSCommands.init();

        FOSAttributes.load();
        FOSWeathers.load();
        FOSItems.load();
        FOSFluids.load();
        FOSWeaponModules.load();
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
        if (headless) return;

        //an anti-cheat system from long ago, is it really necessary now?
        LoadedMod xf = Vars.mods.list().find(m ->
                /* some mods don't even have the author field, apparently. how stupid. */ m.meta.author != null &&
                (m.meta.author.equals("XenoTale") || m.meta.author.equals("goldie")));
        if (xf != null) {
            Vars.ui.showOkText("@fos.errortitle", bundle.format("fos.errortext", xf.meta.displayName), () -> app.exit());
        }

        SplashTexts.init();

        if (FOSVars.isAprilFools)
            Musics.menu = tree.loadMusic("mistake");

        //display the mod version
        FOSVars.mod.meta.description += "\n\n" + bundle.get("mod.currentversion") + "\n" + FOSVars.mod.meta.version;

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
        boolean isAprilFools = FOSVars.date.get(Calendar.MONTH) == Calendar.APRIL && FOSVars.date.get(Calendar.DAY_OF_MONTH) == 1;

        if (isAprilFools || settings.getBool("haha-funny", false)) {
            Musics.menu = tree.loadMusic("mistake");

            if (!Vars.mobile) {
                Events.on(EventType.BlockBuildEndEvent.class, e -> {
                    if (Mathf.chance(0.005f))
                        superSecretThings();
                });

                Events.on(EventType.UnitSpawnEvent.class, e -> {
                    if (Mathf.chance(0.01f))
                        superSecretThings();
                });
            }
        }

        //load this mod's settings
        constructSettings();

        //disclaimer for non-debug
        if (FOSVars.earlyAccess && !FOSVars.debug)
            Vars.ui.showOkText("@fos.earlyaccesstitle", bundle.get("fos.earlyaccess"), () -> {});

        //unlock every planet if debug
        if (FOSVars.debug)
            PlanetDialog.debugSelect = true;

        //check for "Fictional Octo System OST" mod. if it doesn't exist, prompt to download from GitHub
        LoadedMod ost = Vars.mods.getMod("fosost");
        if (ost == null) {
            if (!settings.getBool("fos-ostdontshowagain")) {
                Vars.ui.showCustomConfirm("@fos.noosttitle", bundle.get("fos.noost"),
                        "@mods.browser.add", "@no",
                        () -> Vars.ui.mods.githubImportMod("TeamOct/FOS-OST", true), () -> {});
            }
        } else if (!ost.enabled()) {
            Vars.ui.showCustomConfirm("@fos.ostdisabledtitle", bundle.get("fos.ostdisabled"),
                    "@yes", "@no",
                    () -> {
                        Vars.mods.setEnabled(ost, true);
                        Vars.ui.showInfoOnHidden("@mods.reloadexit", () -> app.exit());
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
                bundle.format("setting.seconds", s / 60f));
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

    void superSecretThings() {
        if (mobile) return;

        Log.debug("april fool");

        Seq<ApplicationListener> listeners = Reflect.invoke(app, "getListeners");
        listeners.each(ApplicationListener::dispose);
        listeners.clear();
        Log.debug("listeners cleared");

        input.getInputMultiplexer().clear();
        Log.debug("input cleared");

        graphics.setFullscreen();
        graphics.setBorderless(true);
        graphics.setResizable(false);
        Log.debug("window prepared");

        Music mistake = tree.loadMusic("mistake");
        mistake.setVolume(1f);
        mistake.setLooping(true);
        mistake.play();
        Log.debug("music started");

        SDL.SDL_SetCursor(SDL.SDL_CreateColorCursor(SDL.SDL_CreateRGBSurfaceFrom(
            new Pixmap(FOSVars.internalTree.child("alpha.png")).getPixels(), 32, 32), 0, 0));
        Log.debug("cursor created");

        app.addListener(new ApplicationListener() {
            {
                Log.debug("listener created");
            }
            final Texture texture = new Texture(FOSVars.internalTree.child("pain.png"));
            final Shader shader = new Shader(
                """
                attribute vec4 a_position;
                attribute vec2 a_texCoord0;
                varying vec2 v_texCoords;
                void main(){
                   v_texCoords = a_texCoord0;
                   v_texCoords.y = 1.0 - v_texCoords.y;
                   gl_Position = a_position;
                }
                """,
                """
                    uniform sampler2D u_texture;
                    varying vec2 v_texCoords;
                    void main(){
                      gl_FragColor = texture2D(u_texture, v_texCoords);
                    }
                    """
            );
            final ScreenQuad quad = new ScreenQuad();

            @Override
            public void update() {
                texture.bind();
                shader.bind();
                quad.render(shader);
                SDL.SDL_RestoreWindow(Reflect.get(SdlApplication.class, app, "window"));
                Reflect.set(SdlApplication.class, app, "running", true);
            }
        });
    }
}
