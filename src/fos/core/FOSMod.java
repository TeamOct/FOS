package fos.core;

import arc.*;
import arc.audio.Music;
import arc.backend.sdl.SdlApplication;
import arc.backend.sdl.jni.SDL;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.Shader;
import arc.input.*;
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
import fos.graphics.cachelayers.FOSCacheLayers;
import fos.net.FOSCall;
import fos.ui.menus.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.mod.Mod;
import mindustry.mod.Mods.LoadedMod;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.PlanetDialog;

import java.util.Calendar;

import static arc.Core.*;
import static arc.discord.DiscordRPC.*;
import static arc.input.GestureDetector.GestureListener;
import static mindustry.Vars.*;

public class FOSMod extends Mod {
    /** Only for debugging! */
    public FOSVars vars = new FOSVars();

    public FOSMod() {
        Log.debug("[FOS] main class construction");
        if (FOSVars.debug)
            Log.level = Log.LogLevel.debug;

        FOSCall.registerPackets();
        EntityMapping.nameMap.keys().toSeq().each(s -> {
            EntityMapping.nameMap.put("fos-" + s, EntityMapping.nameMap.get(s));
        });

        Events.on(EventType.ClientLoadEvent.class, e -> {
            clientLoaded();
        });

        Events.run(EventType.Trigger.newGame, () -> {
            // remove landing cutscene on Awakening
            if (state.rules.sector != null && state.rules.sector == FOSSectors.awakening.sector) {
                Reflect.set(renderer, "landTime", 0f);
                var core = FOSTeams.corru.core();
                Time.run(160f, () -> FOSFx.corruLogo.at(core.x, core.y));
            }
        });

        Events.run(EventType.Trigger.update, () -> {
            if (!mobile) {
                boolean useDiscord = !OS.hasProp("nodiscord");
                if (useDiscord) {
                    var planet = state.rules.planet;

                    if (planet != null && !planet.isVanilla() && planet.minfo.mod == FOSVars.mod) {
                        RichPresence presence = new RichPresence();

                        String gameMapWithWave, gameMode = "", gamePlayersSuffix = "";

                        gameMapWithWave = Strings.capitalize(Strings.stripColors(state.map.name()));

                        if(state.rules.waves){
                            gameMapWithWave += " | Wave " + state.wave;
                        }
                        gameMode = state.rules.pvp ? "PvP" : state.rules.attackMode ? "Attack" : state.rules.infiniteResources ? "Sandbox" : "Survival";
                        if(net.active() && Groups.player.size() > 1){
                            gamePlayersSuffix = " | " + Groups.player.size() + " Players";
                        }

                        presence.details = "[FOS] " + gameMapWithWave;
                        presence.state = gameMode + gamePlayersSuffix;

                        presence.largeImageKey = "logo";

                        try {
                            send(presence);
                        } catch (Exception ignored) {}
                    }
                }
            }

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
    public void loadContent() {
        EntityRegistry.register();

        Log.debug("[FOS] loading content");
        FOSVars.mod = mods.getMod(getClass());

        if (!headless) {
            ConveyorSpritesPacker.pack(); // generate conveyor regions

            FOSVars.oreRenderer = new FOSOreRenderer();
            FOSShaders.init();
            FOSCacheLayers.init();
        }

        SplashTexts.load();

        FOSAttributes.load();
        FOSStatuses.load();
        FOSItems.load();
        FOSFluids.load();
        FOSWeathers.load();
        FOSWeaponModules.load();
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

        //anything after this is client-side only.
        if (headless) return;

        //debug-only insect pathfinder test
        if (settings.getBool("fos-pathfinder-debug", false)) {
            Events.run(EventType.Trigger.draw, () -> {
                for (int i = 0; i < FOSVars.deathMapController.deathMap.length; i++) {
                    if (FOSVars.deathMapController.deathMap[i] == 0) continue;

                    Draw.z(Layer.light);
                    Draw.alpha(FOSVars.deathMapController.deathMap[i] / 100f);

                    Draw.rect("empty", world.tiles.geti(i).worldx(), world.tiles.geti(i).worldy());

                    Draw.reset();
                }
            });
        }

        //an anti-cheat system from long ago, is it really necessary now?
        LoadedMod xf = mods.list().find(m ->
                /* some mods don't even have the author field, apparently. how stupid. */ m.meta.author != null &&
                (m.meta.author.equals("XenoTale") || m.meta.author.equals("goldie")));
        if (xf != null) {
            ui.showOkText("@fos.errortitle", bundle.format("fos.errortext", xf.meta.displayName), () -> app.exit());
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
        ui.editor.shown(this::addEditorTeams);

        //add a new font page for... reasons
        //FIXME
/*
        Seq<Font> fonts = Seq.with(Fonts.def, Fonts.outline);
        fonts.each(f -> {
            var regions = f.getRegions();
            regions.add(new TextureRegion());
        });
*/

        //init modded teams
        FOSTeams.load();
    }

    public void clientLoaded() {
        boolean isAprilFools = FOSVars.date.get(Calendar.MONTH) == Calendar.APRIL && FOSVars.date.get(Calendar.DAY_OF_MONTH) == 1;

        if (isAprilFools || settings.getBool("haha-funny", false)) {
            Musics.menu = tree.loadMusic("mistake");

            if (!mobile) {
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
            ui.showOkText("@fos.earlyaccesstitle", bundle.get("fos.earlyaccess"), () -> {});

        //unsupported on bleeding-edge notice
        if (becontrol.active())
            ui.showOkText("@fos.beunsupportedtitle", bundle.get("fos.beunsupported"), () -> {});

        //unlock every planet if debug
        if (FOSVars.debug)
            PlanetDialog.debugSelect = true;

        //check for "Fictional Octo System OST" mod. if it doesn't exist, prompt to download from GitHub
        LoadedMod ost = mods.getMod("fosost");
        if (ost == null) {
            if (!settings.getBool("fos-ostdontshowagain")) {
                ui.showCustomConfirm("@fos.noosttitle", bundle.get("fos.noost"),
                    "@mods.browser.add", "@no",
                    () -> {
                        ui.mods.githubImportMod("TeamOct/FOS-OST", true);
                    },
                    () -> {});
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
                tn == 2 ? FOSMenus.uxerdSpace :
                tn == 3 ? FOSMenus.lumoniSpace :
                tn == 4 ? FOSMenus.random :
                tn == 5 ? FOSMenus.solarSystem :
                tn == 6 ? FOSMenus.caldemoltSystem :
                tn == 7 ? FOSMenus.lumoniTerrain : null);
        if (bg != null) {
            FOSVars.menuRenderer.changeBackground(bg);
        }

        // add modded hints
        FOSVars.hints.load();

        input.addProcessor(new GestureDetector(new GestureListener() {
            @Override
            public boolean tap(float x, float y, int count, KeyCode button) {
                if (((mobile && count == 2) || button == KeyCode.mouseMiddle) && !(
                    state.isMenu() ||
                    scene.hasMouse() ||
                    control.input.isPlacing() ||
                    control.input.isBreaking() ||
                    control.input.selectedUnit() != null
                ) && player.unit() instanceof LumoniPlayerc) {
                    FOSCall.detonate();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean touchDown(float x, float y, int pointer, KeyCode button) {
                return GestureListener.super.touchDown(x, y, pointer, button);
            }
        }));
    }

    public void constructSettings() {
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
            t.checkPref("fos-animatedore", true);
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

    public void addEditorTeams() {
        //java sucks
        WidgetGroup teambuttons = (WidgetGroup) ui.editor.getChildren().get(0);
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
