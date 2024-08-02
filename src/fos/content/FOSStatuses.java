package fos.content;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.util.Time;
import fos.graphics.FOSPal;
import fos.type.draw.FOSStats;
import fos.type.statuses.HackedEffect;
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
        injected = new StatusEffect("injected"){
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
        tokiciteSlowed = new StatusEffect("tokicite-slowed"){{
            speedMultiplier = 0.25f;
            effect = FOSFx.tokiciteDrop;
        }};
        buildBoost = new StatusEffect("build-boost"){{
            buildSpeedMultiplier = 1.25f;
        }};
        dissolving = new StatusEffect("dissolving"){{
            healthMultiplier = 0.8f;
            damage = 1.5f;
            effect = new Effect(42f, e -> {
                color(Color.valueOf("6abe30"));

                randLenVectors(e.id, 2, 1f + e.fin() * 2f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout());
                });
            });
        }};
    }
}
