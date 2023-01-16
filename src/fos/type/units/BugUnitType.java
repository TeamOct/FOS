package fos.type.units;

import fos.ai.BugAI;
import mindustry.type.UnitType;
import mindustry.world.meta.BlockFlag;

/** Just a template for bugs. */
public class BugUnitType extends UnitType {
    public BugUnitType(String name) {
        super(name);
        constructor = BugUnit::create;
        targetFlags = new BlockFlag[]{BlockFlag.drill, BlockFlag.factory, null};
        controller = u -> new BugAI();
    }
}
