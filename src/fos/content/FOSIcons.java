package fos.content;

import arc.Core;
import arc.scene.style.TextureRegionDrawable;
import mindustry.gen.Icon;

public class FOSIcons {
    public static TextureRegionDrawable asteroids, settings;

    public static void load() {
        asteroids = Core.atlas.getDrawable("fos-asteroids");
        Icon.icons.put("fos-asteroids", asteroids);

        settings = Core.atlas.getDrawable("fos-settings-icon");
        Icon.icons.put("fos-settings-icon", settings);
    }
}
