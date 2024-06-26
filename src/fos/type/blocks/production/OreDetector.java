package fos.type.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.Time;
import arc.util.io.*;
import fos.audio.FOSSounds;
import fos.type.blocks.environment.UndergroundOreBlock;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.Ranged;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.air;

public class OreDetector extends Block {
    /** Ore detector radius, in world units. */
    public float range = 15f * 8f;
    /** The active cone width of the radar, in degrees. */
    public float radarCone = 18f;
    /** Radar location speed, in degrees per tick. */
    public float speed = 0.8f;
    /** Effect color. */
    public Color effectColor = Color.valueOf("4b95ff");
    /** Efficiency of drills powered by this detector. */
    public float drillEfficiencyMultiplier = 1f;
    /** Max depth this can work with. */
    public int tier = 1;

    /** Drawer. You know the drill by this point. */
    public DrawBlock drawer = new DrawMulti(
        new DrawDefault(),
        new DrawFlame(){{
            lightRadius = 8f;
            lightSinMag = 0f;
        }},
        new DrawRegion("-top2") // why the fuck is DrawFlame hard-coded to have a -top suffix???
    );

    public OreDetector(String name) {
        super(name);
        solid = true;
        update = true;
        configurable = true;
        hasPower = true;
        canOverdrive = false;
        fogRadius = (int)range / 8;
        clipSize = range * 2f;
        loopSound = FOSSounds.radar;
        flags = EnumSet.of(BlockFlag.unitCargoUnloadPoint);

        config(Boolean.class, (r, b) -> ((OreDetectorBuild) r).showOres = b);
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
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

    @Override
    protected TextureRegion[] icons() {
        return drawer.icons(this);
    }

    @SuppressWarnings("unused")
    public class OreDetectorBuild extends Building implements Ranged {
        public boolean showOres = true;
        public float startTime;
        public Seq<Tile> detectedOres = new Seq<>();

        @Override
        public void created() {
            startTime = Time.time;
        }

        @Override
        public float range() {
            return range * potentialEfficiency;
        }

        @Override
        public float warmup() {
            return showOres ? efficiency : 0f;
        }

        protected TextureRegionDrawable eyeIcon() {
            return showOres ? Icon.eyeSmall : Icon.eyeOffSmall;
        }

        @Override
        public void buildConfiguration(Table table) {
            table.button(eyeIcon(), Styles.clearTogglei, () -> {
                showOres = !showOres;
                startTime = Time.time; //reset the timer to fix sound loop
                configure(showOres);
                deselect();
            }).size(40);
        }

        @Override
        public boolean shouldActiveSound() {
            return canConsume() && showOres;
        }

        public float radarRot() {
            return (curTime() * speed) % 360f;
        }

        public float curTime() {
            return Time.time - startTime;
        }

        @Override
        public void updateTile() {
            if (canConsume() && drillEfficiencyMultiplier > 1) {
                indexer.eachBlock(this, range(), other -> other.block instanceof UndergroundDrill && other.block.canOverdrive,
                    other -> other.applyBoost(efficiency * drillEfficiencyMultiplier, 10f));
            }
        }

        @Override
        public void draw() {
            super.draw();
            drawer.draw(this);

            if (canConsume() && team == player.team()) {
                Draw.z(Layer.light);
                Draw.alpha(0.6f);
                Lines.stroke(2.5f, effectColor);

                if (showOres) {
                    Draw.alpha(1f - (curTime() % 120f) / 120f);
                    Lines.circle(x, y, (curTime() % 120f) / 120f * range());

                    Draw.alpha(0.3f);
                    Fill.arc(x, y, range(), radarCone / 360f, radarRot());
                }

                Draw.alpha(0.2f);
                Lines.circle(x, y, range());
                Lines.circle(x, y, range() * 0.95f);

                Draw.reset();
                if (showOres) locateOres(range());
            }
        }

        @Override
        public void drawLight() {
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, effectColor);
        }

        public void locateOres(float radius) {
            Tile hoverTile = world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);

            tile.circle((int) (radius / tilesize), (ore) -> {
                if (ore != null && ore.overlay() != null && ore.overlay() instanceof UndergroundOreBlock u) {
                    var angle = Mathf.angle(ore.x - tile.x, ore.y - tile.y);
                    var c1 = radarRot();
                    var c2 = radarRot() + radarCone;
                    if (c2 >= 360f && angle < 180f) {
                        angle += 360;
                    }

                    if (angle >= c1 && angle <= c2 && !detectedOres.contains(ore)) {
                        detectedOres.add(ore);
                    }
                }
            });

            for (var ore : detectedOres) {
                if (ore.block() != air || !(ore.overlay() instanceof UndergroundOreBlock u) || tier < u.depth) continue;

                u.shouldDrawBase = true;
                u.drawBase(ore);
                u.shouldDrawBase = false;

                //show an item icon above the cursor/finger
                if (ore == hoverTile && ore.block() != null) {
                    Draw.z(Layer.max);
                    Draw.alpha(1f);
                    Draw.rect(u.drop.uiIcon, ore.x * 8, ore.y * 8 + 8);
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
