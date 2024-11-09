package fos.type.units.weapons;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import fos.entities.bullet.AcidBulletType;
import mindustry.Vars;
import mindustry.type.*;

public class AcidWeapon extends Weapon {
    @Override
    public void addStats(UnitType u, Table t) {
        super.addStats(u, t);

        if (bullet instanceof AcidBulletType b) {
            t.row();
            t.add(Core.bundle.format("stat.fos-aciddamage", Mathf.round(b.acidDamage * 60f)) +
                "[lightgray] ~ [stat]" + (b.acidLifetime / 60) + "[] " + Core.bundle.get("unit.seconds") + "[]");

            t.row();
            t.add(Core.bundle.format("stat.fos-acidrange", b.acidRadius / Vars.tilesize));
        }
    }

}
