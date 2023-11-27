package fos.type.units.comp;

import arc.util.io.*;
import fos.FOSTypeIO;
import mindustry.annotations.Annotations;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.io.TypeIO;

// FIXME mounts
@Annotations.Component
public abstract class LumoniPlayerUnitComp implements Weaponsc, Entityc, Syncc {
    @Annotations.Import WeaponMount[] mounts;

    @Override
    public void write(Writes write) {
        TypeIO.writeMounts(write, mounts);
    }

    @Override
    public void read(Reads read) {
        mounts = FOSTypeIO.readMounts(read);
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
