package fos.type.units.comp;

import arc.util.Time;
import arc.util.io.*;
import fos.ai.FOSPathfinder;
import fos.content.FOSFx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.gen.*;

import static ent.anno.Annotations.*;

@EntityComponent
abstract class BurrowComp implements Syncc, Unitc {
    @Import float x, y, elevation;
    transient boolean burrowed = false, isBurrowing = false;

    @Override
    @Replace
    public boolean isGrounded() {
        return elevation <= 0.01f && !burrowed;
    }

    @Override
    @Replace(101)
    public int pathType() {
        return FOSPathfinder.costBurrowing;
    }

    @Override
    public void write(Writes write) {
        write.bool(burrowed);
    }

    @Override
    public void read(Reads read) {
        burrowed = read.bool();
    }

    @SuppressWarnings("unused")
    void burrow() {
        if (isBurrowing) return;

        apply(StatusEffects.unmoving, 120f);
        apply(StatusEffects.disarmed, 120f);
        isBurrowing = true;
        FOSFx.burrowDust.at(x, y, tileOn().floor().mapColor);
        Time.run(120f, () -> {
            Damage.damage(team(), x, y, type().hitSize / 4, 2000);
            burrowed = !burrowed;
            isBurrowing = false;
        });
    }
}
