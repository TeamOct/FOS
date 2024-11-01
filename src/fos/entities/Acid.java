package fos.entities;

import arc.math.Mathf;
import arc.util.Time;
import fos.content.FOSStatuses;
import fos.mod.AcidController;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.StatusEffect;

public class Acid implements Cloneable{
    public Team team = Team.blue;
    public boolean alive = false;
    public float x;
    public float y;
    public float damage = 10;
    public float maxLifetime = 120f;
    public float lifetime = maxLifetime;
    public Effect effect = Fx.none;
    public StatusEffect status = FOSStatuses.dissolving;
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
        at(a, Team.derelict, 10, 120f, x, y);
    }

    public static void at(Acid a, Team team, float damage, float lifetime, float x, float y) {
        Acid aa = a.clone();
        aa.team = team;
        aa.damage = damage;
        aa.maxLifetime = lifetime;
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

        Building b = Vars.world.buildWorld(acid.x, acid.y);

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