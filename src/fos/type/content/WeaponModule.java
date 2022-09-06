package fos.type.content;

import mindustry.type.*;

//YES, this looks very cursed lmao
//FOR MODDERS: applying this "status effect" will have no effect whatsoever
public class WeaponModule extends StatusEffect {
    public Weapon weapon;

    public WeaponModule(String name, Weapon weapon) {
        super(name);
        fullIcon = weapon.region;
        permanent = false;
        this.weapon = weapon;
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}