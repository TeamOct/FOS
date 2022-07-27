package fos.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import mindustry.entities.Effect;
import mindustry.type.StatusEffect;

public class FOSStatuses {
    public static StatusEffect hacked;

    public static void load() {
        hacked = new StatusEffect("hacked") {{
            effectChance = 1;
            permanent = true;
            healthMultiplier = 0.5f;
            effect = new Effect(60, e -> Draw.color(Color.valueOf("51a0b0"), Color.valueOf("8ae3df"), e.fin()));
            damage = 5;
        }};
    }
}
