package fos.ai;

import fos.content.FOSCommands;
import fos.type.units.constructors.SubmarineUnit;
import mindustry.entities.units.AIController;

/**
 * This literally exists just for {@link FOSCommands}.
 * Only works with {@link SubmarineUnit}!
 * @author Slotterleet
 */
public class SubDiveAI extends AIController {
    @Override
    public void init() {
        if (unit instanceof SubmarineUnit sub && !sub.subSolid(sub.tileX(), sub.tileY())) {
            sub.submerged = !sub.submerged;
        }
    }
}
