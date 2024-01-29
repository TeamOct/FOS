package fos.type.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Time;
import fos.content.FOSAttributes;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.graphics.*;
import mindustry.world.Tile;
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
            Tile t = Vars.world.tile(x + edge.x, y + edge.y);
            if (t != null && t.solid()) {
                a += 1 / (size * 2f);
                Draw.z(Layer.blockOver);
                Drawf.square((x + edge.x) * 8, (y + edge.y) * 8, 4f, Mathf.degRad * 45, Color.valueOf("ff0000"));
            }
        }

        drawPlaceText(Core.bundle.formatFloat("bar.efficiency", Mathf.round((attr.env() - a < 0 ? 0 : attr.env() - a) * 100), 0), x, y, valid);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        float a = 0;

        Point2[] edges = getEdges();
        for (Point2 edge : edges) {
            Tile t = Vars.world.tile(tile.x + edge.x, tile.y + edge.y);
            if (t != null && t.solid()) {
                a += 1 / (size * 2f);
            }
        }

        return a < 1;
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

            int no = 0;
            Point2[] edges = block.getEdges();
            for (Point2 edge : edges) {
                Tile t = Vars.world.tile(this.tileX() + edge.x, this.tileY() + edge.y);
                if (t != null && t.solid()) {
                    no++;
                }
            }

            productionEfficiency = Math.min(productionEfficiency, 1f - (1 / (size * 2f)) * no);
            productionEfficiency = Math.max(0, productionEfficiency);

            totalProgress += Time.delta * productionEfficiency;
        }
    }
}
