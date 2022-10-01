package fos.type.blocks.power;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.geom.Point2;
import fos.content.*;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

public class WindTurbine extends PowerGenerator {
    public float displayEfficiencyScale = 1f;
    public Attribute attr = FOSAttributes.windPower;
    public TextureRegion rotatorRegion;

    public WindTurbine(String name) {
        super(name);
        noUpdateDisabled = true;
        buildType = WindTurbineBuild::new;
    }

    @Override
    public void load() {
        super.load();
        rotatorRegion = Core.atlas.find(name + "-rotator");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        float a = 0;

        Point2[] edges = getEdges();
        for (Point2 edge : edges) {
            Building b = Vars.world.build(x + edge.x, y + edge.y);
            if (b != null && b.block.solid) {
                a += 1 / (size * 4f);
            }
        }

        drawPlaceText(Core.bundle.formatFloat("bar.efficiency", (attr.env() - a < 0 ? 0 : attr.env() - a) * 100, 1), x, y, valid);
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

            Point2[] edges = block.getEdges();
            for (Point2 edge : edges) {
                Building b = nearby(edge.x, edge.y);
                if (b != null && b.block.solid) {
                    productionEfficiency -= 1 / (size * 4f);
                }
            }
            if (productionEfficiency < 0f) productionEfficiency = 0f;

            rotatorAngle += productionEfficiency;
        }

        @Override
        public void draw() {
            super.draw();
            Draw.rect(rotatorRegion, x, y, rotatorAngle);
        }
    }
}
