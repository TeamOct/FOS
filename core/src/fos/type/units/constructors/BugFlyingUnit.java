package fos.type.units.constructors;

import arc.util.io.*;
import fos.FOSVars;
import mindustry.gen.*;

public class BugFlyingUnit extends UnitEntity {
    /** A unit that this bug follows. */
    public Unit following;
    /** Whether this bug is followed by others. */
    public boolean isFollowed;
    /** Whether the swarm is currently invading enemy factories. */
    public boolean invading;

    public BugFlyingUnit() {
        super();
        following = null;
        isFollowed = false;
        invading = false;
    }

    public static BugFlyingUnit create() {
        return new BugFlyingUnit();
    }

    @Override
    public int classId() {
        return FOSVars.bugFlyingEntity;
    }

    @Override
    public void write(Writes write) {
        super.write(write);

        write.bool(isFollowed);
        write.bool(invading);
    }

    @Override
    public void read(Reads read) {
        super.read(read);

        isFollowed = read.bool();
        invading = read.bool();
    }
}
