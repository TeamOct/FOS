package fos.content;

import arc.graphics.Color;
import mindustry.type.Item;

public class FOSItems {
    public static Item meteorite, tin, luminium;

    public static void load(){
        meteorite = new Item("meteorite", Color.valueOf("de8900")){{
            hardness = 4;
            radioactivity = 0.1f;
        }};
        tin = new Item("tin", Color.valueOf("000000")){{
            hardness = 1;
        }};
        luminium = new Item("luminium", Color.valueOf("000000")){{
            hardness = 3;
        }};
    }
}
