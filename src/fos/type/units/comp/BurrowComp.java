package fos.type.units.comp;

import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import fos.content.FOSFx;
import mindustry.Vars;
import mindustry.async.PhysicsProcess;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;

import static ent.anno.Annotations.*;
import static mindustry.async.PhysicsProcess.PhysicRef;
import static mindustry.async.PhysicsProcess.PhysicsWorld.PhysicsBody;

@EntityComponent @SuppressWarnings("unused")
abstract class BurrowComp implements Syncc, Unitc {
    @Import float x, y, elevation, rotation;
    //@Import PhysicRef physref;
    @Import boolean hasPhysicsRef;
    @Import WeaponMount[] mounts;
    transient boolean burrowed = false, isBurrowing = false;
    transient float timer = 0f;

    @Override
    @Replace
    public boolean isGrounded() {
        return elevation <= 0.001f && !burrowed;
    }

/* don't do this for now
    @Override
    @Replace(101)
    public int pathType() {
        return FOSPathfinder.costBurrowing;
    }
*/

    @Override
    @Replace(100)
    public boolean collides(Hitboxc other) {
        return hittable() && !burrowed;
    }

    @Override
    @MethodPriority(100)
    public void update() {
        timer -= Time.delta;

        if (timer > 0) {
            for (WeaponMount mount : mounts) {
                mount.charge = (timer % 20f) / 20f;
            }
        }

        if ((isBurrowing && timer <= 0f)) {
            if (!isValid()) return;

            Damage.damage(team(), x, y, type().hitSize / 4, 2000);
            burrowed = !burrowed;
            isBurrowing = false;

            changeHitbox();
        }
    }

    void changeHitbox() {
        Seq<PhysicRef> refs = Reflect.get(Vars.unitPhysics, "refs");

        if (burrowed) {
            // temporarily yeet the hitbox
            var old = refs.find(r -> r.entity == self());
            old.body.radius = 0;
        } else {
            // when digging up, remove empty hitbox
            refs.remove(r -> r.entity == self());

            // then reset everything
            hasPhysicsRef = false;
            Vars.unitPhysics.add(self());
        }
    }

    @SuppressWarnings("unused")
    void burrow() {
        if (isBurrowing) return;

        apply(StatusEffects.unmoving, 120f);
        apply(StatusEffects.disarmed, 120f);
        isBurrowing = true;
        timer = 120f;

        Tmp.v1.trns(rotation, 16f);
        FOSFx.burrowDust.at(x + Tmp.v1.x, y + Tmp.v1.y, tileOn().floor().mapColor);
        FOSFx.burrowDust.at(x - Tmp.v1.x, y - Tmp.v1.y, tileOn().floor().mapColor);
    }

    @Override
    public void write(Writes write) {
        write.bool(burrowed);
    }

    @Override
    public void read(Reads read) {
        burrowed = read.bool();
    }
}
