package fos.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.type.Item;

public class FOSItems {
    public static Item rawNethratium, rawElbium, rawElithite, aluminium, tin, silver, lithium, cuberium;

    public static Seq<Item> uxerdItems = new Seq<>(), luminaItems = new Seq<>();

    public static void load(){
        rawNethratium = new Item("raw-nethratium", Color.valueOf("de8900")){{
            hardness = 1;
            cost = 0.8f;
        }};
        rawElbium = new Item("raw-elbium", Color.valueOf("000000")){{
            hardness = 1;
        }};
        rawElithite = new Item("raw-elithite", Color.valueOf("000000")){{
            hardness = 1;
        }};
        aluminium = new Item("aluminium", Color.valueOf("000000")){{
            radioactivity = 0.1f;
        }};
        tin = new Item("tin", Color.valueOf("85b374")){{
            hardness = 2;
            cost = 1.2f;
        }};
        silver = new Item("silver", Color.valueOf("813ba1")){{
            hardness = 3;
            cost = 1.5f;
        }};
        lithium = new Item("lithium", Color.valueOf("b6e358")){{
            hardness = 1;
            cost = 1.4f;
        }};
        cuberium = new Item("cuberium", Color.valueOf("000000")){{
            cost = 3f;
        }};

        uxerdItems.addAll(rawNethratium, rawElbium, rawElithite, aluminium, tin, silver, lithium, Items.titanium, cuberium);
        luminaItems.addAll(tin, silver);
    }
}
