package fos.ai.bugs;

import arc.Events;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.*;
import fos.core.FOSVars;
import fos.mod.FOSEventTypes;
import mindustry.entities.units.AIController;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.Turret;

import static mindustry.Vars.*;

public class ScoutBugAI extends AIController {
    public Tile dest;
    public Seq<Building> foundTurrets = new Seq<>();
    public float lastCheckedTime = 0f;

    @Override
    public void updateMovement() {
        if (lastCheckedTime == 0f) lastCheckedTime = Time.time;

        if (dest == null) {
            int x = Mathf.random(world.width()-1);
            int y = Mathf.random(world.height()-1);
            dest = world.tile(x, y);
        }

        // pick an undiscovered tile, or go anywhere if failed to find one within 5 seconds
        if (dest == null || (isDiscovered(unit.team, dest.x, dest.y) && Time.time - lastCheckedTime < 300f) || unit.tileOn() == dest) {
            dest = null;
            return;
        }

        moveTo(dest, 4f);
        faceMovement();

        unit.tileOn().circle((int)unit.type.fogRadius, (x, y) -> {
            Tile tile = world.tile(x, y);

            if (tile == null || tile.build == null) return;
            if (tile.build instanceof Turret.TurretBuild t && t.team != unit.team && t.team != Team.derelict) {
                if (!foundTurrets.contains(t)) {
                    foundTurrets.add(t);
                    Events.fire(new FOSEventTypes.InsectDeathEvent(tile));
                }
            }
        });
    }

    // FogControl's isDiscovered() for some reason always returns true with AI teams.
    boolean isDiscovered(Team team, int x, int y) {
        // if bugs evolved for long enough then it's probably not a bad idea to re-scout previous areas.
        if (FOSVars.evoController.getTotalEvo() > 0.15f) return false;

        if(!state.rules.staticFog || !state.rules.fog || team == null) return true;

        var data = fogControl.getDiscovered(team);
        if (data == null) return false;

        int ww = Reflect.get(fogControl, "ww");
        int wh = Reflect.get(fogControl, "wh");
        if (x < 0 || y < 0 || x >= ww || y >= wh) return false;
        return data.get(x + y * ww);
    }

    @Override
    public boolean keepState() {
        return true;
    }
}
