package fos.ai;

import fos.core.FOSVars;
import fos.gen.Burrowc;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;
import mindustry.world.Tile;

import static fos.ai.FOSPathfinder.*;

/**
 * An interface for {@link AIController} that utilizes a modified pathfinder.
 * @author Slotterleet
 */
public interface FOSPathfindAI {
    /** @return next tile to travel to */
    default Tile pathfind(Unit unit) {
        return pathfind(unit, 0);
    }

    default Tile pathfind(Unit unit, int pathType) {
        Tile tile = unit.tileOn();
        FOSFlowfield ff = FOSVars.pathfinder.getField(unit.team, pathType, unit instanceof Burrowc ? fieldBurrowing : fieldBug);
        return FOSVars.pathfinder.getTargetTile(tile, ff);
    }
}
