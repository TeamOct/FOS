package fos.type.blocks.production;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import fos.type.blocks.environment.UndergroundOreBlock;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class OreDetector extends Block {
    public float range = 15f * 8f;

    public OreDetector(String name) {
        super(name);
        solid = true;
        update = true;
        configurable = true;
        hasPower = true;
        canOverdrive = false;
        fogRadius = (int)range / 8;
        clipSize = range * 2f;
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Color.valueOf("4b95ff"));
    }

    @SuppressWarnings("unused")
    public class OreDetectorBuild extends Building implements Ranged {
        public boolean showOres = true;

        @Override
        public float range() {
            return range * potentialEfficiency;
        }

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
        public boolean shouldConsume() {
            return showOres;
        }

        @Override
        public void draw() {
            super.draw();
            if (canConsume() && showOres) {
                Draw.z(Layer.bullet - 0.0001f);
                Draw.alpha(0.6f);
                Lines.stroke(2.5f, Color.valueOf("4b95ff"));
                Draw.alpha(0.2f);
                float x2 = x + (Mathf.cos(Time.time / 18f) * range());
                float y2 = y + (Mathf.sin(Time.time / 18f) * range());
                Lines.line(x, y, x2, y2);
                Drawf.circles(x, y, range(), Color.valueOf("4b95ff"));
                Drawf.circles(x, y, range() * 0.95f, Color.valueOf("4b95ff"));
                locateOres(range());
            }
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, Color.valueOf("4b95ff"));
        }

        public void locateOres(float radius) {
            Tile hoverTile = world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);

            tile.circle((int) (radius / tilesize), (tile) -> {
                if (tile.overlay() instanceof UndergroundOreBlock u) {
                    Draw.z(1f);
                    Draw.alpha(0.6f);

                    Drawf.light(tile.worldx(), tile.worldy(), 6f, u.drop.color, 0.8f);
                    Draw.rect(tile.overlay().region, tile.worldx(), tile.worldy());

                    // show an item icon above the cursor/finger
                    // TODO use tap on mobile?
                    if (tile == hoverTile && tile.block() != null) {
                        Draw.z(Layer.max);
                        Draw.alpha(1f);
                        Draw.rect(u.drop.uiIcon, tile.x * 8, tile.y * 8 + 8);
                    }
                }
            });
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
