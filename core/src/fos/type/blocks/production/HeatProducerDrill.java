package fos.type.blocks.production;

import arc.math.Mathf;
import arc.util.io.*;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.*;
import mindustry.world.meta.*;

public class HeatProducerDrill extends BoostableDrill {
    public float heatOutput = 4f;

    public HeatProducerDrill(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.output, heatOutput, StatUnit.heatUnits);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("heat", (HeatProducerDrillBuild build) -> new Bar("bar.heat", Pal.lightOrange, build::heatFrac));
    }

    @SuppressWarnings("unused")
    public class HeatProducerDrillBuild extends BoostableDrillBuild implements HeatBlock {
        public float heat;

        @Override
        public void updateTile() {
            super.updateTile();
            heat = lastDrillSpeed == 0 ? Mathf.approachDelta(heat, 0f, 0.3f * delta()) : Mathf.approachDelta(heat, heatOutput, 0.3f * delta());

            if (heat == heatOutput && !nearbyHeatConsumers()) damage(1);
        }

        @Override
        public float heatFrac() {
            return heat / heatOutput;
        }

        @Override
        public float heat() {
            return heat;
        }

        public boolean nearbyHeatConsumers() {
            return proximity.contains(b -> b instanceof HeatConsumer);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(heat);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            heat = read.f();
        }
    }
}
