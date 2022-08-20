package fos.type.blocks.production;

import arc.math.*;
import arc.util.io.*;
import fos.type.blocks.power.HeatGenerator;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

import static mindustry.Vars.indexer;

public class HeatProducerDrill extends BurstDrill {
    public float heatOutput = 0.5f;
    public float heatCapacity = 600f;

    public HeatProducerDrill(String name) {
        super(name);
        buildType = HeatProducerDrillBuild::new;
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

    public class HeatProducerDrillBuild extends BurstDrillBuild implements HeatBlock {
        public float heat;

        @Override
        public void updateTile() {
            super.updateTile();
            heat = Mathf.approachDelta(heat, heatOutput, 0.3f * delta());
            if (heat == heatOutput && indexer.findTile(team, x, y, 4f, b -> b instanceof HeatConsumer) == null) damage(1);
        }

        @Override
        public float heatFrac() {
            return heat / heatOutput;
        }

        @Override
        public float heat() {
            return heat;
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
