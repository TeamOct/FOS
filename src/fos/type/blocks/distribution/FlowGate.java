package fos.type.blocks.distribution;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.OverflowGate;

public class FlowGate extends OverflowGate {
    public TextureRegion inverseRegion;
    public Sound clickSound = Sounds.click;

    public FlowGate(String name) {
        super(name);
        configurable = true;
        update = true;

        config(Boolean.class, (FlowGateBuild entity, Boolean b) -> entity.inverse = b);
    }

    @Override
    public void load() {
        super.load();
        inverseRegion = Core.atlas.find(name + "-inverse");
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("items");
    }

    public class FlowGateBuild extends OverflowGateBuild {
        public boolean inverse = false;

        @Override
        public void draw() {
            Draw.rect(inverse ? inverseRegion : region, x, y);
        }

        @Override
        public boolean configTapped() {
            configure(!inverse);
            clickSound.at(this);
            return false;
        }

        public @Nullable Building getTileTarget(Item item, Building src, boolean flip){
            int from = relativeToEdge(src.tile);
            if(from == -1) return null;
            Building to = nearby((from + 2) % 4);
            boolean
                fromInst = src.block.instantTransfer,
                canForward = to != null && to.team == team && !(fromInst && to.block.instantTransfer) && to.acceptItem(this, item),
                inv = inverse;

            if(!canForward || inv){
                Building a = nearby(Mathf.mod(from - 1, 4));
                Building b = nearby(Mathf.mod(from + 1, 4));
                boolean ac = a != null && !(fromInst && a.block.instantTransfer) && a.team == team && a.acceptItem(this, item);
                boolean bc = b != null && !(fromInst && b.block.instantTransfer) && b.team == team && b.acceptItem(this, item);

                if(!ac && !bc){
                    return inv && canForward ? to : null;
                }

                if(ac && !bc){
                    to = a;
                }else if(bc && !ac){
                    to = b;
                }else{
                    to = (rotation & (1 << from)) == 0 ? a : b;
                    if(flip) rotation ^= (1 << from);
                }
            }

            return to;
        }

        @Override
        public void write(Writes write) {
            write.bool(inverse);
        }

        @Override
        public void read(Reads read, byte revision) {
            inverse = read.bool();
        }
    }
}
