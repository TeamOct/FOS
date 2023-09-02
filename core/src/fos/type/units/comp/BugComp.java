package fos.type.units.comp;

import mindustry.annotations.Annotations;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;

@Annotations.Component
public abstract class BugComp implements Unitc {
    /** A unit that this bug follows. */
    transient Unit following;
    /** Whether this bug is followed by others. */
    transient boolean isFollowed = false;
    /** Whether the swarm is currently invading enemy factories. */
    transient boolean invading = false;
}
