package fos.type.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawDiagonalPistons extends DrawBlock {
    public float sinMag = 4f, sinScl = 6f, sinOffset = 50f, sideOffset = 0f, lenOffset = -1f;
    public int sides = 4;
    public TextureRegion region1, region2, regiont;

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){

    }

    @Override
    public void draw(Building build){
        for(int i = 0; i < sides; i++){
            if (Math.floorMod(i, 2) == 0) continue;

            float len = Mathf.absin(build.totalProgress() + sinOffset + sideOffset * sinScl * i, sinScl, sinMag) + lenOffset;
            float angle = i * 360f / sides;
            TextureRegion reg =
                regiont.found() && (Mathf.equal(angle, 315) || Mathf.equal(angle, 135)) ? regiont :
                    angle >= 135 && angle < 315 ? region2 : region1;

            Draw.rect(reg, build.x + Angles.trnsx(angle, len), build.y + Angles.trnsy(angle, len), angle - 45f);

            Draw.yscl = 1f;
        }
    }

    @Override
    public void load(Block block){
        super.load(block);

        region1 = Core.atlas.find(block.name + "-piston0", block.name + "-piston");
        region2 = Core.atlas.find(block.name + "-piston1", block.name + "-piston");
        regiont = Core.atlas.find(block.name + "-piston-t");
    }
}
