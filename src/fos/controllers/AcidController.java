package fos.controllers;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.util.Log;
import arc.util.Time;
import fos.content.FOSBlocks;
import fos.content.FOSStatuses;
import fos.type.content.FOSStatusEffect;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;
import mindustry.world.Tile;

public class AcidController {
    public static Color
            acidColor = Color.valueOf("B3DB81");
    public static Acid
            acid2 = new Acid(1f, new Effect(260f, e -> {

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
        Events.on(EventType.BuildDamageEvent.class, e -> {
            Acid.at(acid2, e.source.x, e.source.y);
            Log.info("Acid at " + e.build.x + " " + e.build.y);
        });
    }

    private static void update() {
        acids.each(Acid::update);
    }
    private static void draw() {
        Draw.z(Layer.effect-5);
        acids.each(Acid::draw);
    }
}

class Acid implements Cloneable{
    public Team team = Team.blue;
    public boolean alive = false;
    public float x;
    public float y;
    public float damage = 10;
    public float maxLifetime = 120f;
    public float lifetime = maxLifetime;
    public Effect effect = Fx.none;
    public StatusEffect status = FOSStatuses.acid;
    public float timer = 0f;
    public static float maxTimer = 20f;

    public Acid(float damage, Effect effect, float lifetime) {
        this.damage = damage;
        this.effect = effect;
        this.maxLifetime = lifetime;
    }

    public Acid() {}

    public Acid clone() {
        try {
            return (Acid) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("да пошло это все нахуй\n\n" + e);
        }
    }


    public void at(float x, float y) {
        alive = true;
        effect.at(x, y);
        this.x = x;
        this.y = y;
        lifetime = maxLifetime;
        AcidController.acids.add(this);
    }

    public static void at(Acid a, float x, float y) {
        Acid aa = a.clone();
        aa.at(x, y);
    }

    public static void update(Acid acid) {
        if (!acid.alive) return;
        if (Vars.state.isPaused()) return;
        acid.lifetime -= Time.delta;
        if (acid.lifetime <= 0) {
            dead(acid);
            return;
        }

        Building b = Vars.world.build(Mathf.round(acid.x/8f), Mathf.round(acid.y/8f));

        if (b != null) {
            if (b.team() != acid.team) b.damage(acid.damage*Time.delta);
        }

//        if (acid.noBuild) {
//            if (b == null) {
//                Vars.world.tiles.set(Mathf.round(acid.x/8), Mathf.round(acid.y/8), FOSBlocks.crutch.newBuilding().tile());
//            }
//        }

        Groups.unit.each(un -> Mathf.dst(un.x, un.y, acid.x, acid.y) < 6.7f, u -> {
            if (!u.hasEffect(acid.status) && u.team() != acid.team) u.apply(acid.status, acid.maxLifetime);
        });

        if (acid.timer <= 0f) {
            acid.timer = maxTimer;
            acid.effect.at(acid.x + Mathf.cosDeg(Mathf.random(0, 360))*3f, acid.y + Mathf.sinDeg(Mathf.random(0, 360))*3f);
        }
        acid.timer -= Time.delta;
    }

    public static void draw(Acid acid) {

    }

    public static void dead(Acid acid) {
        acid.alive = false;
//        Building b = Vars.world.build(Mathf.round(acid.x/8), Mathf.round(acid.y/8));
//        if (b != null && b.block.equals(FOSBlocks.crutch)) b.tile.setAir();
        AcidController.acids.remove(acid);
    }
}