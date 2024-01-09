package fos.type.content;

import arc.struct.Seq;
import fos.gen.LumoniPlayerUnitc;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.*;

//YES, this looks very cursed lmao
/**
 * FOR MODDERS: applying this "status effect" will have no effect whatsoever
 */
public class WeaponSet extends StatusEffect {
    public static Seq<WeaponSet> sets = new Seq<>();

    public int id;
    /** Weapons this set contains. */
    public Seq<Weapon> weapons;
    /** Abilities this set contains, if any. */
    public Seq<Ability> abilities = new Seq<>();
    /** Items required to produce the set. */
    public ItemStack[] reqs;
    /** Production time of this set, in ticks. */
    public float produceTime = 300f;


    public WeaponSet(String name, Weapon... weapons) {
        super(name);
        id = sets.size;
        sets.add(this);
        permanent = false;
        this.weapons = new Seq<>(weapons);
    }

    public WeaponSet(String name, Seq<Ability> abilities, Weapon... weapons) {
        this(name, weapons);
        this.abilities = abilities;
    }

    public WeaponSet(String name, Seq<Ability> abilities) {
        this(name, abilities, new Weapon());
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
        lpc.abilities(abilities.toArray(Ability.class));
    }

    /**
     * Applies weapons to unit.
     */
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
     * @param reqs Items needed for production.
     * @return the weapon set for chaining.
     */
    public WeaponSet reqs(ItemStack[] reqs) {
        this.reqs = reqs;

        return this;
    }

    /**
     * Sets the module's production time.
     * @param ticks Production time, in ticks.
     * @return the weapon set for chaining.
     */
    public WeaponSet produceTime(float ticks) {
        this.produceTime = ticks;

        return this;
    }
}

