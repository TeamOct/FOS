package fos.content;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class FOSFluids {
    public static Liquid oxygen, tokicite, bugAcid;

    public static void load() {
        oxygen = new Liquid("oxygen", Color.valueOf("ffbdd4")){{
            gas = true;
            flammability = 0.8f;
        }};
        tokicite = new Liquid("tokicite", Color.valueOf("d16792")){{
            viscosity = 0.95f;
            heatCapacity = 0.65f;
            temperature = 0.4f;
            effect = FOSStatuses.tokiciteSlowed;
            boilPoint = 0.8f;
        }};
        bugAcid = new Liquid("bug-acid", Color.valueOf("6abe30")){{
            viscosity = 0.4f;
            heatCapacity = 0.3f;
            effect = FOSStatuses.dissolving;
            hidden = true;
        }};
    }
}
