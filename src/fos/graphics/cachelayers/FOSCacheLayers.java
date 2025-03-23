package fos.graphics.cachelayers;

import fos.graphics.FOSShaders;
import mindustry.graphics.CacheLayer;

public class FOSCacheLayers {
    public static CacheLayer tokicite, calcite, calciteCrystals;

    public static void init() {
        CacheLayer.addLast(
            tokicite = new CacheLayer.ShaderLayer(FOSShaders.tokicite),
            calcite = new CacheLayer.ShaderLayer(FOSShaders.calcite),
            calciteCrystals = new CacheLayer.ShaderLayer(FOSShaders.calciteCrystals)
        );
    }
}
