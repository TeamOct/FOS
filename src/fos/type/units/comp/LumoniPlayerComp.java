package fos.type.units.comp;

import arc.util.io.*;
import fos.io.FOSTypeIO;
import fos.type.WeaponSet;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.type.UnitType;

import static ent.anno.Annotations.*;

@EntityComponent @SuppressWarnings("unused")
abstract class LumoniPlayerComp implements Weaponsc, Entityc, Syncc, Unitc {
    transient boolean isEditedWeapons = false;
    transient WeaponSet weaponSet = null;
    @Import WeaponMount[] mounts;

    @Replace
    @Override
    public void setType(UnitType unitType) {
        type(unitType);
        maxHealth(type().health);
        drag(type().drag);
        armor(type().armor);
        hitSize(type().hitSize);
        hovering(type().hovering);
        
        if (controller() == null) controller(type().createController(self()));

        if (!isEditedWeapons) {
            if (mounts().length != type().weapons.size) setupWeapons(type());
            if (abilities().length != type().abilities.size) {
                abilities(new Ability[type().abilities.size]);
                for (int i = 0; i < type().abilities.size; i++) {
                    abilities()[i] = type().abilities.get(i).copy();
                }
            }
        }
    }

    @Override
    public void write(Writes write) {
        write.bool(isEditedWeapons);
        write.i(weaponSet == null ? -1 : weaponSet.id);
        FOSTypeIO.writeMounts2(write, this);
    }

    @Override
    public void read(Reads read) {
        isEditedWeapons = read.bool();
        int weaponSetId = read.i();
        weaponSet = weaponSetId == -1 ? null : WeaponSet.sets.get(weaponSetId);
        mounts = FOSTypeIO.readMounts2(read, this);
    }

    @Override
    public void readSync(Reads read) {
        isEditedWeapons = read.bool();
    }

    @Override
    public void writeSync(Writes write) {
        write.bool(isEditedWeapons);
    }
}
