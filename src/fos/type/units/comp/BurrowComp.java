package fos.type.units.comp;

import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.entities.EntityCollisions;
import mindustry.game.Team;
import mindustry.gen.Unitc;

import static ent.anno.Annotations.*;

@EntityComponent
abstract class BurrowComp implements Unitc {
    @Import float elevation;
    transient boolean burrowed = false;

    @Override
    public EntityCollisions.SolidPred solidity() {
        return burrowed ? null : EntityCollisions::legsSolid;
    }

    @Override
    @Replace
    public boolean isGrounded() {
        return elevation <= 0.01f && elevation != 0f && !burrowed;
    }

    public void burrow() {
        apply(StatusEffects.unmoving, 120f);
        // TODO: dust effect
        Time.run(120f, () ->
            burrowed = !burrowed
        );
    }

    @Override
    @Replace
    public boolean targetable(Team team) {
        return !burrowed;
    }
}
