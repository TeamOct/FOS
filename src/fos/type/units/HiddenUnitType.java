package fos.type.units;

import fos.type.content.*;
import mindustry.Vars;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;

//I had to create this because the WeaponModules' weapons didn't load properly for some bizarre reason
public class HiddenUnitType extends UnitType {
    public HiddenUnitType(String name) {
        super(name);
        Vars.content.statusEffects().each(s -> {
            if (s instanceof WeaponModule) weapons.add(((WeaponModule) s).weapon);
        });
        constructor = UnitEntity::create;
    }

    //unspawnable!
    @Override
    public Unit spawn(Team team, float x, float y) {
        return null;
    }

    //obviously it's hidden
    @Override
    public boolean isHidden() {
        return true;
    }
}
