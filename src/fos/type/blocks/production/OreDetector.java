package fos.type.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import fos.type.blocks.environment.UndergroundOreBlock;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.logic.Ranged;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

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
        buildType = OreDetectorBuild::new;
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
                Draw.z(Layer.light);
                Draw.alpha(0.6f);
                Lines.stroke(2.5f, Color.valueOf("4b95ff"));
                Draw.alpha(0.2f);
                float x2 = x + (Mathf.cos(Time.time / 18f) * range());
                float y2 = y + (Mathf.sin(Time.time / 18f) * range());
                Lines.line(x, y, x2, y2);
                Lines.circle(x, y, range);
                Lines.circle(x, y, range * 0.95f);
                locateOres(range());
            }
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, Color.valueOf("4b95ff"));
        }

        protected void locateOres(float range) {
            Draw.reset();
            for (float i = -range; i <= range; i += 8) {
                for (float j = -range; j <= range; j += 8) {
                    Tile tile = world.tileWorld(x + i, y + j);
                    //oh god so many conditions here
                    if (Mathf.within(x, y, x + i, y + j, range) && tile != null && tile.overlay() != null && tile.overlay() instanceof UndergroundOreBlock u
                        && tile.block() == Blocks.air) {
                        Draw.z(Layer.blockProp);

                        int variants = tile.overlay().variants;
                        int variant = Mathf.randomSeed(tile.pos(), 0, Math.max(0, variants - 1));

                        Draw.draw(Layer.darkness + 1f, () -> Draw.rect(tile.overlay().variantRegions[variant], tile.x * 8, tile.y * 8));
                        //Draw.z(Layer.effect);
                        //Drawf.light(tile.x * 8, tile.y * 8, tile.overlay().variantRegions[variant], u.drop.color, 0.8f);

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
