package fos.type.gen;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.noise.*;
import fos.content.FOSBlocks;
import fos.content.FOSTeam;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.g3d.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static fos.content.FOSBlocks.*;

public class FOSAsteroidGenerator extends BlankPlanetGenerator {
    //schematic used as launch loadout
    public String launchSchem = "bXNjaAF4nFVQzWrDMAyWs5Kk6WBj0LteIA/RF9ilsMvYwXNEFnDlYMcNe/tJ9mGbDP7k78cyhiMcH+DA9kbwcsEruUgbXt0X3ey2ODhNlFxc1m0JDG+vdKeIc2C2OC93wu+QMa/DX95LXukp7PxPiJnRxpB5Gi48oVxMsVih9faTfIKn9z3smAKm8oyPAzzLcLuOu/V+9DbOBKcUpBlXy+Rh+JXh0YVII2fnKScAOEOpxuhWoZVGqoOmASO9UWjBmMILqWcj3yFLM+IsWq/xtlpatSh0lew121Wt05CCmgSKpa/Te53+A19tSSQ=";

    public Block defaultFloor = Blocks.stone;

    public int amount = 28, octaves = 2;
    public float radMin = 32f, radMax = 59f, persistence = 0.4f, scale = 30f, mag = 0.46f, thresh = 0.8f;
    public float elithiteChance = 0f, elbiumChance = 0f, nethratiumChance = 0f;
    public float tinScl = 1f, silverScl = 1f, lithiumScl = 1f;

    void asteroid(int ax, int ay, int rad) {
        Floor floor = (
            rand.chance(elithiteChance) ? elithite :
            rand.chance(elbiumChance) ? elbium :
            rand.chance(nethratiumChance) ? nethratium :
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

    void asteroid(int ax, int ay, int rad, Floor floor) {
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

        //the center asteroid is always nethratium
        asteroid(sx, sy, rand.random(40, 60), nethratium.asFloor());

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
            if (floor == bg || block == Blocks.cliff || Ridged.noise2d(seed + 1, x, y, 4, 0.7f, 1f / 60f) > 0.45f || Mathf.within(x, y, sx, sy, 20 + Ridged.noise2d(seed, x, y, 3, 0.5f, 1f / 30f) * 6f)) return;

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

        //titanium around elithite walls
        oreAround(Blocks.oreTitanium, elithiteWall, 2, 1f, 0.2f);

        //second cliff layer
        cliffs();

        //second wall layer
        pass((x, y) -> {
            if (floor == bg || block == Blocks.cliff || Ridged.noise2d(seed + 1, x, y, 4, 0.7f, 1f / 60f) > 0.45f || Mathf.within(x, y, sx, sy, 20 + Ridged.noise2d(seed, x, y, 3, 0.5f, 1f / 30f) * 6f)) return;

            for (Point2 p : Geometry.d8) {
                Tile other = tiles.get(x + p.x, y + p.y);
                if (other != null && other.block() == Blocks.cliff) {
                    return;
                }
            }

            int rad = 6;
            for (int dx = x-rad; dx <= x-rad; dx++) {
                for (int dy = y-rad; dy <= y+rad; dy++) {
                    if(Mathf.within(dx, dy, x, y, rad + 0.0001f) && tiles.in(dx, dy) && tiles.getn(dx, dy).floor() == bg) {
                        return;
                    }
                }
            }
            block = floor.asFloor().wall;
        });

        //add a bit more environment
        pass((x, y) -> {
            if (Ridged.noise2d(8, x, y, 5, 0.6f, 1f / 60f) > 0.3f) {
                Block replace = (
                    floor == nethratium ? Blocks.yellowStone :
                    floor == elbium ? Blocks.rhyolite :
                    floor == elithite ? Blocks.ferricStone :
                    Blocks.empty
                );
                if (replace == Blocks.empty) return;

                floor = replace.asFloor();
                if (block != Blocks.empty && block != Blocks.air && block != Blocks.cliff) block = replace.asFloor().wall;
            }
        });

        //generate tin and lithium on elbium
        ore(oreTin, elbium, 4f, 0.6f * tinScl);
        ore(oreLithium, elbium, 4f, 0.8f * lithiumScl);

        //generate silver and titanium on elithite
        ore(oreSilver, elithite, 4f, 0.7f * silverScl);

        Schematics.placeLoadout(Schematics.readBase64(launchSchem), sx, sy, Team.sharded);
    }

    @Override
    public Schematic getDefaultLoadout() {
        return Schematics.readBase64(launchSchem);
    }

    @Override
    public int getSectorSize(Sector sector) {
        return 700;
    }
}
