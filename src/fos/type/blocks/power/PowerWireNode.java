package fos.type.blocks.power;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import mindustry.core.Renderer;
import mindustry.graphics.*;
import mindustry.world.blocks.power.BeamNode;

import static mindustry.Vars.tilesize;

public class PowerWireNode extends BeamNode {

    public PowerWireNode(String name) {
        super(name);
        pulseMag = 0;
        laserWidth = 2f / 3f;
    }

    public boolean positionsValid(int x1, int y1, int x2, int y2) {
        if(x1 == x2){
            return Math.abs(y1 - y2) <= range;
        }else if(y1 == y2){
            return Math.abs(x1 - x2) <= range;
        }else{
            return false;
        }
    }

    @SuppressWarnings("unused")
    public class BridgeBeamNodeBuild extends BeamNodeBuild {
        @Override
        public void draw() {
            // copied from Building.class to ignore BeamNode's draw() implementation
            if (this.block.variants != 0 && this.block.variantRegions != null) {
                Draw.rect(this.block.variantRegions[Mathf.randomSeed(this.tile.pos(), 0, Math.max(0, this.block.variantRegions.length - 1))], this.x, this.y, this.drawrot());
            } else {
                Draw.rect(this.block.region, this.x, this.y, this.drawrot());
            }

            this.drawTeamTop();

            if(Mathf.zero(Renderer.laserOpacity)) return;

            Draw.z(Layer.blockOver + 0.1f);
            //Draw.color(laserColor1, laserColor2, (1f - power.graph.getSatisfaction()) * 0.86f + Mathf.absin(3f, 0.1f));
            Draw.alpha(Renderer.laserOpacity);

            for(int i = 3; i >= 0; i--){
                if(dests[i] != null && links[i].wasVisible && (!(links[i].block instanceof BeamNode node) ||
                    (links[i].tileX() != tileX() && links[i].tileY() != tileY()) ||
                    (links[i].id > id && range >= node.range) || range > node.range)){

                    int dst = Math.max(Math.abs(dests[i].x - tile.x), Math.abs(dests[i].y - tile.y));
                    //don't draw lasers for adjacent blocks
                    if(dst > 1 + size/2){
                        float w = laserWidth + Mathf.absin(pulseScl, pulseMag);
                        var point = Geometry.d4[i];
                        var vert = (tile.x == dests[i].x);

                        //invert left/down wires for shading purposes
                        boolean invert = i >= 2;
                        if (invert) w = -w;

                        float poff = invert ? tilesize/1.5f : tilesize/3f;
                        Drawf.laser(laser, laserEnd, x + poff*size*point.x, y + poff*size*point.y, dests[i].worldx() - poff*point.x, dests[i].worldy() - poff*point.y, w);
                    }
                }
            }

            Draw.reset();
        }
    }
}
