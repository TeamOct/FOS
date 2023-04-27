package fos.type.blocks.storage;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.io.*;
import fos.type.blocks.environment.UndergroundOreBlock;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.air;

/**
 * A class for cores that have a functionality of scanning underground ores.
 * (We don't talk about Core: Colony here).
 * @author Slotterleet
 * @author nekit508
 */
public class DetectorCoreBlock extends CoreBlock {
    /** Ore detector radius, in world units. */
    public float radarRange = 25f * 8f;
    /** Player respawn cooldown. */
    public float spawnCooldown = 5f * 60f;
    /** Applies to Core: Colony only. Minimum distance between adjacent Colonies. */
    public float colonyNoBuildRadius = 400f;

    public DetectorCoreBlock(String name) {
        super(name);
        configurable = true;
        clipSize = radarRange * 2f;
    }

    @Override
    protected TextureRegion[] icons() {
        return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
    }

    @Override
    public boolean canReplace(Block other) {
        return !other.name.equals("fos-core-colony") && super.canReplace(other);
    }

    @Override
    public boolean canBreak(Tile tile) {
        return super.canBreak(tile) || name.equals("fos-core-colony");
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        boolean colonyTooClose = indexer.eachBlock(team, tile.worldx(), tile.worldy(), colonyNoBuildRadius, b -> b.block.name.equals("fos-core-colony"), b -> {});
        return super.canPlaceOn(tile, team, rotation) ||
            (name.equals("fos-core-colony") && !colonyTooClose);
    }

    @SuppressWarnings("unused")
    public class DetectorCoreBuild extends CoreBuild {
        public float timer = 0f;
        public boolean showOres = true, requested = false;

        protected TextureRegionDrawable eyeIcon() {
            return showOres ? Icon.eyeSmall : Icon.eyeOffSmall;
        }

        @Override
        public void requestSpawn(Player player) {
            //Core: Colony isn't supposed to spawn anything
            if (name.equals("fos-core-colony")) return;

            //spawn cooldown
            if (!requested) {
                boolean immediate = Vars.state.isEditor() || Vars.state.rules.infiniteResources;
                timer = immediate ? 0f : spawnCooldown;
                requested = true;
                Time.run(timer, () -> {
                    if (player.dead()) {
                        super.requestSpawn(player);
                    }
                    requested = false;
                });
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if (timer > 0) timer -= Time.delta;
        }

        @Override
        public void buildConfiguration(Table table) {
            table.button(eyeIcon(), Styles.clearTogglei, () -> {
                showOres = !showOres;
                deselect();
            }).size(40);
        }

        @Override
        public void draw() {
            super.draw();
            if (showOres && radarRange != 0 && team == player.team()) {
                Draw.z(Layer.light);
                Draw.alpha(0.6f);
                Lines.stroke(2.5f, Color.valueOf("4b95ff"));
                Draw.alpha(0.2f);
                float x2 = x + (Mathf.cos(Time.time / 18f) * radarRange);
                float y2 = y + (Mathf.sin(Time.time / 18f) * radarRange);
                Lines.line(x, y, x2, y2);
                Lines.circle(x, y, radarRange);
                Lines.circle(x, y, radarRange * 0.95f);
                Draw.reset();
                locateOres(radarRange);
            }

            if (timer > 0) {
                Vars.ui.showLabel(String.valueOf(Mathf.ceil(timer / 60f)), 1f / 60f, x, y + 16f);

                Draw.z(Layer.overlayUI);
                Draw.rect("empty", x, y, 45f);

                float progress = 1 - timer / spawnCooldown;
                Draw.draw(Layer.blockOver, () -> Drawf.construct(this, unitType, 0f, progress, 1f, progress * 300f));

                Drawf.square(x, y, 6f);
            }
        }

        public void locateOres(float radius) {
            Tile hoverTile = world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);

            tile.circle((int) (radius / tilesize), (tile) -> {
                if (tile != null && tile.overlay() instanceof UndergroundOreBlock u
                    && tile.block() == air) {
                    int variants = tile.overlay().variants;
                    int variant = Mathf.randomSeed(tile.pos(), 0, Math.max(0, variants - 1));
                    Draw.draw(Layer.light, () -> Draw.rect(tile.overlay().variantRegions[variant], tile.x * 8, tile.y * 8));

                    // show an item icon above the cursor/finger
                    if (tile == hoverTile && tile.block() != null) {
                        Draw.z(Layer.max);
                        Draw.alpha(1f);
                        Draw.rect(u.drop.uiIcon, tile.x * 8, tile.y * 8 + 8);
                    }
                }
            });

            Draw.reset();
        }

        @Override
        public void write(Writes write) {
            write.bool(showOres);
        }

        @Override
        public void read(Reads read, byte revision) {
            showOres = read.bool();
        }
    }
}
