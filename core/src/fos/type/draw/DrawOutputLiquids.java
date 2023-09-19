package fos.type.draw;

import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawLiquidOutputs;

public class DrawOutputLiquids extends DrawLiquidOutputs {
    @Override
    public void draw(Building build) {
        GenericCrafter crafter = (GenericCrafter)build.block;
        if(crafter.outputLiquids == null) return;

        for(int i = 0; i < crafter.outputLiquids.length; i++){
            int side = i < crafter.liquidOutputDirections.length ? crafter.liquidOutputDirections[i] : -1;
            if(side != -1){
                float amount = build.liquids().get(crafter.outputLiquids[i].liquid);
                int realRot = (side + build.rotation) % 4;
                Draw.alpha(amount / crafter.liquidCapacity);
                Draw.rect(liquidOutputRegions[realRot > 1 ? 1 : 0][i], build.x, build.y, realRot * 90);
            }
        }
    }
}
