package fos;

import arc.util.Log;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import fos.gen.LumoniPlayerUnitc;
import fos.type.content.WeaponSet;
import mindustry.annotations.Annotations;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Weaponsc;
import mindustry.io.TypeIO;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

import java.io.IOException;

@Annotations.TypeIOHandler
public class FOSTypeIO extends TypeIO {
    public static void writeMounts2(Writes writes, Weaponsc unit) {
        writes.i(unit.mounts().length);

        for (WeaponMount mount : unit.mounts()) {
            Weapon weapon = mount.weapon;
            if (unit instanceof LumoniPlayerUnitc lpu && lpu.isEditedWeapons()) {
                writes.bool(true);

                writes.i(lpu.weaponSet().id);
                writes.i(lpu.weaponSet().weapons.indexOf(mount.weapon));
            } else {
                writes.bool(false);
                writes.str(weapon.name);
            }
        }

        TypeIO.writeMounts(writes, unit.mounts());
    }

    public static WeaponMount[] readMounts2(Reads reads, UnitType type) {
        WeaponMount[] mounts = new WeaponMount[reads.i()];

        for (int i = 0; i < mounts.length; i++) {
            WeaponMount mount = null;
            if (reads.bool()) {
                WeaponSet set = WeaponSet.sets.get(reads.i());
                mount = set.getMount(reads.i());
            } else {
                String name = reads.str();
                for (Weapon weapon : type.weapons) {
                    if (weapon.name.equals(name))
                        mount = new WeaponMount(weapon);
                }

                if (mount == null) {
                    Log.err(new IOException(Strings.format("Weapon with name %s not founded in %s weapons list.",
                            name, type)));
                }
            }
            mounts[i] = mount;
        }

        TypeIO.readMounts(reads, mounts);

        return mounts;
    }
}
