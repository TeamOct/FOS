package fos.type.units.comp;

import arc.util.io.*;
import fos.FOSTypeIO;
import mindustry.annotations.Annotations;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;

// FIXME mounts
@Annotations.Component
public abstract class LumoniPlayerUnitComp implements Weaponsc, Entityc, Syncc, Unitc {
    @Annotations.Import WeaponMount[] mounts;

    @Override
    public void write(Writes write) {
        FOSTypeIO.writeMounts2(write, this);
    }

    @Override
    public void read(Reads read) {
        mounts = FOSTypeIO.readMounts2(read, type());
    }

/*
    @Override
    public void afterRead() {
        mounts = syncMounts;
    }

    @Override
    public void afterSync() {
        mounts = syncMounts;
    }
*/
}
