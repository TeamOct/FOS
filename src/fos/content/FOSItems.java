package fos.content;

import arc.graphics.Color;
import mindustry.type.Item;

public class FOSItems {
    public static Item meteorite, tin, silver;

    public static void load(){
        meteorite = new Item("meteorite", Color.valueOf("de8900")){{
            hardness = 4;
            radioactivity = 0.1f;
        }};
        tin = new Item("tin", Color.valueOf("85b374")){{
            hardness = 1;
        }};
        silver = new Item("silver", Color.valueOf("813ba1")){{
            hardness = 3;
        }};
    }
}
