package fos.content;

import arc.Core;
import fos.graphics.FOSPal;
import fos.type.statuses.HackedEffect;
import mindustry.gen.Unit;
import mindustry.type.*;

public class FOSStatuses {
    public static StatusEffect hacked,
    /** These "statuses" do nothing and only serve as unlocks for endless tech nodes. */
    drillSpeedEndless;

    public static void load() {
        hacked = new HackedEffect("hacked"){{
            color = FOSPal.hackedBack;
        }};

        drillSpeedEndless = new EndlessResearchStatusEffect("drill-speed"){{
            researchCost = ItemStack.with(FOSItems.tin, 100);
        }};
    }

    /** An internal class used for endless researches. Extends StatusEffect just because. */
    public static class EndlessResearchStatusEffect extends StatusEffect {
        /** Items required for research. */
        public ItemStack[] researchCost;
        public EndlessResearchStatusEffect(String name) {
            super(name);
            show = false;
        }

        @Override
        public void applied(Unit unit, float time, boolean extend) {
            //no
            unit.unapply(this);
        }

        @Override
        public String displayDescription() {
            return description + "\n" + Core.bundle.format("status." + name + ".researches", Core.settings.getInt(name + "-research", 0))
                + "\n" + Core.bundle.format("mod.display", minfo.mod.meta.displayName());
        }

        @Override
        public ItemStack[] researchRequirements() {
            return researchCost;
        }

        @Override
        public boolean unlocked() {
            //it's endless, and not supposed to apply to custom games.
            return false;
        }

        @Override
        public void onUnlock() {
            //lock it again, for endless research
            clearUnlock();

            //reset node requirements
            techNode.reset();

            //increment to total amount of researches
            int n = Core.settings.getInt(name + "-research", 0);
            Core.settings.put(name + "-research", ++n);
        }
    }
}
