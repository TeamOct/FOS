package fos.type.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Time;
import fos.content.FOSAttributes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.meta.*;

public class WindTurbine extends PowerGenerator {
    public float displayEfficiencyScale = 1f;
    public float rotateSpeed = 1f;
    public Attribute attr = FOSAttributes.windPower;

    public WindTurbine(String name) {
        super(name);
        noUpdateDisabled = true;
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
                Draw.z(Layer.blockOver);
                Drawf.square((x + edge.x) * 8, (y + edge.y) * 8, 4f, Mathf.PI / 4, Color.valueOf("ff0000"));
            }
        }

        drawPlaceText(Core.bundle.formatFloat("bar.efficiency", (attr.env() - a < 0 ? 0 : attr.env() - a) * 100, 1), x, y, valid);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(generationType);
        stats.add(generationType, powerProduction * 60.0f / displayEfficiencyScale, StatUnit.powerSecond);
    }

    @SuppressWarnings("unused")
    public class WindTurbineBuild extends GeneratorBuild {
        public float totalProgress;

        @Override
        public float totalProgress() {
            return totalProgress;
        }

        @Override
        public void updateTile() {
            productionEfficiency = Mathf.lerpDelta(productionEfficiency, attr.env(), 0.01f);

            Point2[] edges = block.getEdges();
            for (Point2 edge : edges) {
                Building b = nearby(edge.x, edge.y);
                if (b != null && b.block.solid) {
                    productionEfficiency -= attr.env() / (size * 4f);
                }
            }
            if (productionEfficiency < 0f) productionEfficiency = 0f;

            totalProgress += Time.delta * productionEfficiency;
        }
    }
}
