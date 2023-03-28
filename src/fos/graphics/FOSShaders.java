package fos.graphics;

import arc.graphics.gl.Shader;
import arc.util.Log;
import arc.util.Time;
import fos.FOSVars;

public class FOSShaders {
    public static ShaderTextureRegionShader str;

    public static void init() {
        str = new ShaderTextureRegionShader();
    }

    public static class ShaderTextureRegionShader extends Shader {
        public float time;

        public ShaderTextureRegionShader() {
            super(FOSVars.internalTree.child("shaders/str.vert"), FOSVars.internalTree.child("shaders/str.frag"));
        }

        @Override
        public void apply() {
            super.apply();
            setUniformf("u_time", Time.globalTime / 60f % 3.14f);
        }
    }
}
