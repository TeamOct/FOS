package fos.world.blocks.logic;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.content;
import static mindustry.Vars.tilesize;
import static mindustry.content.Blocks.air;

public class ItemDisplay extends Block {
    public TextureRegion bottomRegion;

    public ItemDisplay(String name) {
        super(name);
        update = true;
        configurable = true;
        solid = true;
        destructible = true;
        group = BlockGroup.logic;
        drawDisabled = false;

        selectionColumns = 10;
        selectionRows = 8;

        @SuppressWarnings("rawtypes") Seq<Class> contents = new Seq<>(
            new Class[]{Block.class, Item.class, Liquid.class, UnitType.class});
        for (var cl : contents) {
            //noinspection unchecked
            config(cl, (ItemDisplayBuild build, UnlockableContent content) -> {
                build.toDisplay = content;
            });
        }

        configClear((ItemDisplayBuild build) -> {
            build.toDisplay = null;
        });
    }

    @Override
    public void load() {
        super.load();
        bottomRegion = Core.atlas.find(name + "-bottom");
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{bottomRegion, region};
    }

    @Override
    public void getPlanConfigs(Seq<UnlockableContent> options) {
        options.add(content.blocks().select(UnlockableContent::unlockedNow));
        options.add(content.items().select(UnlockableContent::unlockedNow));
        options.add(content.liquids().select(UnlockableContent::unlockedNow));
        options.add(content.units().select(UnlockableContent::unlockedNow));
    }

    public class ItemDisplayBuild extends Building {
        @Nullable UnlockableContent toDisplay;

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(ItemDisplay.this, table,
                content.blocks().select(UnlockableContent::unlockedNow).<UnlockableContent>as()
                    .add(content.items().select(UnlockableContent::unlockedNow).as())
                    .add(content.liquids().select(UnlockableContent::unlockedNow).as())
                    .add(content.units().select(UnlockableContent::unlockedNow).as()),
                () -> (UnlockableContent)config(), this::configure, selectionRows, selectionColumns);
            table.row();
            table.button(Icon.trash, () -> {
                toDisplay = null;
                deselect();
            });
        }

        @Override
        public void draw() {
            Draw.rect(bottomRegion, x, y);
            if (toDisplay != null) {
                Draw.alpha(0.75f);

                float w = (size - 1) * tilesize - 2f, h = (size - 1) * tilesize - 2f, ratio = toDisplay.uiIcon.ratio();
                if (ratio > 1) h /= ratio;
                if (ratio < 1) w *= ratio;
                Draw.rect(toDisplay.uiIcon, x, y, w, h);
                Draw.reset();
            }
            super.draw();
        }

        @Override
        public void drawCached() {
            draw();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.str(toDisplay == null ? "block" : toDisplay.getContentType().toString());
            write.str(toDisplay == null ? "air" : toDisplay.name);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            toDisplay = content.getByName(ContentType.valueOf(read.str()), read.str());

            if (toDisplay == air) toDisplay = null;
        }
    }
}
