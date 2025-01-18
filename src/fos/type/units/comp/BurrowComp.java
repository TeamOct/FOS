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
    @Import PhysicRef physref;
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

        if (burrowed && physref.body.radius > 0)
            changeHitbox();

        if ((isBurrowing && timer <= 0f)) {
            if (!isValid()) return;

            Damage.damage(team(), x, y, type().hitSize / 4, 2000);
            burrowed = !burrowed;
            isBurrowing = false;

            changeHitbox();
        }
    }

    void changeHitbox() {
        float x = this.x, y = this.y;

        PhysicsProcess.PhysicsWorld physics = Reflect.get(Vars.asyncCore.processes.get(0), "physics");
        Seq<PhysicRef> refs = Reflect.get(Vars.asyncCore.processes.get(0), "refs");

        if (burrowed) {
            // create the new empty "hitbox"
            PhysicsBody body = new PhysicsBody();
            body.x = x;
            body.y = y;
            body.mass = mass();
            body.radius = -1;

            PhysicRef ref = new PhysicRef(this, body);

            // remove the existing one from physics process
            physics.remove(physref.body);
            refs.remove(physref);

            // and put the empty one at its place
            refs.add(ref);
            physref = ref;
            physics.add(body);

            ref.body.layer = 1; // PhysicsProcess.layerLegs
            ref.x = x;
            ref.y = y;
            ref.body.local = !Vars.net.client() || isLocal();
        } else {
            // when digging up, remove the empty box from physics process
            physics.remove(physref.body);
            refs.remove(physref);

            // then reset everything
            physref = null;
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
