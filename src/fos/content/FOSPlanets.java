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

public class FOSPlanets {
    public static Planet
        /* planets */ lumina,
        /* asteroids */ uxerd;

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
        uxerd = makeAsteroid("uxerd", lumina, 0.5f, 19, 1.6f, gen -> {
            gen.defaultFloor = FOSBlocks.elbium;
            gen.elithiteChance = 0.33f;
            gen.elbiumChance = 0.5f;
            gen.meteoriteChance = 1f;
        });

        //TODO Anuke said it's temporary but it works for now
        uxerd.hiddenItems.addAll(Items.serpuloItems).addAll(Items.erekirItems);
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
                Rand rand = new Rand(id);
                Seq<GenericMesh> meshes = new Seq<>();
                Color color = (
                    rand.chance(0.33f) ? FOSBlocks.elithite :
                    rand.chance(0.5f) ? FOSBlocks.elbium :
                    FOSBlocks.meteoriteFloor
                ).mapColor;
                Color tinted = color.cpy().a(1f - color.a);

                rand = new Rand(id + 690);
                meshes.add(new NoiseMesh(
                    this, 0, 2, radius, 2, 0.55f, 0.45f, 14f,
                    color, tinted, 3, 0.6f, 0.38f, tintThresh
                ));

                for(int j = 0; j < pieces; j++){
                    color = (
                        rand.chance(0.33f) ? FOSBlocks.elithite :
                        rand.chance(0.5f) ? FOSBlocks.elbium :
                        FOSBlocks.meteoriteFloor
                    ).mapColor;
                    tinted = color.cpy().a(1f - color.a);

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
