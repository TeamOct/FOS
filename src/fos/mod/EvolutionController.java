package fos.mod;

import arc.Events;
import arc.struct.ObjectFloatMap;
import arc.util.Time;
import fos.content.*;
import fos.core.FOSVars;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.type.*;
import mindustry.world.blocks.production.Drill.DrillBuild;
import mindustry.world.blocks.production.GenericCrafter.GenericCrafterBuild;

import static mindustry.Vars.*;

public class EvolutionController {
    // TODO: balance (probably)
    /** Internal evolution factor: waves. */
    protected float waveCoefficient = 0.01f;
    /** Internal evolution factor: factories and drills. */
    protected float factoryCoefficient = 0.001f;

    /** External evolution factor: captured sectors. */
    protected float sectorCoefficient = 0.01f;
    /** External evolution factor: researched content. */
    protected float researchCoefficient = 0.0025f;

    protected ObjectFloatMap<SectorPreset> sectorBonuses = new ObjectFloatMap<>();

    public EvolutionController() {
        Events.on(EventType.ContentInitEvent.class, e -> {
            sectorBonuses.put(FOSSectors.awakening, 0.05f); // to ensure that insects always attack
        });
    }

    public float getWaveEvo() {
        return state.rules.waves ? state.wave * waveCoefficient : (float)state.tick / 2 / Time.toMinutes * waveCoefficient;
    }

    public float getFactoryEvo() {
        return Groups.build.count(b -> b instanceof GenericCrafterBuild || b instanceof DrillBuild) * factoryCoefficient;
    }

    public float getSectorEvo() {
        return !state.isCampaign() ? 0 :
            FOSPlanets.lumoni.sectors.count(Sector::isCaptured) * sectorCoefficient;
    }

    public float getResearchEvo() {
        if (!state.isCampaign()) return 0;

        // JAVA SUCKS.
        final int[] counter = {0};
        content.each(c -> {
            if (c.isVanilla() || c.minfo.mod != FOSVars.mod) return;

            if (c instanceof UnlockableContent u && u.unlockedNow())
                counter[0]++;
        });

        return counter[0] * researchCoefficient;
    }

    public float getTotalEvo() {
        return getWaveEvo() + getFactoryEvo() + getSectorEvo() + getResearchEvo() +
            (state.isCampaign() ? sectorBonuses.get(state.rules.sector.preset, 0) : 0);
    }
}
