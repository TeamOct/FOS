package fos.type.gen;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import fos.content.FOSTeam;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;

import static fos.content.FOSBlocks.*;
import static mindustry.Vars.*;
import static mindustry.graphics.g3d.PlanetGrid.*;

public class LuminaPlanetGenerator extends PlanetGenerator {
    //schematic used as launch loadout
    String launchSchem = "bXNjaAF4nGNgZmBmZmDJS8xNZeBzSizOTFZwyy8qKUotLmbgTkktTi7KLCjJzM9jYGBgy0lMSs0pZmCKjmVkEEzLL9ZNzi9K1U2DKWdgYAQhIAEAzp0V0Q==";
    LuminaBaseGenerator basegen = new LuminaBaseGenerator();
    float scl = 8f;

    Block[][] arr = {
        {crimsonStone, purpur, crimsonStone, purpur, crimsonStone, purpur},
        {crimsonStone, purpur, purpur, crimsonStone, purpur, purpur},
        {purpur, blublu, purpur, blublu, purpur, blublu},
        {blublu, annite, blublu, annite, annite, annite},
        {annite, cyanium, cyanium, cyanium, annite, annite},
        {cyanium, cyanium, cyanium, annite, cyanium, cyanium}
    };

    @Override
    public float getHeight(Vec3 position) {
        position = Tmp.v33.set(position).scl(scl);
        return Mathf.pow(Simplex.noise3d(seed, 7, 0.5f, 1f/3f, position.x, position.y, position.z), 3f);
    }

    @Override
    protected void genTile(Vec3 position, TileGen tile) {
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if (Ridged.noise3d(seed, position.x, position.y, position.z, 22) > 0.18f) {
            tile.block = Blocks.air;
        }
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

    Block getBlock(Vec3 position) {
        float height = getHeight(position);
        Tmp.v31.set(position);

        position = Tmp.v33.set(position).scl(scl);
        float rad = scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2) / rad);
        float tnoise = Simplex.noise3d(seed, 7, 0.56, 1f/3f, position.x, position.y + 999, position.z);

        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 1.2f;
        height = Mathf.clamp(height);

        return arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
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

        for (int i = 0; i < 360; i += angleStep){
            int angle = offset + i;
            int cx = (int)Math.floor(width / 2f + Angles.trnsx(angle, length));
            int cy = (int)Math.floor(height / 2f + Angles.trnsy(angle, length));

            if (i + angleStep >= 360){
                spawn = new Room(cx, cy, rand.random(10, 18));
                roomseq.add(spawn);

                for(int j = 0; j < enemySpawns; j++){
                    float enemyOffset = rand.range(60);

                    Tmp.v1.set(cx - width / 2f, cy - height / 2f).rotate(180 + enemyOffset).add(width / 2f, height / 2f);
                    Room espawn = new Room((int)Math.floor(Tmp.v1.x), (int)Math.floor(Tmp.v1.y), rand.random(10, 16));
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
        float poles = Math.abs(sector.tile.v.y);
        float nmag = 0.5f;
        float scl = 0.8f;
        float addscl = 1.3f;

        FloatSeq frequencies = new FloatSeq();
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.01f, 0.07f) - i * 0.01f + poles * 0.04f);
        }

        pass((x, y) -> {
            if (!floor.asFloor().hasSurface()) return;

            float offsetX = x - 4, offsetY = y + 23;
            for(int i = ores.size - 1; i >= 0; i--){
                Block entry = ores.get(i);
                float freq = frequencies.get(i);
                if (Math.abs(0.5 - noise(offsetX, offsetY + i * 999, 2, 0.7f, (40 + i * 2))) > 0.22f + i * 0.01 &&
                Math.abs(0.5 - noise(offsetX, offsetY - i * 999, 1, 1, (30 + i * 4))) > 0.33f + freq){
                    ore = entry;
                    break;
                }
            }
        });

        trimDark();
        median(2);
        tech(Blocks.metalFloor, Blocks.metalFloor2, Blocks.darkMetal);

        oreAround(alienMoss, blubluWall, 2, 1f, 0f);

        float difficulty = sector.threat;
        ints.clear();
        ints.ensureCapacity(width * height / 4);

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        enemies.each(espawn -> {
            tiles.getn(espawn.x, espawn.y).setBlock(bugSpawn, FOSTeam.bessin);
            tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn);
        });

        if (sector.hasEnemyBase()){
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.get(spawn.x, spawn.y), Team.sharded, sector, difficulty);
            state.rules.attackMode = sector.info.attack = true;
            state.rules.waveTeam = Team.sharded;
        }

        float waveTimeDec = 0.4f;

        state.rules.waveSpacing = Mathf.lerp(60 * 360, 60 * 120, (float)Math.floor(Math.max(difficulty - waveTimeDec, 0) / 0.8f));
        state.rules.spawns = LuminaWaves.generate(difficulty, new Rand(), state.rules.attackMode);
    }

    //TODO because the generator is not complete, I do not allow you to enter, unless you know what you're doing
    @Override
    public boolean allowLanding(Sector sector) {
        return false;
    }

    @Override
    public Schematic getDefaultLoadout() {
        return Schematics.readBase64(launchSchem);
    }

    @Override
    public void postGenerate(Tiles tiles) {
        //TODO add something later...
    }
}
