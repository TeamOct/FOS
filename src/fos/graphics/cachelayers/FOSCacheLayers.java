package fos.graphics.cachelayers;

import fos.graphics.FOSShaders;
import mindustry.graphics.CacheLayer;

public class FOSCacheLayers {
    public static CacheLayer tokicite;

    public static void init() {
        CacheLayer.add(
            tokicite = new CacheLayer.ShaderLayer(FOSShaders.tokicite)
        );
    }
}
