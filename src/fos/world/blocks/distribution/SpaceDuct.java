package fos.world.blocks.distribution;

import arc.struct.*;
import fos.content.*;
import mindustry.entities.units.*;
import mindustry.input.*;
import mindustry.world.blocks.distribution.*;

public class SpaceDuct extends Duct {
    public SpaceDuct(String name) {
        super(name);
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){
        Placement.calculateBridges(plans, (DuctBridge) FOSBlocks.spaceBridge, false, b -> b instanceof Duct || b instanceof StackConveyor || b instanceof Conveyor);
    }
}
