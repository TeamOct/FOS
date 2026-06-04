package fos.content;

import arc.Core;
import fos.type.units.types.BossUnitType;
import mindustry.type.UnitType;

import static arc.Core.settings;
import static mindustry.game.Objectives.Objective;

public class FOSObjectives {
    public static class DefeatBoss implements Objective {
        public BossUnitType type;

        public DefeatBoss(UnitType type) {
            this.type = (BossUnitType)type;
        }

        @Override
        public boolean complete() {
            return settings.getBool(type.name + "-defeated");
        }

        @Override
        public String display() {
            return Core.bundle.format("requirement.defeatboss", type.localizedName);
        }
    }

    public static class DefeatBugs implements Objective {
        public UnitType type;
        public int amount;

        public DefeatBugs(UnitType type, int amount) {
            this.type = type;
            this.amount = amount;
        }

        @Override
        public boolean complete() {
            return settings.getInt(type.name + "-count", 0) >= amount;
        }

        @Override
        public String display() {
            return amount == 1 ? Core.bundle.format("requirement.defeatbug", type.localizedName) :
                Core.bundle.format("requirement.defeatbugs",
                type.localizedName, settings.getInt(type.name + "-count", 0), amount);
        }
    }

    public static class FindInCrates implements Objective {
        public FindInCrates(){}

        @Override
        public boolean complete() {
            return false;
        }

        @Override
        public String display() {
            return Core.bundle.format("requirement.findincrates");
        }
    }

    public static class TBDObjective implements Objective {
        public TBDObjective(){}

        @Override
        public boolean complete() {
            return false;
        }

        @Override
        public String display() {
            return Core.bundle.format("requirement.tbd");
        }
    }
}
