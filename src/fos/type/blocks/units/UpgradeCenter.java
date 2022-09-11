package fos.type.blocks.units;

import arc.scene.ui.layout.*;
import arc.struct.Seq;
import fos.content.*;
import fos.type.units.LuminaUnitType;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.function.Consumer;

import static mindustry.Vars.net;

public class UpgradeCenter extends Block {
    public UpgradeCenter(String name) {
        super(name);
        solid = false;
        update = true;
        configurable = true;
        buildType = UpgradeCenterBuild::new;
    }

    public class UpgradeCenterBuild extends Building {
        public Weapon weapon = FOSWeaponModules.standard2.weapon;

        //TODO currently non-functional
        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.units, Styles.clearTogglei, () -> {
                if (!canConsume() || potentialEfficiency < 1 || !(Vars.player.unit().type instanceof LuminaUnitType)) return;

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
