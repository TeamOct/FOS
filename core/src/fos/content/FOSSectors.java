package fos.content;

import mindustry.type.SectorPreset;

public class FOSSectors {
    public static SectorPreset
    /* Serpulo */ siloTerminal,
    /* Lumoni */ crashLanding;

    public static void load() {
/*
        //TODO: probably remove and/or move to lumoni
        siloTerminal = new SectorPreset("siloTerminal", Planets.serpulo, 95){{
            difficulty = 7;
        }};
*/

        crashLanding = new SectorPreset("crashLanding", FOSPlanets.lumoni, 89){{
            alwaysUnlocked = true;
            difficulty = 1;
            captureWave = 2;
        }};
    }
}
