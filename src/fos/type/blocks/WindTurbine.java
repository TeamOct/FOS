package fos.type.blocks;

import arc.util.io.Reads;
import arc.util.io.Writes;
import fos.content.*;
import mindustry.world.blocks.power.*;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.*;

public class WindTurbine extends PowerGenerator {
    public float displayEfficiencyScale = 1f;
    public Attribute attr = FOSAttributes.windPower;

    public WindTurbine(String name) {
        super(name);
        drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-spinner"));
        noUpdateDisabled = true;
        buildType = WindTurbineBuild::new;
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(generationType);
        stats.add(generationType, powerProduction * 60.0f / displayEfficiencyScale, StatUnit.powerSecond);
    }

    public class WindTurbineBuild extends GeneratorBuild {
        @Override
        public void updateTile(){
            productionEfficiency = attr.env();
        }
    }
}
