package fos.type.content;

import arc.struct.Seq;
import fos.gen.LumoniPlayerUnitc;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.*;

//YES, this looks very cursed lmao
/**
 * FOR MODDERS: applying this "status effect" will have no effect whatsoever
 **/
public class WeaponSet extends StatusEffect {
    public static Seq<WeaponSet> sets = new Seq<>();

    public int id;
    public Seq<Weapon> weapons;
    public ItemStack[] reqs;

    public WeaponSet(String name, Weapon... weapons) {
        super(name);
        id = sets.size;
        sets.add(this);
        permanent = false;
        this.weapons = new Seq<>(weapons);
    }

    @Override
    public void init() {
        super.init();

        for (Weapon weapon : weapons)
            weapon.init();
    }

    @Override
    public void load() {
        super.load();

        for (Weapon weapon : weapons)
            weapon.load();

        fullIcon = uiIcon = weapons.first().region;
    }

    @Override
    public void applied(Unit unit, float time, boolean extend) {
        unit.unapply(this);
    }

    public void applyToUnit(LumoniPlayerUnitc lpc) {
        lpc.isEditedWeapons(true);
        lpc.weaponSet(this);
        lpc.mounts(getMounts());
    }

    /**
     * Applies weapons to unit.
     **/
    public WeaponMount[] getMounts() {
        WeaponMount[] mounts = new WeaponMount[weapons.size];

        for (int i = 0; i < mounts.length; i++)
            mounts[i] = getMount(i);

        return mounts;
    }

    public WeaponMount getMount(int index) {
        Weapon weapon = weapons.get(index);
        return weapon.mountType.get(weapon);
    }

    /**
     * Sets the module's requirements.
     * @return the weapon set for chaining. */
    public WeaponSet reqs(ItemStack[] reqs) {
        this.reqs = reqs;

        return this;
    }
}

