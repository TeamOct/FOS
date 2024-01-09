package fos.type.blocks.units;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
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
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.draw.*;

public class UpgradeCenter extends Block {
    public TextureRegion topRegion;
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
        topRegion = Core.atlas.find(name + "-top");
    }

    @Override
    public TextureRegion[] icons() {
        Seq<TextureRegion> i = new Seq<>(TextureRegion.class);
        i.add(drawer.finalIcons(this));
        i.add(topRegion);

        return i.toArray();
    }

    @Override
    public void setBars() {
        super.setBars();

        removeBar("items");
        addBar("items", (UpgradeCenterBuild e) -> new Bar(
            () -> Core.bundle.format("bar.items", e.items.total()),
            () -> Pal.items,
            () -> {
                int capacity = 0;
                if (e.weaponSet == null) {
                    capacity = Integer.MAX_VALUE;
                } else {
                    for (ItemStack s : e.weaponSet.reqs) {
                        capacity += s.amount;
                    }
                }

                return (float) e.items.total() / capacity;
            }
        ));

        addBar("progress", (UpgradeCenterBuild e) -> new Bar("bar.progress", Pal.ammo, e::fraction));
    }

    @SuppressWarnings("unused")
    public class UpgradeCenterBuild extends Building {
        public WeaponSet weaponSet;
        public float progress;

        public float fraction() {
            return weaponSet == null ? 0 : progress / weaponSet.produceTime;
        }

        @Override
        public void draw() {
            drawer.draw(this);

            if (weaponSet != null) {
                Draw.draw(Layer.blockOver, () -> Drawf.construct(this, weaponSet, 0f, fraction(), 1f, progress));
            }

            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
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

        @Override
        public void updateTile() {
            if (!configurable) {
                weaponSet = null;
            }

            if (efficiency > 0 && weaponSet != null && fraction() < 1) {
                progress += edelta();
            }
        }

        // Do not waste power if the building does nothing.
        @Override
        public boolean shouldConsume() {
            return enabled && weaponSet != null && fraction() < 1;
        }

        /** Called from packet. **/
        public void upgrade(FOSPackets.UpgradeCenterUpgradePacket packet) {
            if (fraction() < 1 || !(Vars.player.unit() instanceof LumoniPlayerUnitc lpc) || tile == null) return;

            consume();
            progress = 0;
            packet.weaponSet.applyToUnit(lpc);
        }

        @Override
        public void buildConfiguration(Table table) {
            if (WeaponSet.sets.any()) {
                ItemSelection.buildTable(UpgradeCenter.this, table, WeaponSet.sets,
                        () -> weaponSet == null ? null : weaponSet,
                        ws -> {
                            configure(ws == null ? -1 : ws.id);
                            progress = 0;
                        },
                        selectionRows, selectionColumns
                );
            } else {
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }

            table.row();

            if (fraction() >= 1) {
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
