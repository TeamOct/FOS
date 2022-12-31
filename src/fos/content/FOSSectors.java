package fos.content;

import mindustry.content.*;
import mindustry.type.*;

public class FOSSectors {
    public static SectorPreset
    /* Serpulo */ siloTerminal,
    /* Lumina */ beginning, island;

    public static void load() {
        siloTerminal = new SectorPreset("siloTerminal", Planets.serpulo, 95){{
            difficulty = 7;
        }};

        beginning = new SectorPreset("beginning-remake", FOSPlanets.lumoni, 9){{
            captureWave = 10;
            difficulty = 2;
        }};
        island = new SectorPreset("island", FOSPlanets.lumoni, 27){{
            captureWave = 25;
            difficulty = 4;
        }};
    }
}
