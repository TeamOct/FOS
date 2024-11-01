package fos.world.blocks.power;

import arc.*;
import arc.graphics.*;
import mindustry.graphics.*;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class HeatGenerator extends PowerGenerator {
    public float heatInput = 14f;

    public HeatGenerator(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.input, heatInput, StatUnit.heatUnits);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("heat", (HeatGeneratorBuild build) -> new Bar(Core.bundle.format("bar.heat"), Pal.lightOrange, () -> build.productionEfficiency));
    }

    @SuppressWarnings("unused")
    public class HeatGeneratorBuild extends GeneratorBuild implements HeatConsumer {
        public float heat;

        @Override
        public void draw() {
            super.draw();
            Drawf.additive(Core.atlas.find(name + "-heat"), new Color(1f, 0.22f, 0.22f, heat / heatRequirement() * 0.8f), x, y, 0f, Layer.turretHeat);
        }

        @Override
        public void updateTile() {
            heat = calculateHeat(sideHeat());
            if (heat > heatRequirement()) heat = heatRequirement();
            productionEfficiency = heat / heatRequirement();
        }

        @Override
        public float[] sideHeat() {
            return new float[4];
        }

        @Override
        public float heatRequirement() {
            return heatInput;
        }
    }
}
