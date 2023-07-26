package fos.content;

import arc.Core;
import arc.files.Fi;
import arc.util.*;
import fos.core.FOSVars;
import mindustry.gen.Icon;

public class FOSIcons {
    public static void load() {
        Log.info("[FOS] Started loading icons.");
        Time.mark();

        Fi iconDir = FOSVars.mod.root.child("sprites/icons");

        for (var i : iconDir.list()) {
            var name = FOSVars.mod.meta.name + i.nameWithoutExtension();
            Icon.icons.put(name, Core.atlas.getDrawable(name));

            Log.debug("[FOS] Loaded icon: " + name);
        }

        Log.info("[FOS] Icons loaded in @s", Time.elapsed());
    }
}
