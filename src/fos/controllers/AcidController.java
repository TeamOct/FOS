package fos.controllers;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.ObjectSet;
import fos.entities.Acid;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.graphics.Layer;

public class AcidController {
    public static Color acidColor = Color.valueOf("B3DB81");
    public static Acid acid2 = new Acid(1f, new Effect(260f, e -> {
        Draw.z(29-0.1f);
        Draw.color(acidColor);
        float xx = 1-(Mathf.cos(2*Mathf.pi*Mathf.pow(2.71828182845f, 3f*(1f-e.fin())-3f))+1f)/2f;
        Fill.circle(e.x + Mathf.cos(Mathf.randomSeed(e.id+2, 0, Mathf.pi*2f)) * e.finpow() * 2.8f, e.y + Mathf.sin(Mathf.randomSeed(e.id+2, 0, Mathf.pi*2f)) * e.finpow() * 2.8f, 5.7f*xx);

        e.scaled(170f, ee -> {
            Draw.z(Layer.effect+0.1f);
            float x = 1-(Mathf.cos(2*Mathf.pi*Mathf.pow(2.71828182845f, 3f*(1f-e.fin())-3f))+1f)/2f;
            Color c = new Color(acidColor.r * Mathf.randomSeed(e.id+1, 0.7f, 1.3f), acidColor.g * Mathf.randomSeed(e.id, 0.55f, 1.55f), acidColor.b * Mathf.randomSeed(e.id+1, 0.7f, 1.3f), 0.18f);
            Draw.color(c);
            Fill.circle(e.x + Mathf.cos(Mathf.randomSeed(e.id, 0, Mathf.pi*2f)) * e.finpow() * 5f, e.y + Mathf.sin(Mathf.randomSeed(e.id, 0, Mathf.pi*2f)) * e.finpow() * 5f, 3.8f*x);
        });
    }), 425f);

    public static ObjectSet<Acid> acids = new ObjectSet<>();

    public static void init() {
        Events.run(EventType.Trigger.update, AcidController::update);
        Events.run(EventType.Trigger.draw, AcidController::draw);
/*
        Events.on(EventType.BuildDamageEvent.class, e -> {
            Acid.at(acid2, e.source.x, e.source.y);
            Log.info("Acid at " + e.build.x + " " + e.build.y);
        });
*/
    }

    private static void update() {
        acids.each(Acid::update);
    }
    private static void draw() {
        Draw.z(Layer.effect-5);
        acids.each(Acid::draw);
    }
}