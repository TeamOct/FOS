package fos.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.type.Item;

import static mindustry.content.Items.*;

public class FOSItems {
    public static Item rawNethratium, rawElbium, rawElithite, aluminium, tin, silver, lithium, cuberium, diamond, vanadium, iridium, luminium;

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
        diamond = new Item("diamond", Color.valueOf("b6cdec")){{
            cost = 2.8f;
            hardness = 4;
        }};
        vanadium = new Item("vanadium", Color.valueOf("000000")){{
            cost = 3f;
            hardness = 5;
        }};
        iridium = new Item("iridium", Color.valueOf("000000")){{
            cost = 4f;
            hardness = 6;
        }};
        luminium = new Item("luminium", Color.valueOf("000000")){{
            cost = 5.9f;
            hardness = 7;
        }};

        uxerdItems.addAll(rawNethratium, rawElbium, rawElithite, aluminium, tin, silver, lithium, titanium, cuberium);
        luminaItems.addAll(tin, silver, diamond, sand, silicon, vanadium, iridium, luminium);
    }
}
