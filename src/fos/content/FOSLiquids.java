package fos.content;

import arc.graphics.Color;
import mindustry.content.StatusEffects;
import mindustry.type.Liquid;

public class FOSLiquids {
    public static Liquid oxygen, tokicite;

    public static void load() {
        oxygen = new Liquid("oxygen", Color.valueOf("ffbdd4")){{
            gas = true;
            flammability = 0.8f;
        }};
        tokicite = new Liquid("tokicite", Color.valueOf("d16792")){{
            viscosity = 1.1f;
            effect = StatusEffects.slow;
            capPuddles = false;
        }};
    }
}
