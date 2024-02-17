package fos.type.blocks.special;

import arc.audio.Sound;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.world.*;

import static mindustry.Vars.world;

public class SurfaceExplosive extends Block {
    public int range = 3;
    public Effect explosionEffect = Fx.blastExplosion;
    public Sound explosionSound = Sounds.explosionbig;

    public SurfaceExplosive(String name) {
        super(name);
        solid = true;
        update = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x*8, y*8, range * 8, Vars.player.team().color);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        int cliffs = 0;
        for (var e : getEdges()) {
            Tile other = world.tileWorld(tile.worldx() + e.x, tile.worldy() + e.y);
            if (other != null && other.block() == Blocks.cliff) {
                cliffs++;
            }
        }

        return cliffs < size * 4;
    }

    @SuppressWarnings("unused")
    public class SurfaceExplosiveBuild extends Building {
        public float counter = 180f;

        protected Seq<Tile> tiles = new Seq<>();
        protected Seq<Tile> cliffTiles = new Seq<>();

        @Override
        public void updateTile() {
            if (counter <= 0) {
                detonate(range);
            }

            counter -= Time.delta;
        }

        public void detonate(float rad) {
            //pre-detonation init
            tileOn().circle(range, t -> {
                tiles.add(t);
                if (t.block() == Blocks.cliff)
                    cliffTiles.add(t);
                t.setBlock(Blocks.stoneWall);
            });

            //detonation process
            for (Tile tile : tiles) {
                if (!tile.block().isStatic() || tile.block() == Blocks.cliff) continue;

                int rotation = 0;
                for (int i = 0; i < 8; i++) {
                    Tile other = world.tiles.get(tile.x + Geometry.d8[i].x, tile.y + Geometry.d8[i].y);
                    if(other != null && (!other.block().isStatic())){
                        rotation |= (1 << i);
                    }
                }

                if (rotation != 0) {
                    tile.setBlock(Blocks.cliff);
                }

                // a tilde inverts bits, apparently
                tile.data = (byte)~rotation;
            }

            //post-detonation
            for (Tile tile : cliffTiles) {
                tile.setBlock(Blocks.air);
            }
            for (Tile tile : tiles) {
                if (tile.block() != Blocks.cliff) {
                    if (tile.block().isStatic()) {
                        tile.setBlock(Blocks.air);
                    }
                } else {
                    for (int i = 0; i < 8; i++) {
                        Tile other = world.tiles.get(tile.x + Geometry.d8[i].x, tile.y + Geometry.d8[i].y);
                        if (other.block() == Blocks.cliff) {
                            tile.data -= (byte) (1 << i);
                        }
                    }
                }
            }

            explosionSound.at(this);
            explosionEffect.at(this);
            tile.remove();
        }
    }
}
