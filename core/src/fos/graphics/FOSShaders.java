package fos.graphics;

import arc.graphics.gl.Shader;
import arc.util.Time;
import fos.core.FOSVars;

public class FOSShaders {
    public static LuminiumItemShader lis;
    public static LuminiumOreShader los;

    public static void init() {
        lis = new LuminiumItemShader();
        los = new LuminiumOreShader();
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

    public static class LuminiumOreShader extends Shader{
        public float time;

        public LuminiumOreShader() {
            super(FOSVars.internalTree.child("shaders/los.vert"), FOSVars.internalTree.child("shaders/los.frag"));
        }

        @Override
        public void apply() {
            super.apply();
            setUniformf("u_time", Time.globalTime / 60f % 3.14f);
        }
    }
}
