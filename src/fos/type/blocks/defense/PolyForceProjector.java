package fos.type.blocks.defense;

import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.ForceProjector;

import static mindustry.Vars.*;

public class PolyForceProjector extends ForceProjector {
    public float[] polygon;
    protected static Vec2[] polyLines;
    protected static final Cons<Bullet> customShieldConsumer = bullet -> {
        if(bullet.team != paramEntity.team && bullet.type.absorbable && Intersector.isInPolygon(((PolyForceBuild) paramEntity).hitbox, new Vec2(bullet.x - paramEntity.x, bullet.y - paramEntity.y))){
            bullet.absorb();
            paramEffect.at(bullet);
            paramEntity.hit = 1f;
            paramEntity.buildup += bullet.damage;
        }
    };

    public PolyForceProjector(String name) {
        super(name);
        rotate = true;
        rotateDraw = false;
        buildType = PolyForceBuild::new;
    }

    @Override
    public void init() {
        super.init();

        if (polygon.length % 2 == 1) throw new IllegalArgumentException("Polygon length must be even!");

        for(float f : polygon) {
            radius = Math.max(f, radius);
        }

        polyLines = new Vec2[polygon.length / 2];
        for(int i = 0; i < polygon.length; i += 2) {
            int n = Mathf.floor(i / 2f);
            polyLines[n] = new Vec2(polygon[i], polygon[i+1]);
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Vec2[] arr = new Vec2[polyLines.length];
        for(int i = 0; i < polyLines.length; i++) {
            arr[i] = new Vec2(polyLines[i].x, polyLines[i].y).rotate(rotation * 90);
        }

        drawPotentialLinks(x, y);
        //drawOverlay(x * 8 + offset, y * 8 + offset, rotation);

        Draw.color(Pal.gray);
        Lines.stroke(3f);
        Lines.poly(arr, x * 8, y * 8, 1f);
        Draw.color(player.team().color);
        Lines.stroke(1f);
        Lines.poly(arr, x * 8, y * 8, 1f);
        Draw.color();
    }

    public class PolyForceBuild extends ForceBuild {
        public float[] curPolygon;
        public Seq<Vec2> hitbox = new Seq<>();

        @Override
        public void created() {
            curPolygon = new float[polygon.length];

            for (Vec2 point : polyLines) {
                hitbox.add(point);
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();

            for(int i = 0; i < polygon.length; i += 2) {
                Vec2 v = new Vec2(polygon[i], polygon[i+1]).rotate(rotation * 90);
                curPolygon[i] = x + v.x; curPolygon[i+1] = y + v.y;
            }

            for(int i = 0; i < polygon.length; i += 2) {
                int n = Mathf.floor(i / 2f);
                Vec2 v = new Vec2(curPolygon[i], curPolygon[i+1]);
                polyLines[n] = v;
                hitbox.set(n, v.add(-x, -y));
            }
        }

        @Override
        public void deflectBullets() {
            float realRadius = realRadius();

            if(realRadius > 0 && !broken){
                paramEntity = this;
                paramEffect = absorbEffect;
                Groups.bullet.intersect(x - realRadius, y - realRadius, realRadius * 2f, realRadius * 2f, customShieldConsumer);
            }
        }

        @Override
        public void drawShield() {
            if(!broken){
                Polygon poly = new Polygon(curPolygon);

                Draw.z(Layer.shields);

                Draw.color(team.color, Color.white, Mathf.clamp(hit));

                if(renderer.animateShields){
                    Fill.poly(poly);
                }else{
                    Lines.stroke(1.5f);
                    Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                    Fill.poly(poly);
                    Draw.alpha(1f);
                    Lines.poly(polyLines, x, y, realRadius() / radius);
                    Draw.reset();
                }
            }

            Draw.reset();
        }
    }
}
