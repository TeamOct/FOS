package fos.ui.menus;

import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.noise.Simplex;
import fos.content.FOSTeam;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;
import mindustry.world.*;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSPlanets.*;
import static mindustry.content.Blocks.air;

public class FOSMenus {
    public static MenuBackground uxerdSpace, lumoniSpace, random, solarSystem, caldemoltSystem, lumoniTerrain;

    public static void load() {
        uxerdSpace = new SpaceMenuBackground(){{
            params = new PlanetParams(){{
                planet = uxerd;
                zoom = 1.2f;
            }};
        }};
        lumoniSpace = new SpaceMenuBackground(){{
            params = new PlanetParams(){{
                planet = lumoni;
            }};
        }};
        random = new SpaceMenuBackground(){{
            params = new PlanetParams(){{
                Seq<Planet> visible = Vars.content.planets().select(p -> p.visible);
                planet = visible.get(Mathf.floor((float) (Math.random() * visible.size)));
            }};
        }};
        solarSystem = new SpaceMenuBackground(){{
            params = new PlanetParams(){{
                planet = Planets.sun;
                camPos = new Vec3(0.01, 1, 0);
                zoom = 12f;
            }};
        }};
        caldemoltSystem = new SpaceMenuBackground(){{
            params = new PlanetParams(){{
                planet = caldemolt;
                camPos = new Vec3(0.01, 1, 0);
                zoom = 6f;
            }};
        }};
        lumoniTerrain = new TerrainMenuBackground(){
            @Override
            public void generate(Tiles tiles) {
                Seq<Block> ores = Seq.with(oreTinSurface, Blocks.oreCopper, oreDiamond, oreLuminium);
                int offset = Mathf.floor((float) (Math.random() * 100000));
                int s2 = offset + 1, s3 = offset + 2;
                Block[][] blocks = new Block[][]{
                    {cyanium, cyaniumWall},
                    {annite, anniteWall},
                    {blublu, blubluWall},
                    {cyanium, cyaniumWall},
                    {annite, anniteWall},
                    {blublu, blubluWall},
                    {purpur, purpurWall},
                    {crimsonStone, crimsonStoneWall}
                };
                Block[][] blocks2 = new Block[][]{
                    {blublu, blubluWall},
                    {blublu, blubluWall},
                    {purpur, purpurWall},
                    {purpur, purpurWall},
                    {crimsonStone, crimsonStoneWall},
                    {crimsonStone, crimsonStoneWall}
                };

                Block[] selected = blocks[Mathf.floor((float) (Math.random() * blocks.length))];
                Block[] selected2 = blocks2[Mathf.floor((float) (Math.random() * blocks2.length))];

                Block ore1 = ores.get(Mathf.floor((float) (Math.random() * ores.size)));
                ores.remove(ore1);
                Block ore2 = ores.get(Mathf.floor((float) (Math.random() * ores.size)));

                double tr1 = Mathf.random(0.65f, 0.85f);
                double tr2 = Mathf.random(0.65f, 0.85f);

                Block floord = selected[0], walld = selected[1];
                Block floord2 = selected2[0], walld2 = selected2[1];

                for(int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        Block floor = floord;
                        Block ore = air;
                        Block wall = air;

                        if (Simplex.noise2d(offset, 3, 0.5, 1 / 20.0, x, y) > 0.5) {
                            wall = walld;
                        }

                        if (Simplex.noise2d(s3, 3, 0.5, 1 / 20.0, x, y) > 0.5) {
                            floor = floord2;
                            if (wall != air) {
                                wall = walld2;
                            }
                        }

                        if (Simplex.noise2d(s2, 3, 0.3, 1 / 30.0, x, y) > tr1) {
                            ore = ore1;
                        }

                        if (Simplex.noise2d(s2, 2, 0.2, 1 / 15.0, x, y + 99999) > tr2) {
                            ore = ore2;
                        }
                        setTile(x, y, floor, wall, ore, tiles);
                    }
                }

                var center = tiles.get(tiles.width / 2, tiles.height / 2);
                center.setBlock(coreFortress, FOSTeam.corru);
            }
        };
    }
}
