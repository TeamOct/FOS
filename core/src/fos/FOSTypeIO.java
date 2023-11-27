package fos;

import arc.util.Log;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import fos.type.content.ModuleWeapon;
import fos.type.content.WeaponModule;
import mindustry.annotations.Annotations;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.gen.Weaponsc;
import mindustry.io.TypeIO;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

import java.io.IOException;

@Annotations.TypeIOHandler
public class FOSTypeIO extends TypeIO {
    public static void writeMounts2(Writes writes, Weaponsc unit) {
        writes.i(unit.mounts().length);

        Log.info(0);
        for (WeaponMount mount : unit.mounts()) {
            Weapon weapon = mount.weapon;
            if (mount.weapon instanceof ModuleWeapon mw) {
                writes.bool(true);
                writes.i(mw.module.id);
            } else {
                writes.bool(false);
                writes.str(weapon.name);
            }
        }

        TypeIO.writeMounts(writes, unit.mounts());
    }

    public static WeaponMount[] readMounts2(Reads reads, UnitType type) {
        WeaponMount[] mounts = new WeaponMount[reads.i()];

        Log.info(0);
        for (int i = 0; i < mounts.length; i++) {
            WeaponMount mount = null;
            if (reads.bool()) {
                WeaponModule module = WeaponModule.modules.get(reads.i());
                mount = new WeaponMount(module.weapon);
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
