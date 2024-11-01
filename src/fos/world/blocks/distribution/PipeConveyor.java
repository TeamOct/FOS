package fos.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.geom.Geometry;
import fos.graphics.ConveyorSpritesPacker;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.world.blocks.distribution.Conveyor;

import static mindustry.Vars.*;

public class PipeConveyor extends Conveyor {
    public TextureRegion[] pipeRegions;

    public PipeConveyor(String name) {
        super(name);
    }

    @Override
    public void load() {
        if (!headless) {
            String prefix = ConveyorSpritesPacker.getPrefix();
            ConveyorSpritesPacker.generateRegions(Core.atlas.find(prefix + "-" + name.replaceFirst("fos-", "")), prefix);
        }

        pipeRegions = new TextureRegion[5];
        for (int i = 0; i < pipeRegions.length; i++) {
            pipeRegions[i] = Core.atlas.find(name + "-top-" + i);
        }

        fullIcon = Core.atlas.find(name + "-full");
        uiIcon = Core.atlas.find(name + "-ui");

        super.load();
    }

    @SuppressWarnings("unused")
    public class PipeConveyorBuild extends ConveyorBuild {
        @Override
        public void draw() {
            super.draw();

            //draw extra conveyor pipes facing this one for non-square tiling purposes
            Draw.z(Layer.blockUnder + 0.1f);
            for(int i = 0; i < 4; i++){
                if((blending & (1 << i)) != 0){
                    int dir = rotation - i;
                    float rot = i == 0 ? rotation * 90 : (dir)*90;

                    Draw.rect(sliced(pipeRegions[0], i != 0 ? SliceMode.bottom : SliceMode.top), x + Geometry.d4x(dir) * tilesize*0.75f, y + Geometry.d4y(dir) * tilesize*0.75f, rot);
                }
            }

            Draw.z(Layer.block - 0.05f);
            Draw.rect(pipeRegions[blendbits], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);
        }

        @Override
        public void unitOn(Unit unit) {
            //no
        }
    }
}
