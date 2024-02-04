package fos.type.blocks.special;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.world;

public class CliffExplosive extends Block {
    public TextureRegion glowRegion;
    public float range = 16f;
    public float time = 180f;
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
    @Override
    public void load(){
        super.load();
        glowRegion = Core.atlas.find(this.name + "-glow");
    }

    @SuppressWarnings("unused")
    public class CliffExplosiveBuild extends Building {
        public float explosionTime = 0;

        @Override
        public void updateTile() {
            if (explosionTime >= time) {
                detonate(range);
            }

            explosionTime += Time.delta;
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
        @Override
        public void draw(){
            super.draw();
            Draw.alpha(explosionTime / time);
            Draw.rect(glowRegion, x, y);
        }
    }
}
