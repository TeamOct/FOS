package fos.ui.menus;

import arc.math.geom.Vec3;
import mindustry.graphics.g3d.PlanetParams;

import static fos.content.FOSPlanets.*;

public class FOSMenus {
    public static MenuBackground uxerdSpace, luminaSpace;

    public static void load() {
        uxerdSpace = new SpaceMenuBackground(){{
            params = new PlanetParams(){{
                planet = uxerd;
                zoom = 0.8f;
                camPos = new Vec3(0f, 0f, 0.5f);
            }};
        }};
        luminaSpace = new SpaceMenuBackground(){{
            params = new PlanetParams(){{
                planet = lumina;
                zoom = 0.8f;
                camPos = new Vec3(0f, 0f, 0.5f);
            }};
        }};
    }
}
