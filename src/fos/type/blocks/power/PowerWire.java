package fos.type.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PowerWire extends PowerNode {
    public TextureRegion[] regions = new TextureRegion[16];

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
        hasShadow = false;
        swapDiagonalPlacement = false;
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
    public void load() {
        super.load();

        for (int i = 0; i < regions.length; i++) {
            regions[i] = Core.atlas.find(name + "-" + i);
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        if(plan.tile() == null) return;

        byte[] b = {0, 0};

        list.each(other -> {
            if (other.breaking || other == plan || !other.block.hasPower || other.samePos(plan)) return;
            for (int i = 0; i < 4; i++) {
                Point2 p = Geometry.d4[i];

                if (other.x == plan.x + p.x && other.y == plan.y + p.y && (b[1] & 1 << 3-i) == 0) {
                    b[0] += 1 << 3 - i;
                    b[1] += 1 << 3 - i;
                }
            }
        });

        Draw.rect(/* just in case this value somehow exceeds 15 */ b[0] >= 16 ? regions[0] : regions[b[0]], plan.drawx(), plan.drawy());
    }

    @Override
    public void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2) {
        //it is a freaking wire, no lasers!
    }

    public class PowerWireBuild extends PowerNodeBuild {
        protected byte curTile = 0, checkedNearby = 0;

        @Override
        public void draw() {
            Draw.rect(/* just in case this value somehow exceeds 15 */ curTile >= 16 ? regions[0] : regions[curTile], x, y);
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            //reset the mask
            curTile = 0;
            checkedNearby = 0;

            for (int i = 0; i < 4; i++) {
                Point2 p = Geometry.d4[i];
                Building build = world.build(tileX() + p.x, tileY() + p.y);
                if (build == null || !build.isValid() || build.team != this.team) continue;

                if (build.block.hasPower && ((checkedNearby & 1 << 3-i) == 0)) {
                    curTile += 1 << 3 - i;
                    checkedNearby += 1 << 3 - i;
                }
            }
        }

        @Override
        public void drawSelect() {

        }
    }
}
