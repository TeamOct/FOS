package fos.ai;

import fos.content.FOSCommands;
import fos.gen.Submarine;
import fos.gen.Submarinec;
import mindustry.entities.units.AIController;

/**
 * This literally exists just for {@link FOSCommands}.
 * Only works with {@link SubmarineUnit}!
 * @author Slotterleet
 */
public class SubDiveAI extends AIController {
    @Override
    public void init() {
        if (unit instanceof Submarinec sub && !sub.subSolid(sub.tileX(), sub.tileY())) {
            sub.submerged(!sub.submerged());
            sub.elevation(sub.submerged() ? 0f : 0.005f);
        }
    }
}
