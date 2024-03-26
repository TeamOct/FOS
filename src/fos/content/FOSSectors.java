package fos.content;

import mindustry.type.SectorPreset;

public class FOSSectors {
    public static SectorPreset
    /* Serpulo */ siloTerminal,
    /* Lumoni */ crashLanding, ruins, intruders, citadel;

    public static void load() {
/*
        //TODO: probably remove and/or move to lumoni
        siloTerminal = new SectorPreset("siloTerminal", Planets.serpulo, 95){{
            difficulty = 7;
        }};
*/

        crashLanding = new SectorPreset("crash-landing", FOSPlanets.lumoni, 89){{
            alwaysUnlocked = true;
            difficulty = 1;
            captureWave = 2;
        }};

        ruins = new SectorPreset("ruins", FOSPlanets.lumoni, 26){{
            difficulty = 2;
            captureWave = 2;
        }};

        intruders = new SectorPreset("intruders", FOSPlanets.lumoni, 67){{
            difficulty = 5;
            captureWave = 11;
        }};

        citadel = new SectorPreset("citadel", FOSPlanets.lumoni, 44){{
            difficulty = 4;
            captureWave = 21;
        }};
    }
}
