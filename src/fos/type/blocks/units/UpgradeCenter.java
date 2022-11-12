package fos.type.blocks.units;

import arc.graphics.Color;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.*;
import arc.struct.Seq;
import arc.util.Scaling;
import fos.type.content.WeaponModule;
import fos.type.units.LuminaUnitType;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.StatusEffect;
import mindustry.type.Weapon;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;

public class UpgradeCenter extends Block {
    public UpgradeCenter(String name) {
        super(name);
        solid = false;
        update = true;
        configurable = true;
        buildType = UpgradeCenterBuild::new;

        selectionColumns = 5;

        config(Integer.class, (UpgradeCenterBuild tile, Integer i) -> {
            if (!configurable) return;

            tile.weaponIndex = i >= 0 ? i : -1;
        });

        config(WeaponModule.class, (UpgradeCenterBuild tile, WeaponModule val) -> {
            if (!configurable) return;

            tile.weapon = val.weapon;
        });
    }

    public class UpgradeCenterBuild extends Building {
        public Seq<StatusEffect> modules = Vars.content.statusEffects().copy().filter(s -> s instanceof WeaponModule);

        public Weapon weapon;
        public int weaponIndex = -1;

        @Override
        public void display(Table table) {
            super.display(table);

            TextureRegionDrawable reg = new TextureRegionDrawable();
            WeaponModule w = weaponIndex == -1 ? null : (WeaponModule) modules.get(weaponIndex);

            table.row();
            table.table(t -> {
                t.left();
                t.image().update(i -> {
                    i.setDrawable(weaponIndex == -1 || w == null ? Icon.cancel : reg.set(w.weapon.region));
                    i.setScaling(Scaling.fit);
                    i.setColor(weaponIndex == -1 ? Color.lightGray : Color.white);
                }).size(32).padBottom(-4).padRight(2);

                t.label(() -> weaponIndex == -1 || w == null ? "@none" : w.localizedName).wrap().width(230f).color(Color.lightGray);
            }).left();
        }

        @Override
        public void buildConfiguration(Table table) {
            if (modules.any()) {
                ItemSelection.buildTable(UpgradeCenter.this, table, modules, () -> weaponIndex == -1 ? null : modules.get(weaponIndex), wm -> configure(modules.indexOf(i -> i == wm)), selectionRows, selectionColumns);
            } else {
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }

            table.row();

            table.button(Icon.units, Styles.clearTogglei, () -> {
                deselect();

                if (!canConsume() || weapon == null || potentialEfficiency < 1 || !(Vars.player.unit().type instanceof LuminaUnitType)) return;

                Player player = Vars.player;
                if(player == null || tile == null || !(tile.build instanceof UpgradeCenterBuild entity)) return;

                CoreBlock c = (CoreBlock) player.bestCore().block;
                if(entity.wasVisible){
                    Fx.spawn.at(entity);
                }

                player.set(entity);

                if(!net.client()){
                    Unit unit = c.unitType.create(tile.team());
                    unit.mounts(new WeaponMount[]{weapon.mountType.get(weapon)});
                    unit.set(entity);
                    unit.rotation(90f);
                    unit.impulse(0f, 3f);
                    unit.controller(player);
                    unit.spawnedByCore(true);
                    unit.add();
                }
            });
        }
    }
}
