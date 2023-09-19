package fos.content;

import arc.graphics.*;
import arc.struct.Seq;
import fos.core.FOSVars;
import fos.graphics.*;
import mindustry.type.Item;

import static mindustry.content.Items.*;

public class FOSItems {
    public static Item rawNethratium, rawElbium, rawElithite, aluminium, tin, silver, lithium, brass, sulphur, cuberium, diamond, vanadium, iridium, luminium;

    public static Seq<Item> uxerdItems = new Seq<>(), lumoniItems = new Seq<>();

    public static void load(){
        rawNethratium = new Item("raw-nethratium", Color.valueOf("974545")){{
            hardness = 1;
            cost = 0.8f;
        }};
        rawElbium = new Item("raw-elbium", Color.valueOf("975c43")){{
            hardness = 1;
        }};
        rawElithite = new Item("raw-elithite", Color.valueOf("4e4f55")){{
            hardness = 1;
        }};
        aluminium = new Item("aluminium", Color.valueOf("de8900")){{
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
        brass = new Item("brass", Color.valueOf("b57050")){{
            cost = 2f;
        }};
        sulphur = new Item("sulphur", Color.valueOf("000000" /*TODO*/));
        cuberium = new Item("cuberium", Color.valueOf("855992")){{
            cost = 3f;
        }};
        diamond = new Item("diamond", Color.valueOf("b6cdec")){{
            cost = 2.8f;
            hardness = 4;
        }};
        vanadium = new Item("vanadium", Color.valueOf("a3afbd")){{
            cost = 3f;
            hardness = 5;
        }};
        iridium = new Item("iridium", Color.valueOf("a3bda7")){{
            cost = 4f;
            hardness = 6;
        }};
        luminium = new Item("luminium", Color.valueOf("72cbcf")){
            {
                cost = 5.9f;
                hardness = 7;
            }
            @Override
            public void loadIcon(){
                super.loadIcon();
                fullIcon = uiIcon = new ShaderTextureRegion(FOSShaders.lis,
                    new Texture(FOSVars.internalTree.child("sprites/items/luminium/luminium.png")), (s, o) -> {}, 0);
            }
        };

        uxerdItems.addAll(rawNethratium, rawElbium, rawElithite, aluminium, tin, silver, lithium, titanium, cuberium);
        lumoniItems.addAll(copper, tin, silver, diamond, sand, silicon, brass, vanadium, iridium, luminium);
    }
}
