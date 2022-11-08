package fos.type.blocks.storage;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import fos.type.blocks.environment.UndergroundOreBlock;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;

import static mindustry.Vars.world;

public class LuminaCoreBlock extends CoreBlock {
    public float radarRange = 25f * 8f;
    public float spawnCooldown = 5f * 60f;

    public LuminaCoreBlock(String name) {
        super(name);
        configurable = true;
        buildType = LuminaCoreBuild::new;
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

    public class LuminaCoreBuild extends CoreBuild {
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
                timer = spawnCooldown;
                requested = true;
                Time.run(spawnCooldown, () -> {
                    super.requestSpawn(player);
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
                Draw.z(Layer.bullet - 0.0001f);
                Draw.alpha(0.6f);
                Lines.stroke(2.5f, Color.valueOf("4b95ff"));
                Draw.alpha(0.2f);
                float x2 = x + (Mathf.cos(Time.time / 18f) * radarRange);
                float y2 = y + (Mathf.sin(Time.time / 18f) * radarRange);
                Lines.line(x, y, x2, y2);
                Drawf.circles(x, y, radarRange, Color.valueOf("4b95ff"));
                Drawf.circles(x, y, radarRange * 0.95f, Color.valueOf("4b95ff"));
                locateOres(radarRange);
            }

            if (timer > 0) {
                Vars.ui.showLabel(String.valueOf(Mathf.ceil(timer / 60f)), 1f / 60f, x, y + 16f);

                Draw.z(Layer.blockOver);
                Draw.rect("empty", x, y, 45f);

                float progress = 1 - timer / spawnCooldown;
                Draw.draw(Layer.blockOver, () -> Drawf.construct(this, unitType, 0f, progress, 1f, progress * 300f));

                Drawf.square(x, y, 6f);
            }
        }

        public void locateOres(float range) {
            for (float i = -range; i <= range; i+=8) {
                for (float j = -range; j <= range; j+=8) {
                    Tile tile = world.tileWorld(x + i, y + j);
                    //oh god so many conditions here
                    if (Mathf.within(x, y, x + i, y + j, range) && tile != null && tile.overlay() != null && tile.overlay() instanceof UndergroundOreBlock u) {
                        Draw.z(1f);
                        Draw.alpha(0.6f);

                        Drawf.light(tile.x * 8, tile.y * 8, 6f, u.drop.color, 0.8f);
                        Draw.rect(tile.overlay().region, tile.x * 8, tile.y * 8);

                        //show an item icon above the cursor/finger
                        Tile hoverTile = world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);

                        if (tile == hoverTile && tile.block() != null) {
                            Draw.z(Layer.max);
                            Draw.alpha(1f);
                            Draw.rect(u.drop.uiIcon, tile.x * 8, tile.y * 8 + 8);
                        }
                    }
                }
            }
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
