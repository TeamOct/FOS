package fos.type.blocks.units;

import arc.graphics.Color;
import arc.scene.ui.layout.*;
import arc.struct.Seq;
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
            if (!configurable || tile.weaponIndex == i) return;

            tile.weaponIndex = i < 0 ? -1 : i;
        });

        config(WeaponModule.class, (UpgradeCenterBuild tile, WeaponModule val) -> {
            if (!configurable) return;

            tile.weapon = val.weapon;
        });
    }

    public class UpgradeCenterBuild extends Building {
        public Weapon weapon;
        public int weaponIndex = -1;

        @Override
        public void buildConfiguration(Table table) {
            Seq<WeaponModule> modules = new Seq<>();
            for(StatusEffect s : content.statusEffects()) {
                if (s instanceof WeaponModule w && w.unlockedNow()) {
                    modules.add(w);
                }
            }

            if (modules.any()) {
                ItemSelection.buildTable(UpgradeCenter.this, table, modules, () -> weaponIndex == -1 ? null : modules.get(weaponIndex), this::configure, selectionRows, selectionColumns);
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
