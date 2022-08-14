package fos.type.gen;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import fos.content.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.graphics.g3d.PlanetGrid.*;

public class LuminaPlanetGenerator extends PlanetGenerator {
    BaseGenerator basegen = new BaseGenerator();
    float scl = 5f;
    float waterOffset = 0.07f;

    Block[][] arr = {
            {Blocks.darksand, Blocks.darksand, Blocks.dacite, Blocks.dacite},
            {Blocks.darksand, Blocks.dacite, Blocks.dacite, Blocks.stone},
            {Blocks.dacite, Blocks.dacite, FOSBlocks.cyanium, FOSBlocks.cyanium},
            {FOSBlocks.cyanium, FOSBlocks.cyanium, FOSBlocks.cyanium, FOSBlocks.cyanium}
    };

    //TODO make a planet have actual mountains instead of being shaped as a sphere
    @Override
    public float getHeight(Vec3 position) {
        position = Tmp.v33.set(position).scl(scl);
        return (Mathf.pow(Simplex.noise3d(seed, 7, 0.5f, 1f/3f, position.x, position.y, position.z), 2.3f) + waterOffset) / (1f + waterOffset);
    }

    @Override
    protected void genTile(Vec3 position, TileGen tile) {
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;
        if (tile.floor == FOSBlocks.cyanium) tile.block = FOSBlocks.cyaniumWall;

        if (Ridged.noise3d(seed, position.x, position.y, position.z, 22) > 0.32) {
            tile.block = Blocks.air;
        }
    }

    @Override
    public void generateSector(Sector sector) {
        Ptile tile = sector.tile;
        float poles = Math.abs(tile.v.y);
        float noise = Noise.snoise3(tile.v.x, tile.v.y, tile.v.z, 0.001f, 0.58f);

        if (noise + poles / 7.1 > 0.25 && poles > 0.23){
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
        this.sector = sec;

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

            void connect(Room to) {
                if (!connected.add(to) || to == this) return;

                float nscl = rand.random(20, 60);
                int stroke = rand.random(4, 12);

                brush(pathfind(x, y, to.x, to.y, tile -> (tile.solid() ? 5 : 0) + noise(x, y, 1, 1, 1 / nscl) * 60, Astar.manhattan), stroke);
            }
        }

        cells(4);
        distort(10, 12);

        width = tiles.width;
        height = tiles.height;

        float constraint = 1.3f;
        float radius = width / 2f / Mathf.sqrt3;
        int rooms = rand.random(2, 5);
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

        Seq<Block> ores = Seq.with(FOSBlocks.oreTin);
        float poles = Math.abs(sector.tile.v.y);
        float nmag = 0.5f;
        float scl = 1;
        float addscl = 1.3f;

        if (Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 1, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.5f * addscl){
            ores.add(FOSBlocks.oreSilver);
        }

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
                if (Math.abs(0.5 - noise(offsetX, offsetY + i * 999, 2, 0.7f, (40 + i * 2))) > 0.24f + i * 0.01 &&
                Math.abs(0.5 - noise(offsetX, offsetY - i * 999, 1, 1, (30 + i * 4))) > 0.38f + freq){
                    ore = entry;
                    break;
                }
            }

            if (ore == Blocks.oreScrap && rand.chance(0.5)) {
                floor = Blocks.metalFloorDamaged;
            }
        });

        trimDark();
        median(2);
        tech();
        pass((x, y) -> {});

        float difficulty = sector.threat;
        ints.clear();
        ints.ensureCapacity(width * height / 4);

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        enemies.each(espawn -> tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn));

        if (sector.hasEnemyBase()){
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.get(spawn.x, spawn.y), Vars.state.rules.waveTeam, sector, difficulty);
            Vars.state.rules.attackMode = sector.info.attack = true;
        } else {
            Vars.state.rules.winWave = sector.info.winWave = 10 + 5 * (int)Math.max(difficulty * 10, 1);
        }

        float waveTimeDec = 0.4f;

        Vars.state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2, 60 * 60, (float)Math.floor(Math.max(difficulty - waveTimeDec, 0) / 0.8f));
        Vars.state.rules.waves = sector.info.waves = true;
        Vars.state.rules.enemyCoreBuildRadius = 300;
        Vars.state.rules.spawns = Waves.generate(difficulty, new Rand(), Vars.state.rules.attackMode);

        Weather.WeatherEntry weather = new Weather.WeatherEntry(FOSWeathers.wind);
        weather.always = true; //always windy
        Vars.state.rules.weather = Seq.with(weather);
    }

    @Override
    public void postGenerate(Tiles tiles) {
        if (sector.hasEnemyBase())
            basegen.postGenerate();
    }
}
