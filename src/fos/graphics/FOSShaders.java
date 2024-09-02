package fos.graphics;

import arc.Core;
import arc.graphics.Texture;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.util.Time;
import fos.core.FOSVars;
import mindustry.Vars;
import mindustry.graphics.Shaders;

import static arc.Core.*;
import static mindustry.Vars.renderer;

public class FOSShaders {
    public static LuminiumItemShader lis;
    public static LuminiumOreShader los;
    public static LuminiumTrailShader lts;

    public static ModSurfaceShader tokicite, calcite, calciteCrystals;

    public static void init() {
        lis = new LuminiumItemShader();
        los = new LuminiumOreShader();
        lts = new LuminiumTrailShader();

        tokicite = new ModSurfaceShader("tokicite");
        calcite = new ModSurfaceShader("calcite");
        calciteCrystals = new ModSurfaceShader("calcite-crystals");
    }

    public static class LuminiumItemShader extends Shader {
        public float time;

        public LuminiumItemShader() {
            super(FOSVars.internalTree.child("shaders/lis.vert"), FOSVars.internalTree.child("shaders/lis.frag"));
        }

        @Override
        public void apply() {
            super.apply();
            setUniformf("u_time", Time.globalTime / 60f % 3.14f);
        }
    }

    public static class LuminiumOreShader extends Shader {
        public float time;

        public LuminiumOreShader() {
            super(FOSVars.internalTree.child("shaders/los.vert"), FOSVars.internalTree.child("shaders/los.frag"));
        }

        @Override
        public void apply() {
            super.apply();
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);


            setUniformf("u_cameraScale", graphics.getWidth() / camera.width);
            setUniformf("u_time", Time.globalTime / 60f % 3.14f);
        }
    }

    public static class LuminiumTrailShader extends Shader {
        public LuminiumTrailShader() {
            super(FOSVars.internalTree.child("shaders/los.vert"), FOSVars.internalTree.child("shaders/luminium-trail.frag"));
        }

        @Override
        public void apply() {
            super.apply();

            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.globalTime / 60f % Mathf.PI);
        }
    }

    public static class ModSurfaceShader extends Shader {
        Texture noiseTex;

        public ModSurfaceShader(String frag) {
            super(Shaders.getShaderFi("screenspace.vert"), Vars.tree.get("shaders/" + frag + ".frag"));
            loadNoise();
        }

        public String textureName(){
            return "noise";
        }

        public void loadNoise(){
            Core.assets.load("sprites/" + textureName() + ".png", Texture.class).loaded = t -> {
                t.setFilter(Texture.TextureFilter.linear);
                t.setWrap(Texture.TextureWrap.repeat);
            };
        }

        @Override
        public void apply() {
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);

            if(hasUniform("u_noise")){
                if(noiseTex == null) {
                    noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
                }

                noiseTex.bind(1);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise", 1);
            }
        }
    }
}
