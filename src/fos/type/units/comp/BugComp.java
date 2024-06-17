package fos.type.units.comp;

import arc.util.io.*;
import fos.core.FOSVars;
import mindustry.gen.*;
import mindustry.io.TypeIO;
import mindustry.type.UnitType;

import static ent.anno.Annotations.*;
import static mindustry.Vars.world;

@EntityComponent
abstract class BugComp implements Unitc {
    /** A unit that this bug follows. */
    transient Unit following;
    /** Whether this bug is followed by others. */
    transient boolean isFollowed = false;
    /** Whether the swarm is currently invading enemy factories. */
    transient boolean invading = false;
    /** Whether it is supposed to stand still at the moment. */
    transient boolean idle = false;
    @Import
    transient UnitType type;

    @Override
    @Replace
    public boolean isPathImpassable(int x, int y) {
        return !type.flying && world.tiles.in(x, y) && type.pathCost.getCost(team().id, FOSVars.pathfinder.get(x, y)) == -1;
    }

    @Override
    public void write(Writes write) {
        TypeIO.writeUnit(write, following);
        write.bool(isFollowed);
        write.bool(invading);
        write.bool(idle);
    }

    @Override
    public void read(Reads read) {
        following = TypeIO.readUnit(read);
        isFollowed = read.bool();
        invading = read.bool();
        idle = read.bool();
    }
}
