package fos.content;

import arc.Core;
import arc.files.Fi;
import fos.core.FOSVars;
import mindustry.gen.Icon;

public class FOSIcons {
    public static void load() {
        //please do not touch the line below. if it works, it works!
        Fi iconDir = FOSVars.mod.root.child("sprites").child("icons");

        for (var i : iconDir.list()) {
            var name = FOSVars.mod.meta.name + "-" + i.nameWithoutExtension();
            Icon.icons.put(name, Core.atlas.getDrawable(name));
        }
    }
}
