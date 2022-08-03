package fos.type.blocks;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class OreDetector extends Block {
    public float range = 15f * 8f;

    public OreDetector(String name) {
        super(name);
        solid = true;
        update = true;
        hasPower = true;
        canOverdrive = false;
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
        @Override
        public float range() {
            return range * potentialEfficiency;
        }

        @Override
        public void draw() {
            super.draw();
            if (canConsume()) {
                Draw.z(Layer.bullet - 0.0001f);
                Draw.alpha(0.6f);
                Lines.stroke(2.5f, Color.valueOf("4b95ff"));
                float x2 = x + (Mathf.cos(Time.time / 18f) * range());
                float y2 = y + (Mathf.sin(Time.time / 18f) * range());
                Lines.line(x, y, x2, y2);
                Draw.alpha(0.2f);
                Drawf.circles(x, y, range(), Color.valueOf("4b95ff"));
                Drawf.circles(x, y, range() * 0.95f, Color.valueOf("4b95ff"));
                locateOres((int)x, (int)y, range());
            }
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, Color.valueOf("4b95ff"));
        }

        public void locateOres(int x, int y, float range) {
            for (float i = -range; i <= range * 4; i+=8) {
                for (float j = -range; j <= range * 4; j+=8) {
                    Tile tile = world.tileWorld(i, j);
                    if (Mathf.within(x, y, i, j, range) && tile != null && tile.overlay() != null && tile.overlay() instanceof UndergroundOreBlock) {
                        Draw.z(Layer.bullet - 0.0001f);
                        Draw.alpha(0.4f);
                        Drawf.square(tile.x * 8, tile.y * 8, 4, Mathf.PI / 4, tile.drop().color);

                        //show an item icon above the cursor/finger?
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
    }

}
