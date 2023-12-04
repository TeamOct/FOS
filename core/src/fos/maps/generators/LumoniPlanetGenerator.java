package fos.maps.generators;

import arc.graphics.Color;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.Tmp;
import arc.util.noise.*;
import fos.content.FOSTeam;
import fos.maps.generators.bases.LumoniBaseGenerator;
import mindustry.ai.Astar;
import mindustry.content.Blocks;
import mindustry.game.*;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;

import static fos.content.FOSBlocks.*;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;
import static mindustry.graphics.g3d.PlanetGrid.Ptile;

public class LumoniPlanetGenerator extends PlanetGenerator {
    /** Enemy base generator. */
    LumoniBaseGenerator basegen = new LumoniBaseGenerator();
    float scl = 8f;

    Block[][] arr = {
        {crimsonStone, purpur, crimsonStone, purpur, crimsonStone, purpur},
        {crimsonStone, purpur, purpur, crimsonStone, purpur, purpur},
        {purpur, blublu, purpur, blublu, purpur, blublu},
        {blublu, purpur, blublu, purpur, purpur, purpur},
        {annite, cyanium, cyanium, cyanium, annite, annite},
        {cyanium, cyanium, cyanium, annite, cyanium, cyanium}
    };

    @Override
    public float getHeight(Vec3 position) {
        if (isWater(position, 0.2f)) return 0.1f;

        position = Tmp.v33.set(position).scl(scl);
        return Mathf.pow(Simplex.noise3d(seed, 5, 0.5f, 1f/3f, position.x, position.y, position.z), 2f);
    }

    @Override
    protected void genTile(Vec3 position, TileGen tile) {
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;
        if (tile.block == sandWall) tile.block = air;

        if (Ridged.noise3d(seed, position.x, position.y, position.z, 22) > 0.18f) {
            tile.block = air;
        }
    }

    protected float waterNoise(Vec3 pos, double octaves, double falloff, double scl, float mag) {
        return waterNoise(pos, octaves, falloff, scl, mag, 0f);
    }

    protected float waterNoise(Vec3 pos, double octaves, double falloff, double scl, float mag, float offset) {
        return Simplex.noise3d(seed, octaves, falloff, scl, pos.x + offset, pos.y + offset, pos.z + offset) * mag;
    }

    protected boolean isWater(Vec3 position, float value) {
        float wnoise1 = waterNoise(position, 7, 0.5, 1f, 0.56f);
        float wnoise2 = waterNoise(position, 9, 0.32, 1f/3f, 0.63f, 69f);
        float wnoise3 = waterNoise(position, 8, 0.26, 1f/6f, 0.61f, 420f);
        return wnoise1 < value || wnoise2 < value || wnoise3 < value;
    }

    @Override
    public void generateSector(Sector sector) {
        Ptile tile = sector.tile;
        float meridian = tile.v.x, poles = tile.v.y;

        //an arc of enemy bases
        if ((meridian < -0.32f && poles < -0.27f && poles > -0.9f) || (meridian > -0.29f && meridian < 0.21f && poles > -0.12f && poles < 0.68f)){
            sector.generateEnemyBase = true;
        }
    }

    @Override
    public Color getColor(Vec3 position) {
        Block block = getBlock(position);
        return Tmp.c1.set(block.mapColor).a(1 - block.albedo);
    }

    Block getSolidBlock(Vec3 position) {
        float height = getHeight(position);

        Tmp.v31.set(position);

        position = Tmp.v33.set(position).scl(scl);
        float rad = scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2) / rad);
        float tnoise = Simplex.noise3d(seed, 7, 0.56, 1f/3f, position.x, position.y + 999, position.z);

        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 1.2f;
        height = Mathf.clamp(height);

        Block b = arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
        if (Simplex.noise3d(seed, 5, 0.6f, 1f / scl, position.x, position.y, position.z) > 0.4f) {
            b = getAlt(b);
        }
        if (isWater(position, 0.2f)) {
            b = getFlooded(b);
        }

        return b;
    }

    Block getFlooded(Block b) {
        return b == annite ? anniteWater :
            b == blublu ? blubluWater :
                b == crimsonStone ? crimsonStoneWater :
                    b == cyanium ? cyaniumWater :
                        b == purpur ? purpurWater :
                            b;
    }

    Block getAlt(Block b) {
        return content.block(b.name + "-alt") != null ? content.block(b.name + "-alt") : b;
    }

    Block getBlock(Vec3 position) {
        if (isWater(position, 0.18f)) return deepwater;
        if (Simplex.noise3d(seed, 5, 0.8f, 1, position.x, position.y, position.z) > 0.8f) return tokiciteFloor;

        return getSolidBlock(position);
    }

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag) {
        Vec3 v = sector.rect.project(x, y).scl(5);
        return Simplex.noise3d(seed, octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float)mag;
    }

    @Override
    public void generate(Tiles tiles, Sector sec, int seed) {
        this.tiles = tiles;
        this.seed = seed;
        this.sector = sec;
        this.width = tiles.width;
        this.height = tiles.height;
        this.rand.setSeed(seed);

        TileGen gen = new TileGen();
        tiles.each((x, y) -> {
            gen.reset();
            Vec3 pos = sector.rect.project((float)x / tiles.width, (float)y / tiles.height);

            genTile(pos, gen);
            tiles.set(x, y, new Tile(x, y, gen.floor, gen.overlay, gen.block));
        });

        class Room {
            final int x, y, radius;
            final ObjectSet<Room> connected = new ObjectSet<>();

            Room(int x, int y, int radius) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                connected.add(this);
            }

            void join(int x1, int y1, int x2, int y2){
                float nscl = rand.random(20, 60);
                int stroke = rand.random(4, 12);
                brush(pathfind(x1, y1, x2, y2, tile -> (tile.solid() ? 27f : 0f) + noise(tile.x, tile.y, 2, 2f, 1f / nscl) * 60, Astar.manhattan), stroke);
            }

            void connect(Room to) {
                if (!connected.add(to) || to == this) return;

                Vec2 midpoint = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                rand.nextFloat();
                midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x, y)));
                midpoint.sub(width/2f, height/2f).limit(width / 2f / Mathf.sqrt3).add(width/2f, height/2f);
                int mx = (int) midpoint.x, my = (int) midpoint.y;

                join(x, y, mx, my);
                join(mx, my, to.x, to.y);
            }
        }

        cells(7);
        distort(10, 12);

        width = tiles.width;
        height = tiles.height;

        float constraint = 1.3f;
        float radius = width / 2f / Mathf.sqrt3;
        int rooms = rand.random(6, 8);
        Seq<Room> roomseq = new Seq<>();

        for (int i = 0; i < rooms; i++){
            Tmp.v1.trns(rand.random(360), rand.random(radius / constraint));
            double rx = Math.floor(width / 2f + Tmp.v1.x);
            double ry = Math.floor(height / 2f + Tmp.v1.y);
            float maxrad = radius - Tmp.v1.len();
            double rrad = Math.floor(Math.min(rand.random(9, maxrad / 2f), 30));

            roomseq.add(new Room((int)rx, (int)ry, (int)rrad));
        }

        Room spawn = null;
        Seq<Room> enemies = new Seq<>();
        int enemySpawns = rand.random(1, Math.max(Mathf.floor(sector.threat * 4), 1));
        int offset = rand.nextInt(360);
        float length = (float)(width / 2.55 - rand.random(13, 23));
        int angleStep = 5;
        ObjectMap<Room, Integer> islands = new ObjectMap<>();

        for (int i = 0; i < 360; i += angleStep){
            int angle = offset + i;
            int cx = (int)Math.floor(width / 2f + Angles.trnsx(angle, length));
            int cy = (int)Math.floor(height / 2f + Angles.trnsy(angle, length));

            if (i + angleStep >= 360){
                spawn = new Room(cx, cy, rand.random(10, 18));
                if (tiles.get(spawn.x, spawn.y).floor() == deepwater) {
                    int rad = rand.random(48, 100);
                    island(spawn.x, spawn.y, rad);
                    islands.put(spawn, rad);
                }
                roomseq.add(spawn);

                for(int j = 0; j < enemySpawns; j++){
                    float enemyOffset = rand.range(60);

                    Tmp.v1.set(cx - width / 2f, cy - height / 2f).rotate(180 + enemyOffset).add(width / 2f, height / 2f);
                    Room espawn = new Room((int)Math.floor(Tmp.v1.x), (int)Math.floor(Tmp.v1.y), rand.random(10, 16));
                    if (tiles.get(espawn.x, espawn.y).floor() == deepwater) {
                        int rad = rand.random(24, 50);
                        island(espawn.x, espawn.y, rad);
                        islands.put(espawn, rad);
                    }
                    roomseq.add(espawn);
                    enemies.add(espawn);
                }
                break;
            }
        }

        roomseq.each(room -> erase(room.x, room.y, room.radius));

        int connections = rand.random(Math.max(rooms - 1, 1), rooms + 3);
        for(int i = 0; i < connections; i++){
            roomseq.random(rand).connect(roomseq.random(rand));
        }

        for(Room room : roomseq){
            spawn.connect(room);
        }

        cells(1);
        distort(10, 6);

        inverseFloodFill(tiles.getn(spawn.x, spawn.y));

        Seq<Block> ores = Seq.with(oreTin, oreTinSurface, oreSilver);
        if (sector.id != 91) {
            ores.add(oreDiamond);
            ores.add(oreCopper);
        }

        float poles = Math.abs(sector.tile.v.y);

        if (poles < 0.63f) {
            ores.add(oreVanadium);
        }
        if (poles < 0.49f) {
            ores.add(oreIridium);
        }
        if (poles < 0.34f) {
            ores.add(oreLuminium);
        }

        FloatSeq frequencies = new FloatSeq();
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.01f, 0.07f) - i * 0.01f + poles * 0.04f);
        }

        //island ores
        for (ObjectMap.Entry<Room, Integer> r : islands) {
            ores(ores, r.key.x, r.key.y, r.value);
        }
        //map ores
        ores(ores);
        //early-game ores next to player's core
        ores(Seq.with(oreTinSurface, oreTin, oreSilver), spawn.x, spawn.y, 20);

        trimDark();
        median(2);
        //tech(metalFloor, metalFloor2, darkMetal);

        oreAround(alienMoss, blubluWall, 2, 1f, 0f);

        pass((x, y) -> {
            //trees on purpur biome
            if (floor == purpur && block == air) {
                if (rand.chance(0.01)){
                    for (Point2 p : Geometry.d8) {
                        Tile other = tiles.get(x + p.x, y + p.y);
                        //if there's already a tree nearby, do nothing
                        if (other.block() == whiteTree) return;
                    }
                    if (rand.chance(0.1)) {
                        block = whiteTree;
                    }
                }
            }

            //shallow water
            if (floor == deepwater) {
                for (Point2 p : Geometry.d8) {
                    Tile other = tiles.get(x + p.x, y + p.y);

                    if (other != null && other.floor() != deepwater && other.floor() != getFlooded(other.floor())) {
                        other.circle(2, t -> {
                            if (t.floor() != getFlooded(other.floor()))
                                t.setFloor(getFlooded(other.floor()).asFloor());
                        });
                        break;
                    }
                }
            }

            //tokicite
            if ((floor == annite || floor == blublu) && block == air) {
                //tokicite and water don't mix.
                for (Point2 p : Geometry.d4) {
                    Tile other = tiles.get(x + p.x, y + p.y);
                    if (other.floor().isLiquid && other.floor() != tokiciteFloor) return;
                }

                if (noise(x + 69, y - 69, 2, 0.6, 80) > 0.86f) {
                    floor = tokiciteFloor;
                    ore = air;
                }
            }

            //arkycite puddles
            if (floor == cyanium && block == air) {
                //don't mix multiple liquids
                for (Point2 p : Geometry.d4) {
                    Tile other = tiles.get(x + p.x, y + p.y);
                    if (other.floor().isLiquid && other.floor() != arkyciteFloor) return;
                }

                //only generate 1x1 puddles with a certain distance between them
                /* java sucks */ final boolean[] isFree = {true};
                tiles.get(x, y).circle(36, t -> {
                    if (t.floor() == arkyciteFloor) isFree[0] = false;
                });

                if (noise(x * 3f + 236, y * 3f + 213, 2, 0.6, 80) < 0.17f && isFree[0]) {
                    //arkyic stone around arkycite puddles
                    tiles.get(x, y).circle(3, t ->
                        t.setFloor(arkyicStone.asFloor())
                    );
                    tiles.get(x, y).circle(4, t -> {
                        if (rand.random(1f) < 0.4f) t.setFloor(arkyicStone.asFloor());
                    });

                    //more arkycite because 1x1 puddles all around do not look natural
                    tiles.get(x, y).circle(2, t -> {
                        if (rand.random(1f) < 0.25f) t.setFloor(arkyciteFloor.asFloor());
                    });

                    floor = arkyciteFloor;
                    ore = air;
                }
            }

            //NO SAND WALLS.
            if (block == sandWall) block = air;
        });

        float difficulty = sector.threat;
        ints.clear();
        ints.ensureCapacity(width * height / 4);

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        enemies.each(espawn -> {
            tiles.getn(espawn.x, espawn.y).setBlock(bugSpawn, FOSTeam.bessin);
            tiles.getn(espawn.x, espawn.y).circle(7, t -> t.setFloor(hiveFloor.asFloor()));
            tiles.getn(espawn.x, espawn.y).circle(11, t -> {
                if (rand.chance(0.35f)) t.setFloor(hiveFloor.asFloor());
            });
            tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn);
        });

        if (sector.hasEnemyBase()){
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.get(spawn.x, spawn.y), Team.sharded, sector, difficulty);
        }

        float waveTimeDec = 0.4f;

        state.rules.attackMode = sector.hasEnemyBase();
        state.rules.waveSpacing = Mathf.lerp(60 * 360, 60 * 120, (float)Math.floor(Math.max(difficulty - waveTimeDec, 0) / 0.8f));
        state.rules.spawns = LumoniWaves.generate(difficulty, new Rand(), state.rules.attackMode);
    }

    /**
     * This method ensures that the given ores generate in a limited square area, no matter how many tries it takes.
     * This probably hurts sector generation time quite a bit, but I dunno what else to try.
     * @param ores List of ores to generate.
     * @param cx Center X coordinate.
     * @param cy Center Y coordinate.
     * @param rad A "radius" of a square (very dumb description, I know.)
     */
    public void ores(Seq<Block> ores, int cx, int cy, int rad) {
        for (Block cur : ores) {
            boolean generated = false;
            int offset = 0;

            while (!generated) {
                for (int x = -rad; x <= rad; x++) {
                    for (int y = -rad; y <= rad; y++) {
                        Tile tile = tiles.get(x + cx, y + cy);
                        if (tile == null || !tile.floor().hasSurface() || !tile.floor().supportsOverlay) return;

                        int i = ores.indexOf(cur);
                        int offsetX = x - 4, offsetY = y + 23;
                        if (Math.abs(0.5f - noise(offsetX + offset, offsetY + i * 999, 2, 0.7, (40 + i * 2))) > 0.26f &&
                            Math.abs(0.5f - noise(offsetX + offset, offsetY - i * 999, 1, 1, (30 + i * 4))) > 0.37f) {
                            ore = cur;
                            if (tile.block() == air) generated = true;
                        }
                    }
                }

                if (!generated) {
                    offset += 1000;
                }
            }
        }
    }

    @Override
    public void trimDark() {
        for (Tile tile : tiles) {
            boolean any = world.getDarkness(tile.x, tile.y) > 0;
            for (int i = 0; i < 4 && !any; i++){
                any = world.getDarkness(tile.x + Geometry.d4[i].x, tile.y + Geometry.d4[i].y) > 0;
            }

            if (any && tile.floor() != deepwater) {
                tile.setBlock(tile.floor().wall);
            }
        }
    }

    public void island(int ix, int iy, int rad) {
        Vec3 pos = sector.rect.project((float)ix / tiles.width, (float)iy / tiles.height);
        Floor floor = getSolidBlock(pos).asFloor();

        for(int x = ix - rad; x <= ix + rad; x++) {
            for (int y = iy - rad; y <= iy + rad; y++) {
                if (tiles.in(x, y) && Mathf.dst(x, y, ix, iy) / rad + Simplex.noise2d(seed, 2, 0.4f, 1f / 30f, x, y) * 0.41f < 0.75f) {
                    tiles.getn(x, y).setFloor(floor);

                    if (tiles.in(x, y) && Simplex.noise2d(seed + 44, 2, 0.4f, 1f / 30f, x, y) < 0.75f) {
                        tiles.getn(x, y).setBlock(floor.wall);
                    }
                }
            }
        }
    }

    @Override
    public boolean allowLanding(Sector sector) {
        //TODO still don't allow landing, the planet isn't finished
        return false;
    }

    @Override
    public void postGenerate(Tiles tiles) {
        //TODO
    }
}
