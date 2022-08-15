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
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;

import static mindustry.Vars.world;

public class LuminaCoreBlock extends CoreBlock {
    public float radarRange = 25f * 8f + 8f * size;

    public LuminaCoreBlock(String name) {
        super(name);
        configurable = true;
        buildType = LuminaCoreBuild::new;
    }

    @Override
    public boolean canReplace(Block other) {
        return !other.name.equals("fos-core-colony") && super.canReplace(other);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (this.name.equals("fos-core-colony")) return true;

        return super.canPlaceOn(tile, team, rotation);
    }

    public class LuminaCoreBuild extends CoreBuild {
        public boolean showOres = true;

        protected TextureRegionDrawable eyeIcon() {
            return showOres ? Icon.eyeSmall : Icon.eyeOffSmall;
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
            if (canConsume() && showOres && radarRange != 0) {
                Draw.z(Layer.bullet - 0.0001f);
                Draw.alpha(0.6f);
                Lines.stroke(2.5f, Color.valueOf("4b95ff"));
                float x2 = x + (Mathf.cos(Time.time / 18f) * radarRange);
                float y2 = y + (Mathf.sin(Time.time / 18f) * radarRange);
                Draw.z(Layer.block - 1f);
                Lines.line(x, y, x2, y2);
                Draw.z(Layer.bullet - 0.0001f);
                Draw.alpha(0.2f);
                Drawf.circles(x, y, radarRange, Color.valueOf("4b95ff"));
                Drawf.circles(x, y, radarRange * 0.95f, Color.valueOf("4b95ff"));
                locateOres((int)x, (int)y, radarRange);
            }
        }

        public void locateOres(int x, int y, float range) {
            for (float i = -range; i <= range * 4; i+=8) {
                for (float j = -range; j <= range * 4; j+=8) {
                    Tile tile = world.tileWorld(i, j);
                    //oh god so many conditions here
                    if (Mathf.within(x, y, i, j, range) && tile != null && tile.overlay() != null && tile.overlay() instanceof UndergroundOreBlock) {
                        Draw.z(1f);
                        Draw.alpha(0.4f);
                        Drawf.square(tile.x * 8, tile.y * 8, 3, Mathf.PI / 4, tile.drop().color);

                        //show an item icon above the cursor/finger
                        Tile hoverTile = world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);

                        if (tile == hoverTile) {
                            Draw.z(Layer.max);
                            Draw.alpha(1f);
                            Draw.rect(tile.drop().uiIcon, tile.x * 8, tile.y * 8 + 8);
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
