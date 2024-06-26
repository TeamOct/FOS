package fos.ai;

import fos.core.FOSVars;
import fos.gen.Burrowc;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;
import mindustry.world.Tile;

import static fos.ai.FOSPathfinder.*;

/**
 * An interface for {@link AIController} that supports custom targeting options.
 * Utilizes a modified pathfinder.
 * @author Slotterleet
 */
public interface FOSPathfindAI {
    /** @return next tile to travel to */
    default Tile pathfind(Unit unit) {
        Tile tile = unit.tileOn();
        FOSFlowfield ff = FOSVars.pathfinder.getField(unit.team, unit.pathType(), (unit instanceof Burrowc ? fieldBurrowing : fieldPos));
        return FOSVars.pathfinder.getTargetTile(tile, ff);
    }
}
