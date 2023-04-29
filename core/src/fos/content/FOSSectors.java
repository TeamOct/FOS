package fos.content;

import mindustry.content.Planets;
import mindustry.type.SectorPreset;

public class FOSSectors {
    public static SectorPreset
    /* Serpulo */ siloTerminal,
    /* Lumoni */ beginning;

    public static void load() {
        siloTerminal = new SectorPreset("siloTerminal", Planets.serpulo, 95){{
            difficulty = 7;
        }};

        //FIXME
        /*beginning = new SectorPreset("beginning", FOSPlanets.lumina, 9){{
            captureWave = 2;
            difficulty = 2;
        }};*/
    }
}
