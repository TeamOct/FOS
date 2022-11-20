package fos.type.blocks.defense;

import arc.Events;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.Time;
import fos.content.FOSFx;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.ForceProjector;

import static mindustry.Vars.*;

// I already know this code's gonna be yoinked.
// If you do yoink this, PLEASE consider giving me a credit
public class PolyForceProjector extends ForceProjector {
    public float[] polygon = new float[]{0, 0};
    protected Vec2[] polyLines = new Vec2[]{};
    protected final Cons<Bullet> customShieldConsumer = bullet -> {
        if(bullet.team != paramEntity.team && bullet.type.absorbable && Intersector.isInPolygon(((PolyForceBuild) paramEntity).hitbox, new Vec2(bullet.x, bullet.y))){
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
        shieldBreakEffect = FOSFx.rectShieldBreak;
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
            int n = i / 2;
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
        drawOverlay(x * 8 + offset, y * 8 + offset, rotation);

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
        public Vec2[] curPolyLines;
        public Seq<Vec2> hitbox = new Seq<>();

        @Override
        public void created() {
            curPolygon = new float[polygon.length];
            curPolyLines = new Vec2[polyLines.length + 1];

            for (int i = 0; i < polyLines.length; i++) {
                Vec2 point = polyLines[i];

                hitbox.add(point);
                curPolyLines[i] = polyLines[i].cpy();
            }

            curPolyLines[polyLines.length] = curPolyLines[0].cpy();
        }

        @Override
        public void updateTile() {
            //I just copied the entire updateTile() from the superclass so that I could modify ONE FREAKING THING in here.
            boolean phaseValid = itemConsumer != null && itemConsumer.efficiency(this) > 0;

            phaseHeat = Mathf.lerpDelta(phaseHeat, Mathf.num(phaseValid), 0.1f);

            if(phaseValid && !broken && timer(timerUse, phaseUseTime) && efficiency > 0){
                consume();
            }

            radscl = Mathf.lerpDelta(radscl, broken ? 0f : warmup, 0.05f);

            if(Mathf.chanceDelta(buildup / shieldHealth * 0.1f)){
                Fx.reactorsmoke.at(x + Mathf.range(tilesize / 2f), y + Mathf.range(tilesize / 2f));
            }

            warmup = Mathf.lerpDelta(warmup, efficiency, 0.1f);

            if(buildup > 0){
                float scale = !broken ? cooldownNormal : cooldownBrokenBase;

                //Anuke hates this system
                if(coolantConsumer != null){
                    if(coolantConsumer.efficiency(this) > 0){
                        coolantConsumer.update(this);
                        scale *= (cooldownLiquid * (1f + (liquids.current().heatCapacity - 0.4f) * 0.9f));
                    }
                }

                buildup -= delta() * scale;
            }

            if(broken && buildup <= 0){
                broken = false;
            }

            if(buildup >= shieldHealth + phaseShieldBoost * phaseHeat && !broken){
                broken = true;
                buildup = shieldHealth;
                shieldBreakEffect.at(x, y, realRadius(), team.color, rotation % 2);
                if(team != state.rules.defaultTeam){
                    Events.fire(EventType.Trigger.forceProjectorBreak);
                }
            }

            if(hit > 0f){
                hit -= 1f / 5f * Time.delta;
            }

            deflectBullets();

            for(int i = 0; i < polygon.length; i += 2) {
                Vec2 v = new Vec2(polygon[i], polygon[i+1]).rotate(rotation * 90);
                curPolygon[i] = x + v.x * (realRadius() / radius); curPolygon[i+1] = y + v.y * (realRadius() / radius);
            }

            for(int i = 0; i < curPolygon.length; i += 2) {
                int n = i / 2;
                Vec2 v = new Vec2(curPolygon[i], curPolygon[i+1]);
                curPolyLines[n].set(v).add(-x, -y);
                hitbox.set(n, v);
            }

            curPolyLines[polyLines.length] = curPolyLines[0].cpy();
        }

        @Override
        public void onRemoved() {
            float radius = realRadius();
            if(!broken && radius > 1f) FOSFx.rectForceShrink.at(x, y, radius, team.color, rotation % 2);
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
            if (!broken && efficiency > 0) {
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
                    Lines.poly(curPolyLines, x, y, 1f);
                    Draw.reset();
                }
            }

            Draw.reset();
        }
    }
}
