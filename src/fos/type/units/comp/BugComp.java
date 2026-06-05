package fos.type.units.comp;

import arc.util.*;
import arc.util.io.*;
import fos.core.FOSVars;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.io.TypeIO;
import mindustry.type.UnitType;

import static ent.anno.Annotations.*;
import static mindustry.Vars.world;

@EntityComponent @SuppressWarnings("unused")
abstract class BugComp implements Unitc {
    /** A unit that this bug follows. */
    transient Unit following;
    /** Whether this bug is followed by others. */
    transient boolean isFollowed = false;
    /** Whether the swarm is currently invading enemy factories. */
    transient boolean invading = false;
    /** Whether it is supposed to stand still at the moment. */
    transient boolean idle = false;

    @Import transient UnitType type;
    @Import transient float x, y, rotation;
    @Import transient Team team;

    @Override
    @Replace
    public boolean isPathImpassable(int x, int y) {
        return !type.flying && world.tiles.in(x, y) && type.pathCost.getCost(team().id, FOSVars.pathfinder.get(x, y)) == -1;
    }

    @Override
    public void update() {
        if (!(self() instanceof Crawlc)) return;

        Tmp.v1.set(4, 0).rotate(rotation);

        Units.nearbyEnemies(team, x + Tmp.v1.x, y + Tmp.v1.y, type.hitSize / 1.85f, other -> {
            if (!other.isGrounded()) return;
            other.damageContinuousPierce(type.crushDamage);
        });
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
