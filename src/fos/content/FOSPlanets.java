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
import mindustry.world.*;
import mindustry.world.meta.*;

public class FOSPlanets {
    public static Planet
        /* planets */ lumina,
        /* asteroids */ /*TODO temp name!!*/ aster;

    public static void load(){
        lumina = new Planet("lumina", Planets.serpulo, 0.6f, 2){{
            alwaysUnlocked = true;
            hasAtmosphere = true;
            atmosphereColor = Color.valueOf("b0dcb7");
            meshLoader = () -> new HexMesh(this, 6);
            startSector = 40;
            generator = new LuminaPlanetGenerator();
            minZoom = 0.3f;
            camRadius += 0.8f;
        }};
        aster = makeAsteroid("aster", lumina, FOSBlocks.elithiteWall, FOSBlocks.elbiumWall, 0.5f, 22, 1.6f, gen -> {
            gen.defaultFloor = FOSBlocks.elithite;
            gen.elbiumChance = 0.4f;
            gen.meteoriteChance = 0.3f;
            gen.iceChance = 0.1f;
        });
    }

    private static Planet makeAsteroid(String name, Planet parent, Block base, Block tint, float tintThresh, int pieces, float scale, Cons<FOSAsteroidGenerator> cgen){
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
                Color tinted = tint.mapColor.cpy().a(1f - tint.mapColor.a);
                Seq<GenericMesh> meshes = new Seq<>();
                Color color = base.mapColor;
                Rand rand = new Rand(id + 2);

                meshes.add(new NoiseMesh(
                    this, 0, 2, radius, 2, 0.55f, 0.45f, 14f,
                    color, tinted, 3, 0.6f, 0.38f, tintThresh
                ));

                for(int j = 0; j < pieces; j++){
                    meshes.add(new MatMesh(
                        new NoiseMesh(this, j + 1, 1, 0.022f + rand.random(0.039f) * scale, 2, 0.6f, 0.38f, 20f,
                            color, tinted, 3, 0.6f, 0.38f, tintThresh),
                        new Mat3D().setToTranslation(Tmp.v31.setToRandomDirection(rand).setLength(rand.random(0.44f, 1.4f) * scale)))
                    );
                }

                return new MultiMesh(meshes.toArray(GenericMesh.class));
            };
        }};
    }
}
