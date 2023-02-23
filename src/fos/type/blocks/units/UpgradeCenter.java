package fos.type.blocks.units;

import arc.graphics.Color;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.*;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Structs;
import arc.util.io.Reads;
import arc.util.io.Writes;
import fos.type.content.WeaponModule;
import fos.type.units.LumoniPlayerUnitType;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Weapon;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.consumers.ConsumeItemDynamic;

import static mindustry.Vars.*;

public class UpgradeCenter extends Block {
    public int[] capacities = {};
    protected Seq<WeaponModule> weaponModules = Vars.content.statusEffects().copy().filter(s -> s instanceof WeaponModule).as();

    public UpgradeCenter(String name) {
        super(name);
        solid = false;
        update = true;
        configurable = true;
        hasPower = true;
        hasItems = true;
        clearOnDoubleTap = true;
        buildType = UpgradeCenterBuild::new;

        selectionColumns = 5;

        config(Integer.class, (UpgradeCenterBuild tile, Integer i) -> {
            if (!configurable) return;

            tile.weaponIndex = i >= 0 ? i : -1;
        });

        consume(new ConsumeItemDynamic((UpgradeCenterBuild e) -> e.weaponIndex != -1 ? weaponModules.get(e.weaponIndex).reqs : ItemStack.empty));
    }

    @Override
    public void init() {
        capacities = new int[Vars.content.items().size];
        for(WeaponModule wm : weaponModules) {
            for(ItemStack stack : wm.reqs) {
                capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
        }

        super.init();
    }

    public class UpgradeCenterBuild extends Building {
        public int weaponIndex = -1;

        @Override
        public void display(Table table) {
            super.display(table);

            TextureRegionDrawable reg = new TextureRegionDrawable();

            table.row();
            table.table(t -> {
                t.left();
                t.image().update(i -> {
                    i.setDrawable(weaponIndex == -1 ? Icon.cancel : reg.set(weaponModules.get(weaponIndex).uiIcon));
                    i.setScaling(Scaling.fit);
                    i.setColor(weaponIndex == -1 ? Color.lightGray : Color.white);
                }).size(32).padBottom(-4).padRight(2);

                t.label(() -> weaponIndex == -1 ? "@none" : weaponModules.get(weaponIndex).localizedName).wrap().width(230f).color(Color.lightGray);
            }).left();
        }

        @Override
        public void buildConfiguration(Table table) {
            if (weaponModules.any()) {
                ItemSelection.buildTable(UpgradeCenter.this, table, weaponModules, () -> weaponIndex == -1 ? null : weaponModules.get(weaponIndex), wm -> configure(weaponModules.indexOf(i -> i == wm)), selectionRows, selectionColumns);
            } else {
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }

            table.row();

            table.button(Icon.units, Styles.clearTogglei, () -> {
                deselect();

                if (weaponIndex == -1) return;

                Weapon weapon = weaponModules.get(weaponIndex).weapon;

                if (weapon == null) return;

                if (potentialEfficiency < 1 || !(Vars.player.unit().type instanceof LumoniPlayerUnitType)) return;

                Player player = Vars.player;
                if(player == null || tile == null || !(tile.build instanceof UpgradeCenterBuild entity)) return;

                consume();

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

        @Override
        public int getMaximumAccepted(Item item) {
            Seq<ItemStack> seq = new Seq<>();
            seq.add(weaponModules.get(weaponIndex).reqs);

            return seq.find(s -> s.item == item).amount;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return weaponIndex != -1 && items.get(item) < getMaximumAccepted(item) &&
                Structs.contains(weaponModules.get(weaponIndex).reqs, stack -> stack.item == item);
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(weaponIndex);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            weaponIndex = read.i();
        }
    }
}
