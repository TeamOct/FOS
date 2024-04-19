package fos.type.blocks.special;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import fos.gen.*;
import fos.type.content.WeaponSet;
import mindustry.Vars;
import mindustry.annotations.Annotations;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.draw.*;

public class UpgradeCenter extends Block {
    /** Called when player upgrades their weapon
     * @param player player that upgraded weapon
     * @param tile UpgradeBuildCenter tile
     * */
    @Annotations.Remote(called = Annotations.Loc.both, targets = Annotations.Loc.both, forward = true)
    public static void upgrade(Player player, Tile tile) {
        // check received packet integrity (other validation server-side)
        if (tile == null || tile.build == null || !(tile.build instanceof UpgradeCenterBuild ucb)) return;

        ucb.upgrade(player);
    }

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
        topRegion = Core.atlas.find(name + "-top2");
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
        public float warmup() {
            // TODO: i can't use Mathf.lerp -_-
            return fraction() == 1 ? 0 : efficiency;
        }

        public void upgrade(Player player) {
            consume();
            progress = 0;

            weaponSet.applyToUnit((LumoniPlayerUnitc) player.unit());
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
            if (efficiency > 0 && weaponSet != null && fraction() < 1) {
                progress += edelta();
            }
        }

        // Do not waste power if the building does nothing.
        @Override
        public boolean shouldConsume() {
            return enabled && weaponSet != null && fraction() < 1;
        }

        @Override
        public void buildConfiguration(Table table) {
            if (WeaponSet.sets.size > 0) {
                ItemSelection.buildTable(UpgradeCenter.this, table, WeaponSet.sets,
                        () -> weaponSet == null ? null : weaponSet,
                        ws -> {
                            configure(ws == null ? -1 : ws.id);
                            progress = 0;
                        },
                        selectionRows, selectionColumns
                );
                table.row();
                table.button(Icon.units, Styles.clearTogglei, () -> {
                    if (weaponSet == null || Vars.player == null) return;
                    FOSCall.upgrade(Vars.player, tile());
                    deselect();
                }).visible(() -> fraction() >= 1f);
            } else {
                deselect();
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

            write.i(weaponSet != null ? weaponSet.id : -1);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            int id = read.i();
            if (id != -1) {
                weaponSet = WeaponSet.sets.get(id);
            }
            progress = read.f();
        }
    }
}
