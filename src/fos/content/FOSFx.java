package fos.content;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.geom.Polygon;
import arc.math.geom.Vec2;
import mindustry.entities.Effect;
import mindustry.graphics.Layer;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.Vars.renderer;

public class FOSFx {
    public static Effect
    rectForceShrink = new Effect(20, e -> {
        float r = e.fout();
        Polygon poly = new Polygon(new float[]{
            -120*r, -40*r,
            120*r, -40*r,
            120*r, 40*r,
            -120*r, 40*r
        });
        float[] arr = poly.getVertices();
        boolean isVertical = e.data.equals(1);
        Vec2[] polyLines = new Vec2[arr.length / 2];
        for(int i = 0; i < arr.length; i += 2) {
            int n = i / 2;
            Vec2 v = new Vec2(arr[i], arr[i+1]);
            if (isVertical) v.rotate(90);
            polyLines[n] = v;
        }

        color(e.color, r);
        if(renderer.animateShields){
            Fill.poly(poly);
        }else{
            stroke(1.5f);
            Draw.alpha(0.09f);
            Fill.poly(poly);
            Draw.alpha(1f);
            Lines.poly(polyLines, e.x, e.y, r);
        }
    }).layer(Layer.shields),
    rectShieldBreak = new Effect(40, e -> {
        float r = e.fout();
        float[] poly = new float[]{
            -120*r, -40*r,
            120*r, -40*r,
            120*r, 40*r,
            -120*r, 40*r
        };
        boolean isVertical = e.data.equals(1);
        Vec2[] polyLines = new Vec2[poly.length / 2];
        for(int i = 0; i < polyLines.length; i++) {
            int n = i * 2;
            Vec2 v = new Vec2(poly[n], poly[n+1]);
            if (isVertical) v.rotate(90);
            polyLines[i] = v;
        }
        color(e.color);
        stroke(3f * r);
        Lines.poly(polyLines, e.x, e.y, r);
    }).followParent(true);
}
