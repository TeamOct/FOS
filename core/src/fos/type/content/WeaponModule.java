package fos.type.content;

import arc.struct.Seq;
import mindustry.gen.Unit;
import mindustry.type.*;

//YES, this looks very cursed lmao
/**
 * FOR MODDERS: applying this "status effect" will have no effect whatsoever
 **/
public class WeaponModule extends StatusEffect {
    public static Seq<WeaponModule> modules = new Seq<>();

    public int id;
    public ModuleWeapon weapon;
    public ItemStack[] reqs;

    public WeaponModule(String name, ModuleWeapon weapon) {
        super(name);
        id = modules.size;
        modules.add(this);
        permanent = false;
        this.weapon = weapon;
        weapon.apply(this);
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

    /**
     * Sets the module's requirements.
     * @return the weapon module for chaining. */
    public WeaponModule reqs(ItemStack[] reqs) {
        this.reqs = reqs;

        return this;
    }
}

