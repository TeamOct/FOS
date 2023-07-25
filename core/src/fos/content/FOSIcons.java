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

        //should return the sprites/icons directory.
        Fi iconDir = FOSVars.thisMod.root.child("sprites").child("icons");

        for (var i : iconDir.list()) {
            var name = "fos-" + i.nameWithoutExtension();
            Icon.icons.put(name, Core.atlas.getDrawable(name));

            Log.info("[FOS] Loaded icon: " + name);
        }

        Log.info("[FOS] Icons loaded in @s", Time.elapsed());
    }
}
