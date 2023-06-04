package fos.ui.menus;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.math.geom.Vec3;
import mindustry.graphics.g3d.PlanetParams;

import static arc.Core.graphics;
import static fos.core.FOSVars.menuBuffer;
import static fos.core.FOSVars.menuParams;
import static mindustry.Vars.renderer;

public class SpaceMenuBackground extends MenuBackground {
    public PlanetParams params;

    @Override
    public void render() {
        int size = Math.max(graphics.getWidth(), graphics.getHeight());

        if(menuBuffer == null){
            menuBuffer = new FrameBuffer(size, size);
        }

        menuBuffer.begin(Color.clear);

        params.alwaysDrawAtmosphere = true;
        params.drawUi = false;

        if (menuParams == null){
            menuParams = params;
        }

        if (Core.settings.getBool("fos-rotatemenucamera")){
            menuParams.camPos.rotate(Vec3.Y, 0.1f);
        }

        renderer.planets.render(menuParams);

        menuBuffer.end();

        Draw.rect(Draw.wrap(menuBuffer.getTexture()), (float) graphics.getWidth() / 2, (float) graphics.getHeight() / 2, graphics.getWidth(), graphics.getHeight());
    }
}
