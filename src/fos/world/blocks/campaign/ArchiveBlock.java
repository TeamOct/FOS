package fos.world.blocks.campaign;

import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.ctype.*;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.logic.LAccess;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.air;

/**
 * For use in campaign maps only. Disabled at first, unlocks certain tech when enabled,
 * which is usually controlled by world processors.
 * Must be enabled with a PULSE signal (a single operation instead of a constant loop) to avoid unnecessary lag.
 * @author Slotterleet
 */
public class ArchiveBlock extends Block {
    public ArchiveBlock(String name) {
        super(name);
        update = true;
        noUpdateDisabled = true;
        breakable = false;
        privileged = true;
        solid = true;
        logicConfigurable = true;
        replaceable = false;
        targetable = false;
        group = BlockGroup.logic;
        category = Category.logic;
        drawCached = true;
        drawDynamic = false;
        envEnabled = Env.any;
        selectionColumns = selectionRows = 8;
        buildVisibility = BuildVisibility.worldProcessorOnly;
        hideDetails = false;

        @SuppressWarnings("rawtypes") Seq<Class> contents = new Seq<>(
            new Class[]{Block.class, Item.class, Liquid.class, UnitType.class, StatusEffect.class, Planet.class, SectorPreset.class});
        for (var cl : contents) {
            //noinspection unchecked
            config(cl, (ArchiveBuild build, UnlockableContent content) -> {
                build.toUnlock = content;
            });
        }

        configClear((ArchiveBuild build) -> {
            build.toUnlock = null;
        });
    }

    public boolean accessible() {
        return !privileged || state.rules.editor || state.playtestingMap != null || state.rules.allowEditWorldProcessors;
    }

    @Override
    public boolean canBreak(Tile tile){
        return accessible();
    }

    public class ArchiveBuild extends Building {
        public @Nullable UnlockableContent toUnlock;
        public boolean shouldUnlock = true; // to handle block animations later

        @Override
        public void created() {
            super.created();
            enabled = false; // must be disabled at first
        }

        @Override
        public void updateTile() {
            if (Vars.net.client()) return;
            if (toUnlock == null || toUnlock.unlocked() || !shouldUnlock) return;

            toUnlock.unlock();
            enabled = false;
        }

        @Override
        public Object senseObject(LAccess sensor) {
            if (sensor == LAccess.config) return toUnlock;
            return super.sense(sensor);
        }

        @Override
        public boolean interactable(Team team) {
            return false;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            enabled = read.bool();
            toUnlock = content.getByName(ContentType.valueOf(read.str()), read.str());
            if (toUnlock == air) toUnlock = null;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.bool(enabled);
            write.str(toUnlock == null ? "block" : toUnlock.getContentType().toString());
            write.str(toUnlock == null ? "air" : toUnlock.name);
        }
    }
}
