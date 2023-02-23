package fos.content;

import arc.Core;
import fos.type.units.BossUnitType;

import static mindustry.game.Objectives.*;

public class FOSObjectives {
    public static class DefeatBoss implements Objective {
        public BossUnitType type;

        public DefeatBoss(BossUnitType type) {
            this.type = type;
        }

        @Override
        public boolean complete() {
            return type.content.unlocked();
        }

        @Override
        public String display() {
            return Core.bundle.format("requirement.defeatboss", type);
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
}
