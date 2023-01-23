package fos.type.units;

import arc.math.geom.Vec2;
import arc.util.io.Reads;
import arc.util.io.Writes;
import fos.FOSVars;
import mindustry.gen.CrawlUnit;
import mindustry.gen.Unit;

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
