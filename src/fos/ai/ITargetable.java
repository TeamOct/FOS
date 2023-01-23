package fos.ai;

import arc.math.geom.Vec2;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.world.Tile;

import static mindustry.Vars.pathfinder;
import static mindustry.ai.Pathfinder.*;

/**
 * An interface for {@link mindustry.entities.units.AIController} that supports custom targeting options.
 * @author Slotterleet
 */
public interface ITargetable {
    /** @return next tile to travel to */
    default Tile pathfindTarget(Teamc target, Unit unit) {
        Tile tile = unit.tileOn();
        //TODO mod conflict is, even though VERY unlikely, still possible
        PositionTarget ff = (PositionTarget) pathfinder.getField(unit.team, unit.pathType(), 1);
        ((Vec2) ff.position).set(target.x(), target.y());
        return pathfinder.getTargetTile(tile, ff);
    }
}
