package fos.type.units;

import mindustry.gen.CrawlUnit;
import mindustry.gen.Unit;

public class BugUnit extends CrawlUnit {
    /** A unit that this bug follows. */
    public Unit following;
    /** Whether this bug is followed by others. */
    public boolean isFollowed;
    /** Whether the swarm is currently invading enemy factories. */
    public boolean invading;

    protected BugUnit() {
        super();
    }

    public static BugUnit create() {
        return new BugUnit();
    }
}
