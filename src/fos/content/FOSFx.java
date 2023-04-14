package fos.content;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import fos.type.bullets.OhioBeamBulletType;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.weather.ParticleWeather;

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
        if(renderer.animateShields) {
            Fill.poly(poly);
        } else {
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
        Lines.poly(polyLines, e.x, e.y, 1f);
    }).followParent(true),

    rectShockwave = new Effect(20, e -> {
        float r = e.fin() * 2;
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
        stroke(3f * e.fout());
        Lines.poly(polyLines, e.x, e.y, 1f);
    }).followParent(true).layer(Layer.shields),

    tokiciteBoil = new Effect(240f, e -> {
        if (Groups.weather.contains(we -> we.weather instanceof ParticleWeather p && p.useWindVector)) {
            WeatherState w = Groups.weather.find(ws -> ws.weather instanceof ParticleWeather p && p.useWindVector);
            e.x += w.windVector.x * 24f * w.intensity * e.fin();
            e.y += w.windVector.y * 24f * w.intensity * e.fin();
        } else {
            Vec2 v = new Vec2(1, 1);
            e.x += v.x * 12f * e.fin();
            e.y += v.y * 12f * e.fin();
        }
        Draw.color(FOSFluids.tokicite.color, 0.4f * e.fout());
        Fill.circle(e.x, e.y, 4f * (1 + e.fin()));
    }),

    brassSmelterCraft = new Effect(1f, e -> {
        tokiciteBoil.at(e.x - 8f, e.y - 8f);
        tokiciteBoil.at(e.x + 8f, e.y - 8f);
        tokiciteBoil.at(e.x - 8f, e.y + 8f);
        tokiciteBoil.at(e.x + 8f, e.y + 8f);
    }),

    deathrayDespawn = new Effect(60f, 720f, e -> {
        var data = Point2.unpack(e.data());

        OhioBeamBulletType.drawBeam(Pal.slagOrange, e.x, e.y, 18f * e.fout());
        OhioBeamBulletType.drawBeam(Pal.slagOrange, data.x*8f, data.y*8f, 18f * e.fout());
    });
}
