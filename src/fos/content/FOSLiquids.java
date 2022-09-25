package fos.content;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class FOSLiquids {
    public static Liquid oxygen;

    public static void load() {
        oxygen = new Liquid("oxygen", Color.valueOf("ffbdd4")){{
            gas = true;
            flammability = 0.8f;
        }};
    }
}
