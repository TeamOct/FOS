package fos.core;

import arc.ApplicationListener;
import arc.Core;
import arc.audio.Music;
import arc.backend.sdl.SdlApplication;
import arc.backend.sdl.jni.SDL;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.ScreenQuad;
import arc.graphics.gl.Shader;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Reflect;

public class FOSModDesktop extends FOSMod{
    @Override
    public void init() {
        if (FOSVars.isAprilFools || Core.settings.getBool("fos-haha-funny", false)) {

            Seq<ApplicationListener> listeners = Reflect.invoke(Core.app, "getListeners");
            listeners.clear();

            Core.input.getInputMultiplexer().clear();

            Core.graphics.setFullscreen();
            Core.graphics.setBorderless(true);
            Core.graphics.setResizable(false);

            Music mistake = Core.audio.newMusic(FOSVars.internalTree.child("music/mistake.mp3"));
            mistake.setVolume(1f);
            mistake.setLooping(true);
            mistake.play();

            SDL.SDL_SetCursor(SDL.SDL_CreateColorCursor(SDL.SDL_CreateRGBSurfaceFrom(
                    new Pixmap(FOSVars.internalTree.child("alpha.png")).getPixels(), 32, 32), 0, 0));

            Core.app.addListener(new ApplicationListener() {
                Texture texture = new Texture(FOSVars.internalTree.child("pain.png"));
                Shader shader = new Shader(
                        """
                            attribute vec4 a_position;
                            attribute vec2 a_texCoord0;
                            varying vec2 v_texCoords;
                            void main(){
                               a_texCoord0.y = 1.0 - a_texCoord0.y;
                               v_texCoords = a_texCoord0;
                               gl_Position = a_position;
                            }""",
                        """
                            uniform sampler2D u_texture;
                            varying vec2 v_texCoords;
                            void main(){
                              gl_FragColor = texture2D(u_texture, v_texCoords);
                            }"""
                );
                ScreenQuad quad = new ScreenQuad();

                @Override
                public void update() {
                    texture.bind();
                    shader.bind();
                    quad.render(shader);
                    // :trollface:
                    SDL.SDL_RestoreWindow(Reflect.get(SdlApplication.class, Core.app, "window"));
                    Reflect.set(SdlApplication.class, Core.app, "running", true);
                }
            });
        }
        super.init();
    }
}
