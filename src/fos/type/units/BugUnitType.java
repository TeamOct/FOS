package fos.type.units;

import fos.ai.BugAI;
import fos.ai.FlyingBugAI;
import mindustry.type.UnitType;
import mindustry.world.meta.BlockFlag;

/** Just a template for bugs. */
public class BugUnitType extends UnitType {
    public BugUnitType(String name, boolean flying) {
        super(name);
        omniMovement = flying;
        constructor = flying ? BugFlyingUnit::create : BugUnit::create;
        this.flying = flying;
        targetAir = flying;
        targetGround = true;
        targetFlags = new BlockFlag[]{BlockFlag.drill, BlockFlag.factory, BlockFlag.core, null};
        controller = u -> flying ? new FlyingBugAI() : new BugAI();
    }
    public BugUnitType(String name, boolean flying, boolean melee) {
        this(name, flying);
        if (melee) this.range = 0.01f;
    }
}
