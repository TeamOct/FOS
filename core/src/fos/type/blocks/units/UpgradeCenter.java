package fos.type.blocks.units;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import fos.type.content.WeaponModule;
import fos.type.packets.UpgradeCenterUpgradePacket;
import fos.type.units.LumoniPlayerUnitType;
import mindustry.Vars;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.draw.*;

public class UpgradeCenter extends Block {
    public DrawBlock drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-top"));

    public int[] capacities = {};
    public Seq<WeaponModule> weaponModules = Vars.content.statusEffects().copy().filter(s -> s instanceof WeaponModule).as();

    public UpgradeCenter(String name) {
        super(name);
        solid = false;
        update = true;
        configurable = true;
        hasPower = true;
        hasItems = true;
        clearOnDoubleTap = true;

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

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    @SuppressWarnings("unused")
    public class UpgradeCenterBuild extends Building {
        public int weaponIndex = -1;

        public Seq<WeaponModule> getWeaponModules() {
            return weaponModules;
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public void drawLight() {
            super.drawLight();
            drawer.drawLight(this);
        }

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

        /** Called from packet. **/
        public void upgrade(UpgradeCenterUpgradePacket packet) {
            Weapon weapon = getWeaponModules().get(weaponIndex).weapon;
            if (weapon == null) return;

            if (potentialEfficiency < 1 || !(Vars.player.unit().type instanceof LumoniPlayerUnitType) || tile == null) return;

            consume();
            packet.player.unit().mounts(new WeaponMount[]{weapon.mountType.get(weapon)});
        }

        @Override
        public void buildConfiguration(Table table) {
            if (weaponModules.any()) {
                ItemSelection.buildTable(UpgradeCenter.this, table, weaponModules, () -> weaponIndex == -1 ? null :
                        weaponModules.get(weaponIndex), wm -> configure(weaponModules.indexOf(i -> i == wm)),
                        selectionRows, selectionColumns);
            } else {
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }

            table.row();

            table.button(Icon.units, Styles.clearTogglei, () -> {
                deselect();

                if (weaponIndex == -1) return;
                UpgradeCenterUpgradePacket packet = new UpgradeCenterUpgradePacket(Vars.player, this, weaponIndex);
                if (!Vars.net.active())
                    upgrade(packet);
                else
                    Vars.net.send(packet, true);
            });
        }

        @Override
        public int getMaximumAccepted(Item item) {
            Seq<ItemStack> seq = new Seq<>();
            seq.add(weaponModules.get(weaponIndex).reqs);

            ItemStack stack = seq.find(s -> s.item == item);
            return stack != null ? stack.amount : 0;
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
