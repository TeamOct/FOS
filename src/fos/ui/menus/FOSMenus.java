package fos.ui.menus;

import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.noise.Simplex;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;
import mindustry.world.Block;
import mindustry.world.Tiles;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSPlanets.*;

public class FOSMenus {
    public static MenuBackground uxerdSpace, lumoniSpace, random, solarSystem, lumoniTerrain;

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
                Seq<Planet> visible = Vars.content.planets().copy().filter(p -> p.visible);
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
        lumoniTerrain = new TerrainMenuBackground(){
            @Override
            public void generate(Tiles tiles) {
                Seq<Block> ores = Seq.with(oreTinSurface, oreDiamond, oreLuminium);
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
                boolean tech = Mathf.chance(0.25);

                Block floord = selected[0], walld = selected[1];
                Block floord2 = selected2[0], walld2 = selected2[1];

                for(int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        Block floor = floord;
                        Block ore = Blocks.air;
                        Block wall = Blocks.air;

                        if (Simplex.noise2d(offset, 3, 0.5, 1 / 20.0, x, y) > 0.5) {
                            wall = walld;
                        }

                        if (Simplex.noise2d(s3, 3, 0.5, 1 / 20.0, x, y) > 0.5) {
                            floor = floord2;
                            if (wall != Blocks.air) {
                                wall = walld2;
                            }
                        }

                        if (Simplex.noise2d(s2, 3, 0.3, 1 / 30.0, x, y) > tr1) {
                            ore = ore1;
                        }

                        if (Simplex.noise2d(s2, 2, 0.2, 1 / 15.0, x, y + 99999) > tr2) {
                            ore = ore2;
                        }
                        if (tech) {
                            int mx = x % 10, my = y % 10;
                            int sclx = x / 10, scly = y / 10;
                            if (Simplex.noise2d(offset, 2, 1f / 10f, 0.5f, sclx, scly) > 0.4f && (mx == 0 || my == 0 || mx == 10 - 1 || my == 10 - 1)) {
                                floor = Blocks.darkPanel3;
                                if (Mathf.dst(mx, my, 5, 5) > 10 / 2f + 1) {
                                    floor = Blocks.darkPanel4;
                                }


                                if (wall != Blocks.air && Mathf.chance(0.7)) {
                                    wall = Blocks.darkMetal;
                                }
                            }
                        }
                        setTile(x, y, floor, wall, ore, tiles);
                    }
                }
            }
        };
    }
}
