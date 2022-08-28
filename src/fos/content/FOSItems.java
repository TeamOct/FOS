package fos.content;

import arc.graphics.Color;
import mindustry.type.Item;

public class FOSItems {
    public static Item nethratium, aluminium, tin, silver, lithium;

    public static void load(){
        nethratium = new Item("raw-nethratium", Color.valueOf("de8900")){{
            hardness = 1;
            radioactivity = 0.1f;
        }};
        aluminium = new Item("aluminium", Color.valueOf("000000"));
        tin = new Item("tin", Color.valueOf("85b374")){{
            hardness = 102;
        }};
        silver = new Item("silver", Color.valueOf("813ba1")){{
            hardness = 103;
        }};
        lithium = new Item("lithium", Color.valueOf("b6e358")){{
            hardness = 1;
        }};
    }
}
