package fos.ui.menus;

import arc.Core;
import arc.math.geom.Vec3;
import mindustry.graphics.g3d.PlanetParams;

import static mindustry.Vars.renderer;

public class SpaceMenuBackground extends MenuBackground {
    public PlanetParams params;

    @Override
    public void render() {
        params.alwaysDrawAtmosphere = true;
        params.drawUi = false;

        if (Core.settings.getBool("fos-rotatemenucamera")){
            params.camPos.rotate(Vec3.Y, 0.1f);
        }

        renderer.planets.render(params);
    }
}
