package fos.graphics.cachelayers;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.gl.Shader;
import arc.util.Log;
import mindustry.Vars;
import mindustry.graphics.CacheLayer;

public class AnimatedOreCacheLayer extends CacheLayer.ShaderLayer {
    public AnimatedOreCacheLayer(Shader shader) {
        super(shader);
        CacheLayer.add(this);
    }

    @Override
    public void begin(){
        if(!Core.settings.getBool("fos-animatedore", true)) return;

        Vars.renderer.blocks.floor.endc();
        Vars.renderer.effectBuffer.begin();
        Core.graphics.clear(Color.clear);
        Vars.renderer.blocks.floor.beginc();
    }

    @Override
    public void end(){
        if(!Core.settings.getBool("fos-animatedore", true)) return;

        Vars.renderer.blocks.floor.endc();
        Vars.renderer.effectBuffer.end();

        Vars.renderer.effectBuffer.blit(shader);

        Vars.renderer.blocks.floor.beginc();
    }
}
