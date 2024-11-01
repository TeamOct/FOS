package fos.content;

import arc.graphics.*;
import arc.struct.Seq;
import fos.core.FOSVars;
import fos.graphics.*;
import mindustry.type.Item;

import static mindustry.content.Items.*;

public class FOSItems {
    public static Item
        // LUMONI
        zinc, nickel, quartz, brass, sulphur, cuberium, diamond, vanadium, silver, luminium;

    public static Item
        // UXERD
        rawNethratium, rawElbium, rawElithite, aluminium, lithium;

    public static Seq<Item> uxerdItems = new Seq<>(), lumoniItems = new Seq<>();

    public static void load(){
        zinc = new Item("zinc", Color.valueOf("85b374")){{
            hardness = 2;
            cost = 1.2f;
        }};
        nickel = new Item("nickel", Color.valueOf("a3bda7")){{
            hardness = 3;
            cost = 1.5f;
        }};
        quartz = new Item("quartz", Color.valueOf("ffffff"));
        brass = new Item("brass", Color.valueOf("b57050")){{
            cost = 2f;
        }};
        sulphur = new Item("sulphur", Color.valueOf("f0b454")){{
            flammability = 0.6f;
            explosiveness = 0.4f;
            cost = 0.5f;
        }};
        cuberium = new Item("cuberium", Color.valueOf("855992")){{
            cost = 3f;
        }};
        diamond = new Item("diamond", Color.valueOf("b6cdec")){{
            cost = 2.8f;
            hardness = 4;
            flammability = 0.1f;
        }};
        vanadium = new Item("vanadium", Color.valueOf("a3afbd")){{
            cost = 3f;
            hardness = 5;
        }};
        silver = new Item("silver", Color.valueOf("813ba1")){{
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
                    new Texture(FOSVars.internalTree.child("sprites/items/luminium.png")), (s, o) -> {}, 0);
            }
        };

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
        lithium = new Item("lithium", Color.valueOf("b6e358")){{
            hardness = 1;
            cost = 1.4f;
        }};

        uxerdItems.addAll(rawNethratium, rawElbium, rawElithite, aluminium, zinc, silver, lithium, titanium, cuberium);
        lumoniItems.addAll(copper, zinc, silver, diamond, sand, silicon, brass, vanadium, nickel, luminium);
    }
}
