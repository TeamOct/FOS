package fos.type.units.weapons;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import fos.type.bullets.InjectorBulletType;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class InjectorWeapon extends Weapon {
    public InjectorWeapon(String name) {
        super(name);
    }
    public InjectorWeapon() {
        super();
    }

    @Override
    public void addStats(UnitType u, Table t) {
        super.addStats(u, t);

        if (bullet instanceof InjectorBulletType b) {
            t.row();
            t.add("[lightgray]" + Core.bundle.get("stat.hackchance") + ": [][white]" + (b.minChance == b.maxChance ? (Mathf.round(b.minChance * 100) + "%")
                : (Mathf.round(b.minChance * 100) + "~" + Mathf.round(b.maxChance * 100) + "%")));

            if (b.attacksGuardians) {
                t.row();
                t.add("@stat.attacksbosses");
            }
        }
    }
}
