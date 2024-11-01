package fos.world.draw;

import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.draw.DrawLiquidRegion;

public class DrawRotLiquidRegion extends DrawLiquidRegion {
    public DrawRotLiquidRegion(Liquid drawLiquid) {
        this.drawLiquid = drawLiquid;
    }

    @Override
    public void draw(Building build) {
        Liquid drawn = drawLiquid != null ? drawLiquid : build.liquids.current();
        Drawf.liquid(liquid, build.x, build.y,
            build.liquids.get(drawn) / build.block.liquidCapacity * alpha,
            drawn.color,
            build.rotation * 90f
        );
    }
}
