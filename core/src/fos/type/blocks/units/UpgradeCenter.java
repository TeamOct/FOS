package fos.type.blocks.units;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import fos.gen.LumoniPlayerUnitc;
import fos.net.FOSPackets;
import fos.type.content.WeaponSet;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.draw.*;

public class UpgradeCenter extends Block {
    public DrawBlock drawer = new DrawDefault();

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

            tile.weaponSet = i != -1 ? WeaponSet.sets.get(i) : null;
        });

        consume(new ConsumeItemDynamic((UpgradeCenterBuild e) -> e.weaponSet != null ? e.weaponSet.reqs : ItemStack.empty));
    }

    @Override
    public void init() {
        for(WeaponSet wm : WeaponSet.sets) {
            for(ItemStack stack : wm.reqs) {
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
        public WeaponSet weaponSet;

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
                    i.setDrawable(weaponSet == null ? Icon.cancel : reg.set(weaponSet.uiIcon));
                    i.setScaling(Scaling.fit);
                    i.setColor(weaponSet == null ? Color.lightGray : Color.white);
                }).size(32).padBottom(-4).padRight(2);

                t.label(() -> weaponSet == null ? "@none" : weaponSet.localizedName).wrap().width(230f).color(Color.lightGray);
            }).left();
        }

        /** Called from packet. **/
        public void upgrade(FOSPackets.UpgradeCenterUpgradePacket packet) {
            if (potentialEfficiency < 1 || !(Vars.player.unit() instanceof LumoniPlayerUnitc lpc) || tile == null) return;

            consume();
            packet.weaponSet.applyToUnit(lpc);
        }

        @Override
        public void buildConfiguration(Table table) {
            if (WeaponSet.sets.any()) {
                ItemSelection.buildTable(UpgradeCenter.this, table, WeaponSet.sets,
                        () -> weaponSet == null ? null : weaponSet,
                        ws -> configure(ws == null ? -1 : ws.id),
                        selectionRows, selectionColumns
                );
            } else {
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }

            table.row();

            table.button(Icon.units, Styles.clearTogglei, () -> {
                if (weaponSet == null) return;
                FOSPackets.UpgradeCenterUpgradePacket packet = new FOSPackets.UpgradeCenterUpgradePacket(Vars.player,
                        this, weaponSet);
                if (!Vars.net.active())
                    upgrade(packet);
                else {
                    if (Vars.net.server())
                        upgrade(packet);
                    Vars.net.send(packet, true);
                }

                deselect();
            });
        }

        @Override
        public int getMaximumAccepted(Item item) {
            Seq<ItemStack> seq = new Seq<>();
            seq.add(weaponSet.reqs);

            ItemStack stack = seq.find(s -> s.item == item);
            return stack != null ? stack.amount : 0;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return weaponSet != null && items.get(item) < getMaximumAccepted(item) &&
                Structs.contains(weaponSet.reqs, stack -> stack.item == item);
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(weaponSet.id);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            weaponSet = WeaponSet.sets.get(read.i());
        }
    }
}
