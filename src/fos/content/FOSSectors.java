package fos.content;

import mindustry.content.*;
import mindustry.type.*;

public class FOSSectors {
    public static SectorPreset
    /* Serpulo */ siloTerminal,
    /* Lumina */ beginning;

    public static void load() {
        siloTerminal = new SectorPreset("siloTerminal", Planets.serpulo, 95){{
            difficulty = 7;
        }};

        beginning = new SectorPreset("beginning", FOSPlanets.lumina, 9){{
            captureWave = 2;
            difficulty = 2;
        }};
    }
}
