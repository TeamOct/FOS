package fos.type.gen;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.noise.*;
import fos.content.FOSBlocks;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.g3d.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

public class FOSAsteroidGenerator extends BlankPlanetGenerator {
    public Block defaultFloor = FOSBlocks.cyanium;

    public int min = 15, max = 30, octaves = 2;
    public float radMin = 15f, radMax = 40f, persistence = 0.4f, scale = 30f, mag = 0.46f, thresh = 1f;
    public float elithiteChance = 0f, elbiumChance = 0f, iceChance = 0f, meteoriteChance = 0f;
    public float tinScl = 1f, silverScl = 1f, lithiumScl = 1f;

    @Nullable Rand rand;
    int seed;

    void asteroid(int ax, int ay, int rad) {
        Floor floor = (
            rand.chance(elithiteChance) ? FOSBlocks.elithite :
            rand.chance(elbiumChance) ? FOSBlocks.elbium :
            rand.chance(iceChance) ? Blocks.ice :
            rand.chance(meteoriteChance) ? FOSBlocks.meteoriteFloor :
            defaultFloor
        ).asFloor();

        for(int x = ax - rad; x <= ax + rad; x++){
            for (int y = ay - rad; y <= ay + rad; y++){
                if (tiles.in(x, y) && Mathf.dst(x, y, ax, ay) / rad + Simplex.noise2d(seed, octaves, persistence, 1f / scale, x, y) * mag < thresh) {
                    tiles.getn(x, y).setFloor(floor);
                }
            }
        }
    }

    @Override
    public void generate() {
        seed = Vars.state.rules.sector.planet.id;
        int sx = width/2, sy = height/2;
        rand = new Rand(seed);

        Floor bg = Blocks.empty.asFloor();

        tiles.eachTile(t -> t.setFloor(bg));

        asteroid(sx, sy, rand.random(40, 60));

        int amount = rand.random(min, max);
        for(int i = 0; i < amount; i++){
            float rad = rand.random(radMin, radMax), ax = rand.random(rad, width - rad), ay = rand.random(rad, height - rad);
            asteroid((int)ax, (int)ay, (int)rad);
        }

        int smalls = rand.random(min, max) * 3;
        for (int i = 0; i < smalls; i++){
            float rad = rand.random(3, 8), ax = rand.random(rad, width - rad), ay = rand.random(rad, height - rad);
            asteroid((int)ax, (int)ay, (int)rad);
        }

        //walls on asteroids
        pass((x, y) -> {
            if (floor == bg || Ridged.noise2d(seed + 1, x, y, 4, 0.7f, 1f / 60f) > 0.45f || Mathf.within(x, y, sx, sy, 20 + Ridged.noise2d(seed, x, y, 3, 0.5f, 1f / 30f) * 6f)) return;

            int rad = 6;
            for (int dx = x-rad; dx <= x-rad; dx++){
                for (int dy = y-rad; dy <= y+rad; dy++){
                    if(Mathf.within(dx, dy, x, y, rad + 0.0001f) && tiles.in(dx, dy) && tiles.getn(dx, dy).floor() == bg){
                        return;
                    }
                }
            }
            block = floor == FOSBlocks.meteoriteFloor ? FOSBlocks.meteoriteBlock : floor.asFloor().wall;
        });

        //generate tin and lithium on elbium
        ore(FOSBlocks.oreTin, FOSBlocks.elbium, 5f, 0.8f * tinScl);
        ore(FOSBlocks.oreLithium, FOSBlocks.elbium, 4f, 0.8f * lithiumScl);

        //generate silver on elithite
        ore(FOSBlocks.oreSilver, FOSBlocks.elithite, 4f, 0.7f * silverScl);

        int spawnSide = rand.random(3);
        int sizeOffset = width / 2 - 1;
        tiles.getn(sizeOffset * Geometry.d8edge[spawnSide].x + width/2, sizeOffset * Geometry.d8edge[spawnSide].y + height/2).setOverlay(Blocks.spawn);

        Schematics.placeLaunchLoadout(sx, sy);

        Vars.state.rules.planetBackground = new PlanetParams(){{
            planet = sector.planet.parent;
            zoom = 0.75f;
            camPos = new Vec3(0f, 0f, 45f);
        }};

        Vars.state.rules.dragMultiplier = 0.2f; //it's space after all, so very little drag
        Vars.state.rules.borderDarkness = false;
        Vars.state.rules.waves = true;
        Vars.state.rules.showSpawns = true;
        //TODO custom waves
        Vars.state.rules.spawns = Waves.generate(0.5f, rand, false, true, false);
    }

    @Override
    public Schematic getDefaultLoadout() {
        return Loadouts.basicNucleus;
    }

    @Override
    public int getSectorSize(Sector sector) {
        return 500;
    }
}
