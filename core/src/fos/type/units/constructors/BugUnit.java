package fos.type.units.constructors;

import arc.util.io.*;
import fos.core.FOSVars;
import mindustry.gen.*;

public class BugUnit extends CrawlUnit {
    /** A unit that this bug follows. */
    public Unit following;
    /** Whether this bug is followed by others. */
    public boolean isFollowed;
    /** Whether the swarm is currently invading enemy factories. */
    public boolean invading;

    public BugUnit() {
        super();
        following = null;
        isFollowed = false;
        invading = false;
    }

    public static BugUnit create() {
        return new BugUnit();
    }

    @Override
    public int classId() {
        return FOSVars.bugEntity;
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
