package fos.type.units.comp;

import arc.util.Log;
import mindustry.annotations.Annotations;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Entityc;
import mindustry.gen.Syncc;
import mindustry.gen.Weaponsc;

// FIXME mounts
@Annotations.Component
public abstract class LumoniPlayerUnitComp  implements Weaponsc, Entityc, Syncc {
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
