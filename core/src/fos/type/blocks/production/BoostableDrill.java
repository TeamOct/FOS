package fos.type.blocks.production;

import arc.Core;
import mindustry.world.blocks.production.Drill;

/** A class for this mod's drills in particular, affected by endless researches. */
public class BoostableDrill extends Drill {
    public BoostableDrill(String name) {
        super(name);
    }

    @SuppressWarnings("unused")
    public class BoostableDrillBuild extends DrillBuild {
        @Override
        public float efficiencyScale() {
            return Core.settings.getBool("fos-endless-enabled", true) ? 1f + Core.settings.getInt("fos-drill-speed-research") * 0.01f : 1f;
        }
    }
}
