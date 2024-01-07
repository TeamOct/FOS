package fos.type.units.comp;

import arc.util.io.*;
import fos.FOSTypeIO;
import fos.type.content.WeaponSet;
import mindustry.annotations.Annotations;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.type.UnitType;

// FIXME mounts
@Annotations.Component
public abstract class LumoniPlayerUnitComp implements Weaponsc, Entityc, Syncc, Unitc {
    transient boolean isEditedWeapons = false;
    transient WeaponSet weaponSet = null;
    @Annotations.Import WeaponMount[] mounts;

    @Annotations.Replace
    @Override
    public void setType(UnitType unitType) { // created for deleting abilities and mounts replacement
        type(unitType);
        maxHealth(type().health);
        drag(type().drag);
        armor(type().armor);
        hitSize(type().hitSize);
        hovering(type().hovering);
        if (controller() == null) controller(type().createController(self()));
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
}
