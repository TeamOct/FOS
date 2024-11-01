package fos.content;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.Tmp;
import fos.graphics.FOSPal;
import fos.entities.bullet.OhioBeamBulletType;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.weather.ParticleWeather;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
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
            Vec2 v = Tmp.v4.set(1, 1);
            e.x += v.x * 12f * e.fin();
            e.y += v.y * 12f * e.fin();
        }

        Draw.color(e.color, 0.4f * e.fout());
        Fill.circle(e.x, e.y, 4f * (1 + e.fin()));
    }),

    windSmoke = new Effect(240f, e -> {
        if (Groups.weather.contains(we -> we.weather instanceof ParticleWeather p && p.useWindVector)) {
            WeatherState w = Groups.weather.find(ws -> ws.weather instanceof ParticleWeather p && p.useWindVector);
            e.x += w.windVector.x * 24f * w.intensity * e.fin();
            e.y += w.windVector.y * 24f * w.intensity * e.fin();
        } else {
            Vec2 v = Tmp.v4.set(1, 1);
            e.x += v.x * 12f * e.fin();
            e.y += v.y * 12f * e.fin();
        }

        Draw.color(e.color, e.fout());
        Fill.circle(e.x, e.y, 4f * (1 + e.fin()));
    }),

    brassSmelterCraft = new Effect(1f, e -> {
        var c = FOSFluids.tokicite.color;
        //copypasta time
        tokiciteBoil.at(e.x - 8f, e.y - 8f, c);
        tokiciteBoil.at(e.x + 8f, e.y - 8f, c);
        tokiciteBoil.at(e.x - 8f, e.y + 8f, c);
        tokiciteBoil.at(e.x + 8f, e.y + 8f, c);
    }),

    generatorSmoke = new Effect(1f, e -> {
        var c = Pal.gray;
        //copypasta time
        windSmoke.at(e.x - 8f, e.y - 8f, c);
        windSmoke.at(e.x + 8f, e.y - 8f, c);
        windSmoke.at(e.x - 8f, e.y + 8f, c);
        windSmoke.at(e.x + 8f, e.y + 8f, c);
    }),

    deathrayDespawn = new Effect(60f, 720f, e -> {
        var data = Point2.unpack(e.data());

        OhioBeamBulletType.drawBeam(Pal.slagOrange, e.x, e.y, 18f * e.fout());
        OhioBeamBulletType.drawBeam(Pal.slagOrange, data.x, data.y, 18f * e.fout());
    }),

    refinerySmoke = new Effect(1f, e -> {
        //RECYCLED ASSET TIME
        windSmoke.at(e.x, e.y, Pal.gray);
    }),

    dotLaserLine = new Effect(10f, e -> {
        if (!(e.data instanceof Vec2 v)) return;

        Drawf.laser(Core.atlas.find("fos-point-laser"), Core.atlas.find("fos-point-laser-end"), Core.atlas.find("fos-point-laser-end"),
            v.x, v.y, e.x, e.y, 0.4f);
    }){{
        clip = 550f;
    }},

    dotLaserEnd = new Effect(10f, e -> {
        Draw.rect(Core.atlas.find("fos-point-laser-end"), e.x, e.y, 12f, 12f);
    }).layer(Layer.effect + 0.1f),

    citadelSteam = new Effect(40f, e -> {
        randLenVectors(e.id, 2, 80f, 250f, 10f, ((x, y) -> {
            color(Color.gray);
            alpha((0.5f - Math.abs(e.fin() - 0.5f)) * 2f);
            Fill.circle(e.x + (x * e.finpow()), e.y + (y * e.finpow()), 0.5f + e.fout() * 4f);
        }));
        randLenVectors(e.id, 2, 80f, 290f, 10f, ((x, y) -> {
            color(Color.gray);
            alpha((0.5f - Math.abs(e.fin() - 0.5f)) * 2f);
            Fill.circle(e.x + (x * e.finpow()), e.y + (y * e.finpow()), 0.5f + e.fout() * 4f);
        }));
    }),

    fireLong = new Effect(300f, e -> {
        color(Pal.lightFlame, Pal.darkFlame, e.fin());

        randLenVectors(e.id, 2, 2f + e.fin() * 9f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.2f + e.fslope() * 1.5f);
        });

        color();

        Drawf.light(e.x, e.y, 20f * e.fslope(), Pal.lightFlame, 0.5f);
    }),

    fireSmokeLong = new Effect(300f, e -> {
        color(Color.gray);

        randLenVectors(e.id, 1, 2f + e.fin() * 7f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.2f + e.fslope() * 1.5f);
        });
    }),

    corruLogo = new Effect(50f, e -> {
        Draw.color(FOSPal.hacked);
        Draw.alpha(e.fout());

        Draw.rect(Core.atlas.find("fos-team-corru-upscale"), e.x, e.y, 64 * (1 + e.fin()), 64 * (1 + e.fin()));

        Draw.reset();
    }),

    tokiciteDrop = new Effect(80f, e -> {
        color(FOSFluids.tokicite.color);
        alpha(Mathf.clamp(e.fout() * 2f));

        Fill.circle(e.x, e.y, e.fout() * 4f);
    }),

    burrowDustSingle = new Effect(40f, e -> {
        Draw.color(e.color);
        Draw.alpha(0.6f);

        randLenVectors(e.id, 1, e.fin() * 160f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 24f);
        });

        Draw.reset();
    }).layer(Layer.groundUnit + 1f),

    burrowDust = new Effect(120f, e -> {
        burrowDustSingle.at(e.x, e.y, e.color);
    }),

    bruntChargeSmoke = new Effect(80f, e -> {
        color(Pal.reactorPurple2);
        alpha(e.fin());

        randLenVectors(e.id, 1, e.fout() * 20f, (x, y) -> {
            float rad = 12f * e.fout();

            Fill.circle(e.x + x, e.y + y, rad);
            Drawf.light(e.x + x, e.y + y, rad * 2.5f, Pal.reactorPurple, 0.5f);
        });
    }).followParent(true).layer(Layer.flyingUnit + 0.01f),

    bruntCharge = new Effect(300f, e -> {
        if (Mathf.chance(0.08f) && e.time < 220f) {
            bruntChargeSmoke.at(e.x, e.y);
        }
    }).followParent(true),

    bugDeath1 = new Effect(600f, e -> {
        float intensity = 4f;

        color(Color.valueOf("51c7d8"), 0.6f);
        for (int i = 0; i < 4; i++) {
            Fx.rand.setSeed(e.id* 2L + i);
            float lenScl = Fx.rand.random(0.5f, 1f);
            int fi = i;
            e.scaled(e.lifetime * lenScl, e2 -> {
                randLenVectors(e2.id + fi - 1, e2.fin(Interp.pow10Out), (int)(2.9f * intensity), 1.8f * intensity, (x, y, in, out) -> {
                    float fout = e2.fout(Interp.pow5Out) * Fx.rand.random(0.5f, 1f);
                    float rad = fout * ((intensity - 1f));

                    Fill.circle(e2.x + x, e2.y + y, rad);
                    //Drawf.light(e2.x + x, e2.y + y, rad * 2.5f, e.color, 0.5f);
                });
            });
        }
    }).layer(Layer.floor + 1),

    bugDeath2 = new Effect(90f, e -> {
        color(Color.valueOf("51c7d8"), 0.6f);

        randLenVectors(e.id, 4, e.fin() * 28f, (x, y) -> {
            Fill.circle(x, y, 5f);
            if (e.fin() >= 0.99f) bugDeath1.at(x, y);
        });
    }).layer(Layer.legUnit + 1);
}
