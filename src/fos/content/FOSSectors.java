package fos.content;

import fos.type.FOSSectorPreset;
import mindustry.type.SectorPreset;

import static fos.content.FOSPlanets.lumoni;

public class FOSSectors {
    public static SectorPreset
    /* Serpulo */ siloTerminal,
    /* Lumoni */ awakening, rwf, intruders, hostiles, citadel, zincMiningSite, conflict;

    public static void load() {
/*
        //TODO: probably remove and/or move to lumoni
        siloTerminal = new SectorPreset("siloTerminal", Planets.serpulo, 95){{
            difficulty = 7;
        }};
*/

        awakening = new FOSSectorPreset("awakening", lumoni, 89){{
            alwaysUnlocked = true;
            difficulty = 1;
            captureWave = 3;

            rules = r -> {
                r.coreCapture = true;
            };

            rulesOverride = r -> {
                r.fog = r.staticFog = true;
            };
        }};

        rwf = new SectorPreset("rwf", lumoni, 26){{
            difficulty = 3;
            captureWave = 11;
        }};

        intruders = new SectorPreset("intruders", lumoni, 67){{
            difficulty = 5;
            captureWave = 31;
        }};

        hostiles = new SectorPreset("hostiles", lumoni, 68){{
            difficulty = 8;
            captureWave = 16;
        }};

        citadel = new SectorPreset("citadel", lumoni, 44){{
            difficulty = 4;
            captureWave = 21;
        }};

        zincMiningSite = new SectorPreset("zinc-mining-site", lumoni, 71){{
            difficulty = 5;
        }};

        conflict = new SectorPreset("conflict", lumoni, 18){{
            difficulty = 6;
        }};
    }
}
