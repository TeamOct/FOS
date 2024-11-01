package fos.world.blocks.special;

import arc.util.Time;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.world.*;

import static mindustry.Vars.world;

public class CliffExplosive extends Block {
    public float range = 16f;

    public CliffExplosive(String name) {
        super(name);
        solid = true;
        update = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashSquare(Vars.player.team().color, x*8, y*8, range * 2 + 8);
    }

    @SuppressWarnings("unused")
    public class CliffExplosiveBuild extends Building {
        public float timer = 180f;

        @Override
        public void updateTile() {
            if (timer <= 0) {
                detonate(range);
            }

            timer -= Time.delta;
        }

        public void detonate(float range) {
            for (float i = -range; i <= range; i+=8) {
                for (float j = -range; j <= range; j += 8) {
                    Tile tile = world.tileWorld(x + i, y + j);

                    if (tile == null || tile.block().hasBuilding() || tile.block() == Blocks.cliff) continue;

                    if (tile.block() != Blocks.air) Fx.blockExplosionSmoke.at(x + i, y + j);
                    tile.setBlock(Blocks.air);
                }
            }

            Sounds.explosionbig.at(this);
            Fx.explosion.at(this);
            tile.remove();
        }
    }
}
