package fos.ai;

import arc.math.Mathf;
import fos.type.units.BugUnit;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.ai.types.GroundAI;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.world.Tile;

import static mindustry.Vars.pathfinder;

public class BugAI extends GroundAI {
    public BugUnit bug;
    @Override
    public void updateUnit() {
        bug = (BugUnit) unit;

        if (bug.isFollowed) {
            int followers = Units.count(unit.x, unit.y, 60f, u -> u instanceof BugUnit);

            if (followers >= 10 + Mathf.floor(Vars.state.wave / 10f)) {
                bug.invading = true;
            }
        } else {
            //check for bug swarms nearby
            bug.following = Units.closest(unit.team, unit.x, unit.y, u ->
                u instanceof BugUnit b && b.isFollowed);

            //become a swarm leader if none exist
            if (bug.following == null) bug.isFollowed = true;
        }

        super.updateUnit();
    }

    @Override
    public void updateMovement() {
        if (bug.invading) {
            super.updateMovement();
        } else if (bug.following != null) {
            bug.invading = ((BugUnit) bug.following).invading;

            pathfindUnit(bug.following);
        }
    }

    public void pathfindUnit(Teamc target) {
        int costType = unit.pathType();

        Tile tile = target.tileOn();
        if(tile == null) return;
        Tile targetTile = pathfinder.getTargetTile(tile, pathfinder.getField(unit.team, costType, Pathfinder.fieldCore));

        if(tile == targetTile) return;

        unit.movePref(vec.trns(unit.angleTo(targetTile.worldx(), targetTile.worldy()), unit.speed()));
    }
}
