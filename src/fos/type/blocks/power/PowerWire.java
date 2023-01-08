package fos.type.blocks.power;

import arc.graphics.g2d.Draw;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;

public class PowerWire extends PowerNode {
    public PowerWire(String name) {
        super(name);
        conductivePower = true;
        laserRange = 0f;
        maxNodes = 0;
        conveyorPlacement = true;
        solid = false;
        underBullets = true;
        configurable = false;
        enableDrawStatus = false;
        group = BlockGroup.power;
        buildType = PowerWireBuild::new;
    }

    @Override
    public void setStats() {
        super.setStats();

        //not needed for a wire
        stats.remove(Stat.powerRange);
        stats.remove(Stat.powerConnections);
    }

    @Override
    public void setBars() {
        super.setBars();

        //not needed for a wire
        removeBar("connections");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);
    }

    @Override
    public void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2) {
        //it is a freaking wire, no lasers!
    }

    public class PowerWireBuild extends PowerNodeBuild {
        @Override
        public void draw() {
            //TODO when sprites are done, make a conveyor-like draw method
            Draw.rect(region, x, y, drawrot());
        }

        @Override
        public void drawSelect() {

        }
    }
}
