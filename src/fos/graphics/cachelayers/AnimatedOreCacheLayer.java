package fos.graphics.cachelayers;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.gl.Shader;
import fos.core.FOSVars;
import fos.graphics.FOSOreRenderer;
import mindustry.Vars;
import mindustry.graphics.CacheLayer;

public class AnimatedOreCacheLayer extends CacheLayer.ShaderLayer {
    public AnimatedOreCacheLayer(Shader shader) {
        super(shader);
        CacheLayer.add(this);
        FOSOreRenderer.oreCacheLayers.add(this);
        id = FOSOreRenderer.oreCacheLayers.size-1;
    }

    @Override
    public void begin(){
        if(!Core.settings.getBool("fos-animatedore", true)) return;

        FOSVars.oreRenderer.endc();
        Vars.renderer.effectBuffer.begin();
        Core.graphics.clear(Color.clear);
        FOSVars.oreRenderer.beginc();
    }

    @Override
    public void end(){
        if(!Core.settings.getBool("fos-animatedore", true)) return;

        FOSVars.oreRenderer.endc();
        Vars.renderer.effectBuffer.end();

        Vars.renderer.effectBuffer.blit(shader);

        FOSVars.oreRenderer.beginc();
    }
}
