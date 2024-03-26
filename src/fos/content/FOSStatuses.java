package fos.content;

import fos.graphics.FOSPal;
import fos.type.statuses.HackedEffect;
import mindustry.type.StatusEffect;

public class FOSStatuses {
    public static StatusEffect hacked;

    public static void load() {
        hacked = new HackedEffect("hacked"){{
            color = FOSPal.hackedBack;
        }};
    }
}
