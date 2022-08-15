package fos.type.blocks.power;

import arc.Core;
import arc.graphics.g2d.*;
import fos.content.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

public class WindTurbine extends PowerGenerator {
    public float displayEfficiencyScale = 1f;
    public Attribute attr = FOSAttributes.windPower;
    public TextureRegion rotatorRegion = Core.atlas.find(this.name + "-rotator");

    public WindTurbine(String name) {
        super(name);
        noUpdateDisabled = true;
        buildType = WindTurbineBuild::new;
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{
            this.region, this.rotatorRegion
        };
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(generationType);
        stats.add(generationType, powerProduction * 60.0f / displayEfficiencyScale, StatUnit.powerSecond);
    }

    public class WindTurbineBuild extends GeneratorBuild {
        public float rotatorAngle = 0f;

        @Override
        public void updateTile(){
            productionEfficiency = attr.env();
            rotatorAngle += productionEfficiency;
        }

        @Override
        public void draw() {
            super.draw();
            Draw.rect(rotatorRegion, x, y, rotatorAngle);
        }
    }
}
