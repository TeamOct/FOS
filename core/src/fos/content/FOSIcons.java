package fos.content;

import arc.Core;
import arc.graphics.g2d.TextureAtlas;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Reflect;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Icon;

public class FOSIcons {
    public static void load() {
        Log.info("[FOS][FOSIcons] Loading started");
        Time.mark();
        ObjectMap<String, TextureAtlas.AtlasRegion> drawables = Reflect.get(Core.atlas, "regionmap");
        Tmp.c4.b = 0;
        drawables.each((n, r) -> {
            int a = n.indexOf("-");
            int b = n.lastIndexOf("-");

            if ((a != -1 && b != -1) && n.substring(0, a).equals("fos") && n.substring(b+1).equals("icon")) {
                Icon.icons.put(n, Core.atlas.getDrawable(n));
                Tmp.c4.b += 1;
            }
        });
        Log.info("[FOS][FOSIcons] Loaded @ icons in @s", (int) Tmp.c4.b, Time.elapsed());
    }
}
