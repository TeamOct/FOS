package fos.content;

import arc.Core;
import mindustry.gen.Icon;

public class FOSIcons {
    public static void load() {
        /* Used as the Uxerd asteroids icon in the planet selection menu. */
        Icon.icons.put("fos-asteroids", Core.atlas.getDrawable("fos-asteroids"));

        /* Used as the icon for the mod's settings section. */
        Icon.icons.put("fos-settings-icon", Core.atlas.getDrawable("fos-settings-icon"));
    }
}
