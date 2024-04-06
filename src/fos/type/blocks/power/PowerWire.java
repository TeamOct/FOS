package fos.type.blocks.power;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.*;
import fos.core.FOSVars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class PowerWire extends PowerNode {
    public TextureRegion[] regions = new TextureRegion[16];
    public @Nullable Block bridgeReplacement;

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

        laser = Core.atlas.find(name + "-wire");
        laserEnd = Core.atlas.find(name + "-edge");
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

        Draw.rect(b[0] >= 16 || maxNodes > 0 ? region : regions[b[0]], plan.drawx(), plan.drawy());
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans) {
        if (bridgeReplacement == null) return;

        calculateBridges(plans, (PowerWireNode) bridgeReplacement);
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation) {
        //no
    }

    @Override
    public void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2) {
        //no
    }

    //stolen from Placement class
    void calculateBridges(Seq<BuildPlan> plans, PowerWireNode bridge) {
        //if(isSidePlace(plans)) return;

        //check for orthogonal placement + unlocked state
        if(!(plans.first().x == plans.peek().x || plans.first().y == plans.peek().y) || !bridge.unlockedNow()){
            return;
        }

        //don't count the same block AND power blocks as inaccessible
        Boolf<BuildPlan> placeable = plan -> (plan.placeable(player.team())) ||
            (plan.tile() != null && (plan.tile().block() == plan.block || plan.tile().block().hasPower));
        var result = FOSVars.wirePlans.clear();
        var team = player.team();
        //var rotated = plans.first().tile() != null && plans.first().tile().absoluteRelativeTo(plans.peek().x, plans.peek().y) == Mathf.mod(plans.first().rotation + 2, 4);

        outer:
        for(int i = 0; i < plans.size;){
            var cur = plans.get(i);
            result.add(cur);

            //gap found
            if(i < plans.size - 1 && placeable.get(cur) && !placeable.get(plans.get(i + 1))){

                //find the closest valid position within range
                for(int j = i + 1; j < plans.size; j++){
                    var other = plans.get(j);

                    //out of range now, set to current position and keep scanning forward for next occurrence
                    if(!bridge.positionsValid(cur.x, cur.y, other.x, other.y)){
                        //add 'missed' conveyors
                        for(int k = i + 1; k < j; k++){
                            result.add(plans.get(k));
                        }
                        i = j;
                        continue outer;
                    }else if(other.placeable(team)){
                        //found a link, assign bridges
                        cur.block = bridge;
                        other.block = bridge;
/*
                        if(rotated){
                            other.config = new Point2(cur.x - other.x,  cur.y - other.y);
                        }else{
                            cur.config = new Point2(other.x - cur.x, other.y - cur.y);
                        }
*/

                        i = j;
                        continue outer;
                    }
                }

                //if it got here, that means nothing was found. this likely means there's a bunch of stuff at the end; add it and bail out
                for(int j = i + 1; j < plans.size; j++){
                    result.add(plans.get(j));
                }
                break;
            }else{
                i ++;
            }
        }

        plans.set(result);
    }

    @SuppressWarnings("unused")
    public class PowerWireBuild extends PowerNodeBuild {
        protected byte curTile = 0, checkedNearby = 0;

        @Override
        public void draw() {
            //connect wires and non-squareSprite blocks properly
            for (var p : Geometry.d4) {
                var block = world.tile(tileX() + p.x, tileY() + p.y).block();
                if (block != null && block.hasPower && !block.squareSprite) {
                    Draw.z(Layer.blockUnder);
                    Draw.rect(p.x == 0 ? regions[5] : regions[10], x + p.x*4, y + p.y*4);
                }
            }

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
            //no
        }
    }
}
