package fos.core;

import arc.*;
import arc.audio.Music;
import arc.backend.sdl.SdlApplication;
import arc.backend.sdl.jni.SDL;
import arc.graphics.*;
import arc.graphics.g2d.ScreenQuad;
import arc.graphics.gl.Shader;
import arc.input.*;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.*;
import fos.content.*;
import fos.controllers.CapsulesController;
import fos.gen.*;
import fos.graphics.*;
import fos.graphics.cachelayers.FOSCacheLayers;
import fos.net.FOSCall;
import fos.ui.UIHandler;
import fos.ui.menus.FOSMenus;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.mod.Mod;

import java.util.Calendar;

import static arc.Core.*;
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

        Events.on(FOSEventTypes.RealisticToggleEvent.class, e -> {
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

        SplashTexts.init();

        if (FOSVars.isAprilFools)
            Musics.menu = tree.loadMusic("mistake");

        //display the mod version
        FOSVars.mod.meta.description += "\n\n" + bundle.get("mod.currentversion") + "\n" + FOSVars.mod.meta.version;

        //load icons and menu themes
        FOSIcons.load();
        FOSMenus.load();

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

        // load this mod's settings
        constructSettings();

        // setup UI
        UIHandler.init();

        // setup Discord RPC
        if (!mobile) RichPresenceHandler.init();

        // add modded hints
        FOSVars.hints.load();

        // handle input for active abilities of certain weapons
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
            t.checkPref("fos-ostdontshowagain", false);
            t.checkPref("fos-realisticmode", false, b ->
                Events.fire(new FOSEventTypes.RealisticToggleEvent())
            );
            t.checkPref("fos-moddedrichpresence", true);
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
