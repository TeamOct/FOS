package fos.type.content;

import mindustry.gen.Unit;
import mindustry.type.*;

//YES, this looks very cursed lmao
//FOR MODDERS: applying this "status effect" will have no effect whatsoever
public class WeaponModule extends StatusEffect {
    public Weapon weapon;

    public WeaponModule(String name, Weapon weapon) {
        super(name);
        permanent = false;
        this.weapon = weapon;
    }

    @Override
    public void init() {
        super.init();
        weapon.init();
    }

    @Override
    public void load() {
        super.load();
        weapon.load();

        fullIcon = weapon.region;
    }

    @Override
    public void applied(Unit unit, float time, boolean extend) {
        unit.unapply(this);
    }
}