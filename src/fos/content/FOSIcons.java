package fos.content;

import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import arc.util.Time;
import fos.core.FOSVars;
import mindustry.gen.Icon;

public class FOSIcons {
    public static void load() {
        //please do not touch the line below. if it works, it works!
        Fi iconDir = FOSVars.mod.root.child("sprites").child("icons");

        Time.mark();
        Log.debug("[FOS][FOSIcons] loading icons");
        int counter = 0;
        for (var i : iconDir.findAll(f -> f.extension().equals("png"))) {
            var name = FOSVars.mod.meta.name + "-" + i.nameWithoutExtension();
            Icon.icons.put(name, Core.atlas.getDrawable(name));
            counter++;
        }
        Log.debug("[FOS][FOSIcons] loaded @ icons in @s", counter, Time.elapsed());
    }
}
