package fos.controllers;

import fos.content.FOSPlanets;
import fos.core.FOSVars;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Groups;
import mindustry.type.Sector;
import mindustry.world.blocks.production.Drill.DrillBuild;
import mindustry.world.blocks.production.GenericCrafter.GenericCrafterBuild;

public class EvolutionController {
    // TODO: balance (probably)
    /** Internal evolution factor: waves. */
    public float waveCoefficient = 0.01f;
    /** Internal evolution factor: factories and drills. */
    public float factoryCoefficient = 0.001f;

    /** External evolution factor: captured sectors. */
    public float sectorCoefficient = 0.01f;
    /** External evolution factor: researched content. */
    public float researchCoefficient = 0.005f;

    public float getWaveEvo() {
        return Vars.state.wave * waveCoefficient;
    }

    public float getFactoryEvo() {
        return Groups.build.count(b -> b instanceof GenericCrafterBuild || b instanceof DrillBuild) * factoryCoefficient;
    }

    public float getSectorEvo() {
        return FOSPlanets.lumoni.sectors.count(Sector::isCaptured) * sectorCoefficient;
    }

    public float getResearchEvo() {
        // JAVA SUCKS.
        final int[] counter = {0};
        Vars.content.each(c -> {
            if (c.isVanilla() || c.minfo.mod != FOSVars.mod) return;

            if (c instanceof UnlockableContent u && u.unlockedNow())
                counter[0]++;
        });

        return counter[0] * researchCoefficient;
    }

    public float getTotalEvo() {
        return getWaveEvo() + getFactoryEvo() + getSectorEvo() + getResearchEvo();
    }
}
