package fos.type.gen;

import arc.math.*;
import arc.math.geom.*;
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
    /** Schematic used as the launch loadout.*/
    public String launchSchem = "bXNjaAF4nFVQzWrDMAyWs5Kk6WBj0LteIA/RF9ilsMvYwXNEFnDlYMcNe/tJ9mGbDP7k78cyhiMcH+DA9kbwcsEruUgbXt0X3ey2ODhNlFxc1m0JDG+vdKeIc2C2OC93wu+QMa/DX95LXukp7PxPiJnRxpB5Gi48oVxMsVih9faTfIKn9z3smAKm8oyPAzzLcLuOu/V+9DbOBKcUpBlXy+Rh+JXh0YVII2fnKScAOEOpxuhWoZVGqoOmASO9UWjBmMILqWcj3yFLM+IsWq/xtlpatSh0lew121Wt05CCmgSKpa/Te53+A19tSSQ=";

    public Block defaultFloor = Blocks.stone;

    public int min = 19, max = 19, octaves = 2;
    public float radMin = 32f, radMax = 59f, persistence = 0.4f, scale = 30f, mag = 0.46f, thresh = 0.8f;
    public float elithiteChance = 0f, elbiumChance = 0f, nethratiumChance = 0f;
    public float tinScl = 1f, silverScl = 1f, lithiumScl = 1f;

    void asteroid(int ax, int ay, int rad) {
        Floor floor = (
            rand.chance(elithiteChance) ? FOSBlocks.elithite :
            rand.chance(elbiumChance) ? FOSBlocks.elbium :
            rand.chance(nethratiumChance) ? FOSBlocks.nethratium :
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

        //cliffs around asteroids
        pass((x, y) -> {
            if (floor == bg) return;

            block = floor.asFloor().wall;
        });
        cliffs();

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
            block = floor.asFloor().wall;
        });

        //generate tin and lithium on elbium
        ore(FOSBlocks.oreTin, FOSBlocks.elbium, 4f, 0.6f * tinScl);
        ore(FOSBlocks.oreLithium, FOSBlocks.elbium, 4f, 0.8f * lithiumScl);

        //generate silver and titanium on elithite
        oreAround(Blocks.oreTitanium, FOSBlocks.elithiteWall, 2, 1f, 0.2f);
        ore(FOSBlocks.oreSilver, FOSBlocks.elithite, 4f, 0.7f * silverScl);

        //TODO place enemy spawn in the first place?
        /*int spawnSide = rand.random(3);
        int sizeOffset = width / 2 - 1;
        tiles.getn(sizeOffset * Geometry.d8edge[spawnSide].x + width/2, sizeOffset * Geometry.d8edge[spawnSide].y + height/2).setOverlay(Blocks.spawn);*/

        Schematics.placeLaunchLoadout(sx, sy);

        Vars.state.rules.planetBackground = new PlanetParams(){{
            planet = sector.planet.parent;
            zoom = 0.3f;
            camPos = new Vec3(0f, 0f, 45f);
        }};

        Vars.state.rules.dragMultiplier = 0.2f; //it's space after all, so very little drag
        Vars.state.rules.borderDarkness = false;
        Vars.state.rules.waves = false;
    }

    @Override
    public Schematic getDefaultLoadout() {
        return Schematics.readBase64(launchSchem);
    }

    @Override
    public int getSectorSize(Sector sector) {
        return 500;
    }
}
