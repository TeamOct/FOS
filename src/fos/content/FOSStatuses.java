package fos.content;

import fos.graphics.FOSPal;
import fos.type.draw.FOSStats;
import fos.type.statuses.HackedEffect;
import mindustry.type.StatusEffect;
import mindustry.world.meta.StatUnit;

public class FOSStatuses {
    public static StatusEffect hacked, injected, tokiciteSlowed;

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
        };
        tokiciteSlowed = new StatusEffect("tokicite-slowed"){{
            speedMultiplier = 0.4f;
        }};
    }
}
