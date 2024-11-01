package fos.world.blocks.defense;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.*;
import fos.world.draw.FOSStats;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class MendBeamProjector extends Block {
    public float beamPowerConsumption = 0.25f;
    public int range = 5;
    public float healPercent = 5f;
    public float reload = 90f;

    public float laserWidth = 0.65f;
    public Color glowColor = Pal.heal;
    public float glowIntensity = 0.2f, pulseIntensity = 0.07f;
    public float glowScl = 3f;

    public TextureRegion laser, laserEnd, laserCenter;

    public MendBeamProjector(String name) {
        super(name);
        hasPower = true;
        update = true;
        conductivePower = true;
        consumePowerDynamic((MendBeamBuild b) -> {
            int beams = 0;
            for (Tile[] arr : b.facing) {
                for (Tile other : arr) {
                    if (other != null) beams++;
                }
            }
            return beamPowerConsumption * beams;
        });
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.powerUse, beamPowerConsumption * 60f, StatUnit.powerSecond);
        stats.add(FOSStats.maxBeams, size * 4);

        stats.add(Stat.repairTime, (int)(100f / healPercent * reload / 60f), StatUnit.seconds);
        stats.add(Stat.range, range, StatUnit.blocks);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        for (int side = 0; side < 4; side++) {
            for (int i = 0; i < size; i++) {
                nearbySide(x, y, side, i, Tmp.p1);

                int dx = (Tmp.p1.x + Geometry.d4x(side) * (range - 1)) * tilesize, dy = (Tmp.p1.y + Geometry.d4y(side) * (range - 1)) * tilesize;
                Drawf.dashLine(Pal.heal, Tmp.p1.x * tilesize, Tmp.p1.y * tilesize, dx, dy);
            }
        }
    }

    @Override
    public void load() {
        super.load();

        laser = Core.atlas.find(name + "-beam", "fos-mend-laser");
        laserEnd = Core.atlas.find(name + "-beam-end", "fos-mend-laser-end");
        laserCenter = Core.atlas.find(name + "-beam-center", "fos-mend-laser-center");
    }

    @SuppressWarnings("unused")
    public class MendBeamBuild extends Building {
        public float charge = 0;
        public Point2[][] lasers = new Point2[4][size];
        public Tile[][] facing = new Tile[4][size];

        @Override
        public void draw() {
            super.draw();

            if (isPayload()) return;

            for (int side = 0; side < 4; side++) {
                Point2 dir = Geometry.d4(side);
                int ddx = Geometry.d4x(side + 1), ddy = Geometry.d4y(side + 1);

                for (int i = 0; i < size; i++) {
                    Tile face = facing[side][i];
                    if (face == null) continue;

                    Point2 p = lasers[side][i];
                    float lx = face.worldx() - (dir.x / 2f) * tilesize, ly = face.worldy() - (dir.y / 2f) * tilesize;

                    float width = (laserWidth + Mathf.absin(Time.time + i * 5 + (id % 9) * 9, glowScl, pulseIntensity)) * efficiency;

                    Draw.z(Layer.power - 1);
                    Draw.mixcol(glowColor, Mathf.absin(Time.time + i * 5 + id * 9, glowScl, glowIntensity));
                    if (Math.abs(p.x - face.x) + Math.abs(p.y - face.y) == 0) {
                        Draw.scl(width);

                        Draw.alpha(efficiency);
                        Draw.rect(laserCenter, lx, ly);

                        Draw.scl();
                    } else {
                        float lsx = (p.x - dir.x / 2f) * tilesize, lsy = (p.y - dir.y / 2f) * tilesize;
                        Draw.alpha(efficiency);
                        Drawf.laser(laser, laserEnd, lsx, lsy, lx, ly, width);
                    }

                    Draw.reset();
                }
            }
        }

        @Override
        public void created() {
            super.created();
            updateLasers();
            updateFacing();
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            updateLasers();
            updateFacing();
        }

        @Override
        public void updateTile() {
            boolean canHeal = !checkSuppression();

            charge += edelta();

            if (charge >= reload && canHeal) {
                charge = 0;

                updateLasers();
                updateFacing();
                healFacing();
            }
        }

        @Override
        public void consume() {
            super.consume();
        }

        protected void updateLasers() {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < size; j++) {
                    if (lasers[i][j] == null) lasers[i][j] = new Point2();
                    nearbySide(tileX(), tileY(), i, j, lasers[i][j]);
                }
            }
        }

        protected void updateFacing() {
            for (int side = 0; side < 4; side++) {
                int dx = Geometry.d4x(side), dy = Geometry.d4y(side);

                for (int p = 0; p < size; p++) {
                    Point2 l = lasers[side][p];
                    Tile dest = null;
                    for (int i = 0; i < range; i++) {
                        int rx = l.x + dx * i, ry = l.y + dy * i;
                        Tile other = world.tile(rx, ry);
                        if (other != null && other.build != null && other.build.damaged()) {
                            dest = other;
                            break;
                        }
                    }

                    facing[side][p] = dest;
                }
            }
        }

        protected void healFacing() {
            for (Tile[] arr : facing) {
                for (Tile t : arr) {
                    if (t == null) continue;
                    Building other = t.build;
                    if (other == null || other.checkSuppression()) continue;

                    other.heal(t.block().health * (healPercent / 100));
                    Fx.healBlockFull.at(other.x, other.y, other.block.size, Pal.heal, other.block);
                }
            }
        }
    }
}
