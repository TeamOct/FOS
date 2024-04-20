package fos.type.blocks.special;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.*;
import fos.content.FOSBlocks;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.Liquid;
import mindustry.ui.Fonts;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;

public class SurfaceExplosive extends Block {
    public int range = 5;
    public Effect explosionEffect = Fx.scatheExplosion;
    public Effect smokeEffect = Fx.titanSmoke;
    public Color explosionColor = Pal.redSpark;
    public Sound explosionSound = Sounds.explosionbig;
    public float explosionShake = 10f;
    public float damage = 2000f;

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
            if (other != null && other.block() == cliff) {
                cliffs++;
            }
        }

        return cliffs < size * 4;
    }

    @SuppressWarnings("unused")
    public class SurfaceExplosiveBuild extends Building {
        public float counter = 180f;

        protected Seq<Tile> tiles = new Seq<>();

        @Override
        public void draw() {
            super.draw();

            Fonts.def.draw(Strings.fixed(Mathf.ceil(counter / 60f), 0), x, y + 2, Pal.redSpark, 0.2f, false, Align.center);
        }

        @Override
        public void updateTile() {
            if (counter <= 0) {
                detonate();
            }

            counter -= Time.delta;
        }

        // hard-coded for now
        // TODO: come up with a better solution?
        public Block deepTile(Block ore) {
            return ore == FOSBlocks.oreTin ? FOSBlocks.oreTinDeep :
                ore == FOSBlocks.oreSilver ? FOSBlocks.oreSilverDeep :
                ore == FOSBlocks.oreVanadium ? FOSBlocks.oreVanadiumDeep :
                    air;
        }

        // TODO: again, hard-coded, but this time, not as much
        public Block deepFloor() {
            return carbonStone;
        }

        public void detonate() {
            // pre-detonation init
            tileOn().circle(range, t -> {
                tiles.add(t);
                if (t.floor() != deepFloor())
                    t.setBlock(stoneWall);
            });

            // detonation process
            for (Tile tile : tiles) {
                if (!tile.block().isStatic() || tile.block() == cliff) continue;

                int rotation = -1;
                boolean valid = false;
                for (int i = 0; i < 8; i++) {
                    Tile other = world.tiles.get(tile.x + Geometry.d8[i].x, tile.y + Geometry.d8[i].y);
                    if (other != null && (!other.block().isStatic())) {
                        if (!valid) {
                            rotation = 0;
                            valid = true;
                        }
                        rotation |= (1 << i);
                    }
                }

                if (rotation != -1) {
                    tile.setBlock(cliff);
                }

                // a tilde inverts bits, apparently
                tile.data = (byte)~rotation;
            }

            // post-detonation
            // a detonator could possibly be deployed near a liquid - for now, fill everything with it
            Liquid l = null;

            for (Tile tile : tiles) {
                if (tile.block() != cliff) {
                    if (tile.block().isStatic()) {
                        tile.setBlock(air);
                    }
                } else {
                    for (int i = 0; i < 8; i++) {
                        Tile other = world.tiles.get(tile.x + Geometry.d8[i].x, tile.y + Geometry.d8[i].y);
                        if (other.block() == cliff) {
                            tile.data -= (byte) (1 << i);
                        }
                    }
                }

                // liquid nearby? well then, more liquid, my friend
                for (Point2 p : Geometry.d4) {
                    Tile other = world.tiles.get(tile.x + p.x, tile.y + p.y);
                    if (other.floor().liquidDrop != null) {
                        l = other.floor().liquidDrop;
                        break;
                    }
                }
                if (l == null) {
                    // save for later cuz setFloorNet() resets overlay
                    var overlay = deepTile(tile.overlay());
                    tile.setFloorNet(deepFloor().asFloor());
                    tile.setOverlayNet(overlay);
                }

                // clean up middle cliffs cuz they are ugly
                boolean valid = false;
                for (Point2 p : Geometry.d4) {
                    Tile other = world.tiles.get(tile.x + p.x, tile.y + p.y);
                    if (other.floor() != deepFloor()) valid = true;
                }
                if (!valid && tile.block() == cliff) {
                    tile.setBlock(air);
                }
            }

            // now, fill the entire crater with liquid if it's present
            if (l != null) {
                for (Tile tile : tiles) {
                    // java sucks
                    Liquid finalL = l;
                    Floor fallback = content.blocks().find(b -> b instanceof Floor f && f.liquidDrop == finalL).asFloor();
                    if (tile.block() == cliff && l == Liquids.water) {
                        Block shallow = content.block(tile.floor().name + "-water");
                        tile.setFloorNet(shallow != null ? shallow : fallback);
                    } else {
                        tile.setFloorNet(fallback);
                    }

                    tile.setBlock(air);
                }
            }

            explosionSound.at(this);
            explosionEffect.at(x, y, explosionColor);
            smokeEffect.at(x, y, explosionColor);
            renderer.shake(explosionShake, 60f);
            Damage.damage(x, y, range * tilesize, damage); // damage nearby units
            tile.remove();
        }
    }
}
