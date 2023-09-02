package fos.type.units.comp;

import mindustry.annotations.Annotations;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;

// FIXME mounts
@Annotations.Component
public abstract class LumoniPlayerUnitComp implements Weaponsc, Entityc, Syncc {
    @Annotations.Import WeaponMount[] mounts;
    WeaponMount[] syncMounts;

    @Override
    public void afterRead() {
        mounts = syncMounts;
    }

    @Override
    public void afterSync() {
        mounts = syncMounts;
    }
}
