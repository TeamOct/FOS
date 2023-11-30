package fos.type.units.comp;

import arc.util.io.*;
import mindustry.annotations.Annotations;
import mindustry.gen.*;
import mindustry.io.TypeIO;

@Annotations.Component
public abstract class BugComp implements Unitc {
    /** A unit that this bug follows. */
    transient Unit following;
    /** Whether this bug is followed by others. */
    transient boolean isFollowed = false;
    /** Whether the swarm is currently invading enemy factories. */
    transient boolean invading = false;
    /** Whether it is supposed to stand still at the moment. */
    transient boolean idle = false;

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
