package fos.io;

import arc.util.*;
import arc.util.io.*;
import fos.gen.LumoniPlayerc;
import fos.type.abilities.FOSAbility;
import fos.type.content.WeaponSet;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unitc;
import mindustry.io.TypeIO;
import mindustry.type.Weapon;

import java.io.IOException;

public class FOSTypeIO extends TypeIO {
    public static void writeMounts2(Writes writes, Unitc unit) {
        writes.i(unit.mounts().length);

        for (WeaponMount mount : unit.mounts()) {
            Weapon weapon = mount.weapon;
            if (unit instanceof LumoniPlayerc lpu && lpu.isEditedWeapons()) {
                writes.bool(true);

                writes.i(lpu.weaponSet().id);
                writes.i(lpu.weaponSet().weapons.indexOf(mount.weapon));

                // write WeaponSet's abilities
                for (Ability ability : lpu.weaponSet().abilities) {
                    if (ability instanceof FOSAbility a)
                        a.write(writes, unit);
                    else
                        writes.f(ability.data);
                }

            } else {
                writes.bool(false);
                writes.str(weapon.name);
            }
        }

        TypeIO.writeMounts(writes, unit.mounts());
    }

    public static WeaponMount[] readMounts2(Reads reads, Unitc unit) {
        WeaponMount[] mounts = new WeaponMount[reads.i()];

        for (int i = 0; i < mounts.length; i++) {
            WeaponMount mount = null;
            if (reads.bool()) {
                WeaponSet set = WeaponSet.sets.get(reads.i());
                mount = set.getMount(reads.i());

                unit.abilities(set.abilities.toArray(Ability.class));

                // read WeaponSet's abilities
                for (Ability ability : set.abilities) {
                    if (ability instanceof FOSAbility a)
                        a.read(reads, unit);
                    else
                        ability.data = reads.f();
                }
            } else {
                String name = reads.str();
                for (Weapon weapon : unit.type().weapons) {
                    if (weapon.name.equals(name))
                        mount = new WeaponMount(weapon);
                }

                if (mount == null) {
                    Log.err(new IOException(Strings.format("Weapon with name %s not found in %s weapons list.",
                            name, unit.type())));
                }
            }
            mounts[i] = mount;
        }

        TypeIO.readMounts(reads, mounts);

        return mounts;
    }
}
