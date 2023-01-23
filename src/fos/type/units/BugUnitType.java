package fos.type.units;

import fos.ai.BugAI;
import mindustry.type.UnitType;
import mindustry.world.meta.BlockFlag;

/** Just a template for bugs. */
public class BugUnitType extends UnitType {
    public BugUnitType(String name) {
        super(name);
        omniMovement = false;
        crushDamage = 1f;
        circleTarget = true;
        constructor = BugUnit::create;
        targetAir = false;
        targetGround = true;
        targetFlags = new BlockFlag[]{BlockFlag.drill, BlockFlag.factory, BlockFlag.core, null};
        controller = u -> new BugAI();
    }
}
