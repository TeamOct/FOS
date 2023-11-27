package fos.type.content;

import mindustry.type.Weapon;

public class ModuleWeapon extends Weapon {
    public WeaponModule module;

    public ModuleWeapon() {
        this("");
    }

    public ModuleWeapon(String name) {
        super(name);
    }

    public void apply(WeaponModule wm) {
        module = wm;
    }
}
