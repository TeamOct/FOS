package fos.content;

import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import fos.type.gen.*;
import mindustry.content.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import static mindustry.type.Weather.*;

public class FOSPlanets {
    public static Planet
        /* planets */ lumina,
        /* asteroids */ uxerd;

    public static void load(){
        lumina = new Planet("lumina", Planets.serpulo, 0.9f, 2){{
            defaultCore = FOSBlocks.coreFortress;
            alwaysUnlocked = true;
            hasAtmosphere = true;
            bloom = false;
            atmosphereColor = Color.valueOf("b0dcb76d");
            meshLoader = () -> new HexMesh(this, 5);
            startSector = 9;
            generator = new LuminaPlanetGenerator();
            minZoom = 2f;
            camRadius += 1f;
            cloudMeshLoader = () -> new HexSkyMesh(this, 7, 1.1f, 0.15f, 7, Color.valueOf("b0dcb76d"), 2, 0.5f, 1f, 0.38f);
            ruleSetter = r -> {
                r.fog = true;
                r.waveTeam = FOSTeam.corru;
                r.waves = true;
                r.enemyCoreBuildRadius = 300;
                WeatherEntry weather = new WeatherEntry(FOSWeathers.wind);
                weather.always = true; //always windy
                r.weather.add(weather);
            };
        }};
        uxerd = makeAsteroid("uxerd", lumina, 0.5f, 28, 1.3f, gen -> {
            //this is the seed I thought it's good enough
            gen.seed = 8;
            gen.defaultFloor = Blocks.ice;
            gen.elithiteChance = 0.33f;
            gen.elbiumChance = 0.5f;
            gen.nethratiumChance = 0.4f;
        });

        //TODO Anuke said it's temporary but it works for now
        uxerd.hiddenItems.addAll(Items.serpuloItems).addAll(Items.erekirItems).remove(Items.titanium);
        lumina.hiddenItems.addAll(Items.serpuloItems).addAll(Items.erekirItems);
    }

    private static Planet makeAsteroid(String name, Planet parent, float tintThresh, int pieces, float scale, Cons<FOSAsteroidGenerator> cgen){
        return new Planet(name, parent, 0.12f){{
            hasAtmosphere = false;
            updateLighting = false;
            sectors.add(new Sector(this, PlanetGrid.Ptile.empty));
            camRadius = 0.68f * scale;
            minZoom = 0.6f;
            drawOrbit = false;
            accessible = true;
            clipRadius = 2f;
            defaultEnv = Env.space;

            generator = new FOSAsteroidGenerator();
            cgen.get((FOSAsteroidGenerator)generator);

            meshLoader = () -> {
                Rand rand = new Rand(8);
                Seq<GenericMesh> meshes = new Seq<>();
                Color color = (
                    rand.chance(0.33f) ? FOSBlocks.elithite :
                    rand.chance(0.5f) ? FOSBlocks.elbium :
                    rand.chance(0.4f) ? FOSBlocks.nethratium :
                    Blocks.ice
                ).mapColor;
                Color tinted = color.cpy().a(1f - color.a);

                meshes.add(new NoiseMesh(
                    this, 8, 2, radius, 2, 0.55f, 0.45f, 14f,
                    color, tinted, 3, 0.6f, 0.38f, tintThresh
                ));

                for(int j = 0; j < pieces; j++){
                    color = (
                        rand.chance(0.33f) ? FOSBlocks.elithite :
                        rand.chance(0.5f) ? FOSBlocks.elbium :
                        rand.chance(0.4f) ? FOSBlocks.nethratium :
                        Blocks.ice
                    ).mapColor;
                    tinted = color.cpy().a(1f - color.a);

                    meshes.add(new MatMesh(
                        new NoiseMesh(this, 8, 1, 0.03f + rand.random(0.045f) * scale, 2, 0.6f, 0.38f, 20f,
                            color, tinted, 3, 0.6f, 0.38f, tintThresh),
                        new Mat3D().setToTranslation(Tmp.v31.setToRandomDirection(rand).setLength(rand.random(0.44f, 1.4f) * scale)))
                    );
                }

                return new MultiMesh(meshes.toArray(GenericMesh.class));
            };
        }};
    }
}
