package fos.type.blocks.storage;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import fos.type.blocks.environment.UndergroundOreBlock;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.gen.Player;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static mindustry.content.Blocks.air;

public class DetectorCoreBlock extends CoreBlock {
    public float radarRange = 25f * 8f;
    public float spawnCooldown = 5f * 60f;

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
        return super.canPlaceOn(tile, team, rotation) || name.equals("fos-core-colony");
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
                timer = Vars.state.isEditor() || Vars.state.rules.infiniteResources ? 0f : spawnCooldown;
                requested = true;
                Time.run(spawnCooldown, () -> {
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
            if (showOres && radarRange != 0) {
                Draw.z(Layer.light);
                Draw.alpha(0.6f);
                Lines.stroke(2.5f, Color.valueOf("4b95ff"));
                Draw.alpha(0.2f);
                float x2 = x + (Mathf.cos(Time.time / 18f) * radarRange);
                float y2 = y + (Mathf.sin(Time.time / 18f) * radarRange);
                Lines.line(x, y, x2, y2);
                Lines.circle(x, y, radarRange);
                Lines.circle(x, y, radarRange * 0.95f);
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
                    Draw.z(Layer.blockProp);

                    int variants = tile.overlay().variants;
                    int variant = Mathf.randomSeed(tile.pos(), 0, Math.max(0, variants - 1));
                    Draw.draw(Layer.light, () -> Draw.rect(tile.overlay().variantRegions[variant], tile.x * 8, tile.y * 8));
                    // show an item icon above the cursor/finger
                    // TODO use tap on mobile?
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
