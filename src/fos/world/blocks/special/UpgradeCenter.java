package fos.world.blocks.special;

import arc.Core;
import arc.func.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import fos.gen.LumoniPlayerc;
import fos.net.FOSCall;
import fos.type.WeaponSet;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.draw.*;

import static mindustry.Vars.*;

public class UpgradeCenter extends Block {
    /** Called when player upgrades their weapon
     * @param player player that upgraded weapon
     * @param tile UpgradeBuildCenter tile
     * */
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

        selectionColumns = 3;

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

            weaponSet.applyToUnit((LumoniPlayerc) player.unit());
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
                buildWeaponsTable(UpgradeCenter.this, table, WeaponSet.sets.select(ws -> !isSetBanned(ws)),
                        () -> weaponSet == null ? null : weaponSet,
                        ws -> {
                            configure(ws == null || isSetBanned(ws) ? -1 : ws.id);
                            progress = 0;
                        },
                        selectionRows, selectionColumns
                );
                table.row();
                table.button(Icon.units, Styles.clearTogglei, () -> {
                    if (weaponSet == null || Vars.player == null) return;
                    FOSCall.upgrade(Vars.player, tile());
                    deselect();
                }).visible(() -> !isSetBanned(weaponSet) && fraction() >= 1f);
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

        protected boolean isSetBanned(WeaponSet set) {
            Seq<String> banned = new Seq<>(state.rules.tags.get("fos-bannedMountUpgrades", "").split(";"));
            return set != null && banned.contains(set.name);
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

        // Stolen from ItemSelection.java & slightly modified for our needs.
        public <T extends UnlockableContent> void buildWeaponsTable(@Nullable Block block, Table table, Seq<T> items, Prov<T> holder, Cons<T> consumer, int rows, int columns){
            ButtonGroup<ImageButton> group = new ButtonGroup<>();
            group.setMinCheckCount(0);
            Table cont = new Table().top();
            cont.defaults().size(40);

            Runnable rebuild = () -> {
                group.clear();
                cont.clearChildren();

                var text = "";
                int i = 0;

                for(T item : items){
                    if(!item.unlockedNow() || (item instanceof Item checkVisible && state.rules.hiddenBuildItems.contains(checkVisible))) continue;

                    ImageButton button = cont.button(Tex.whiteui, Styles.clearNoneTogglei, Mathf.clamp(item.selectionSize, 0f, 40f), () -> {
                        control.input.config.hideConfig();
                    }).tooltip(item.localizedName).group(group).get();
                    button.changed(() -> consumer.get(button.isChecked() ? item : null));
                    button.getStyle().imageUp = new TextureRegionDrawable(item.uiIcon);
                    button.update(() -> button.setChecked(holder.get() == item));

                    if(i++ % columns == (columns - 1)){
                        cont.row();
                    }
                }
            };

            rebuild.run();

            Table main = new Table().background(Styles.black6);

            ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
            pane.setScrollingDisabled(true, false);

            if(block != null){
                pane.setScrollYForce(block.selectScroll);
                pane.update(() -> {
                    block.selectScroll = pane.getScrollY();
                });
            }

            pane.setOverscroll(false, false);
            main.add(pane).maxHeight(40 * rows);
            table.top().add(main);
        }
    }
}
