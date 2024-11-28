package fos.content;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.util.Time;
import fos.graphics.FOSPal;
import fos.type.statuses.*;
import fos.world.draw.FOSStats;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.meta.StatUnit;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;

public class FOSStatuses {
    public static StatusEffect hacked, injected, tokiciteSlowed, buildBoost, dissolving;

    public static void load() {
        hacked = new HackedEffect("hacked"){{
            color = FOSPal.hackedBack;
        }};

        injected = new FOSStatusEffect("injected"){
            {
                color = FOSPal.hackedBack;
            }
            @Override
            public void setStats() {
                super.setStats();
                stats.add(FOSStats.hackChanceMultiplier, 125f, StatUnit.percent);
            }

            @Override
            public void draw(Unit unit) {
                Draw.color(FOSPal.hacked);
                for (int i = 0; i < 360; i += 120) {
                    Lines.arc(unit.x, unit.y, unit.hitSize * 1.1f, 0.25f, i + Time.time);
                }
                Draw.reset();
            }
        };
        tokiciteSlowed = new FOSStatusEffect("tokicite-slowed"){{
            speedMultiplier = 0.25f;
            effect = FOSFx.tokiciteDrop;
            color = Color.valueOf("d16792");
        }};
        buildBoost = new StatusEffect("build-boost"){{
            buildSpeedMultiplier = 1.25f;
            show = false;
        }};
        dissolving = new FOSStatusEffect("dissolving"){{
            healthMultiplier = 0.8f;
            damage = 1.5f;
            color = Color.valueOf("b3db81");
            effect = new Effect(180f, e -> {
                color(color);

                randLenVectors(e.id, 2, 3f + e.fin() * 24f, (x, y) -> {
//                    float a = Mathf.randomSeed(e.id+2, 0, Mathf.pi*2f) + 3 * e.fin() * Mathf.pi;
//                    Fill.circle(e.x + x + Mathf.cos(a)*2, e.y + y + Mathf.sin(a)*2, 1.3f*e.finpow());
                    Fill.circle(e.x + x, e.y + y, 1.3f*e.finpow());
                });
            });
        }};
    }
}
