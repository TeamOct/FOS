package fos.ui.menus;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import mindustry.graphics.g3d.PlanetParams;

import static arc.Core.graphics;
import static fos.content.FOSVars.menuBuffer;
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

        renderer.planets.render(params);

        menuBuffer.end();

        Draw.rect(Draw.wrap(menuBuffer.getTexture()), (float) graphics.getWidth() / 2, (float) graphics.getHeight() / 2, graphics.getWidth(), graphics.getHeight());
    }
}
