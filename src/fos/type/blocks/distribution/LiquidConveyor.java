package fos.type.blocks.distribution;

import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.*;
import fos.controllers.CapsulesController;
import fos.core.FOSVars;
import fos.type.content.LiquidCapsule;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.distribution.Conveyor;

public class LiquidConveyor extends Conveyor {
    /** anuke's issue №1 **/
    private static final float itemSpace = Reflect.get(Conveyor.class, "itemSpace");
    private static final int capacity = Reflect.get(Conveyor.class, "capacity");

    public LiquidConveyor(String name) {
        super(name);
        hasLiquids = true;
        outputsLiquid = false;
        liquidCapacity = CapsulesController.liquidInCapsule * 2.05f;

        config(Boolean.class, (b, v) -> {
            if (v)
                b.noSleep();
            ((LiquidConveyorBuild) b).output = v;
        });

        configurable = true;
        allowConfigInventory = false;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void setBars() {
        super.setBars();
        //removeBar("liquid");
    }

    public class LiquidConveyorBuild extends ConveyorBuild {
        boolean output = false;

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return !output;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount) {
            super.handleLiquid(source, liquid, amount);
            noSleep();
        }

        @Override
        public void updateTile() {
            if (output) {
                // anuke's issue №2
                for (Item i : ids) {
                    if (i instanceof LiquidCapsule cap && liquidCapacity - liquids.get(cap.liquid) >=
                        CapsulesController.liquidInCapsule) {
                        removeStack(cap, 1);
                        liquids.add(cap.liquid, CapsulesController.liquidInCapsule);
                    }
                }

                Tmp.v5.x = 0;
                liquids.each((liquid, a) -> {
                    if (Tmp.v5.x == 0) {
                        dumpLiquid(liquid);
                        Tmp.v5.x = 1;
                    }
                });
            } else {
                Tmp.v5.x = 0;
                liquids.each((liquid, a) -> {
                    LiquidCapsule capsule = FOSVars.capsulesController.toCapsule(liquids.current());
                    if (Tmp.v5.x == 0 && a >= CapsulesController.liquidInCapsule && len < capacity) {
                        handleItem(this, capsule);
                        liquids.remove(liquid, CapsulesController.liquidInCapsule);
                        Tmp.v5.x = 1;
                    }
                });
            }

            // anuke's issue №3

            minitem = 1f;
            mid = 0;

            //skip updates if possible
            if(!output && (len == 0 && liquids.sum((l, a) -> a) < CapsulesController.liquidInCapsule)){
                clogHeat = 0f;
                sleep();
                return;
            }

            float nextMax = aligned ? 1f - Math.max(itemSpace - nextc.minitem, 0) : 1f;
            float moved = speed * edelta();

            for(int i = len - 1; i >= 0; i--){
                float nextpos = (i == len - 1 ? 100f : ys[i + 1]) - itemSpace;
                float maxmove = Mathf.clamp(nextpos - ys[i], 0, moved);

                ys[i] += maxmove;

                if(ys[i] > nextMax) ys[i] = nextMax;
                if(ys[i] > 0.5 && i > 0) mid = i - 1;
                xs[i] = Mathf.approach(xs[i], 0, moved*2);

                if(ys[i] >= 1f && pass(ids[i])){
                    //align X position if passing forwards
                    if(aligned){
                        nextc.xs[nextc.lastInserted] = xs[i];
                    }
                    //remove last item
                    items.remove(ids[i], len - i);
                    len = Math.min(i, len);
                }else if(ys[i] < minitem){
                    minitem = ys[i];
                }
            }

            if(minitem < itemSpace + (blendbits == 1 ? 0.3f : 0f)){
                clogHeat = Mathf.approachDelta(clogHeat, 1f, 1f / 60f);
            }else{
                clogHeat = 0f;
            }

            noSleep();
        }

        @Override
        public Object config() {
            return output;
        }

        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.liquid, () -> {
                configure(!output);
            });
        }
    }
}
