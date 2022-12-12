package fos.content;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import fos.gen.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.game.Team;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSItems.*;
import static mindustry.content.Planets.*;
import static mindustry.type.Weather.*;

public class FOSPlanets {
    public static Planet
        /* planets */ lumoni,
        /* asteroids */ uxerd;

    public static void load() {
        lumoni = new Planet("lumoni", serpulo, 0.9f, 2){{
            defaultCore = coreFortress;
            hasAtmosphere = true;
            bloom = false;
            atmosphereColor = Color.valueOf("288a5d27");
            iconColor = annite.mapColor;
            meshLoader = () -> new HexMesh(this, 5);
            startSector = 9;
            generator = new LumoniPlanetGenerator(){{
                defaultLoadout = FOSSchematics.luminaLoadout;
            }};
            defaultEnv = Env.terrestrial | Env.oxygen | Env.groundWater;
            minZoom = 0.8f;
            camRadius += 0.4f;
            orbitSpacing = 6f;
            allowLaunchLoadout = true;
            cloudMeshLoader = () -> new HexSkyMesh(this, 7, 1.1f, 0.15f, 5, Color.valueOf("b0dcb76d"), 2, 0.5f, 1f, 0.38f);
            ruleSetter = r -> {
                r.loadout = ItemStack.list();
                r.fog = true;
                r.defaultTeam = FOSTeam.corru;
                r.waveTeam = r.attackMode ? Team.sharded : FOSTeam.bessin;
                r.waves = true;
                r.enemyCoreBuildRadius = 300;
                r.coreCapture = false;
                WeatherEntry weather = new WeatherEntry(FOSWeathers.wind);
                weather.always = true; //always windy
                r.weather.add(weather);
            };
        }};
        uxerd = new Planet("uxerd", lumoni, 0.12f){{
            hasAtmosphere = false;
            updateLighting = false;
            icon = "fos-asteroids";
            sectors.add(new Sector(this, PlanetGrid.Ptile.empty));
            camRadius = 0.68f * 1.3f;
            minZoom = 0.5f;
            drawOrbit = false;
            accessible = true;
            clipRadius = 2f;
            defaultEnv = Env.space;
            launchCandidates.add(lumoni);
            allowLaunchLoadout = false;
            generator = new UxerdAsteroidGenerator(){{
                seed = 8;
                defaultFloor = Blocks.ice;
                elithiteChance = 0.33f;
                elbiumChance = 0.5f;
                nethratiumChance = 0.4f;
            }};
            ruleSetter = r -> {
                r.loadout = ItemStack.list();
                r.planetBackground = new PlanetParams(){{
                    planet = parent;
                    zoom = 0.8f;
                    camPos = new Vec3(0f, 0f, 0.5f);
                }};
                r.dragMultiplier = 0.2f;
                r.borderDarkness = false;
                r.waves = false;
                r.waveTeam = FOSTeam.corru;
            };
            meshLoader = () -> {
                Rand rand = new Rand(8);
                Seq<GenericMesh> meshes = new Seq<>();
                Color color = (
                    rand.chance(0.33f) ? elithite :
                    rand.chance(0.5f) ? elbium :
                    rand.chance(0.4f) ? nethratium :
                    Blocks.ice
                ).mapColor;
                Color tinted = (
                    color == elithite.mapColor ? Blocks.ferricStone :
                    color == elbium.mapColor ? Blocks.rhyolite :
                    color == nethratium.mapColor ? Blocks.yellowStone :
                    Blocks.ice
                    ).mapColor;

                meshes.add(new NoiseMesh(
                    this, 8, 2, radius, 2, 0.55f, 0.45f, 14f,
                    color, tinted, 3, 0.6f, 0.38f, 0.5f
                ));

                for(int j = 0; j < 28; j++){
                    color = (
                        rand.chance(0.33f) ? elithite :
                        rand.chance(0.5f) ? elbium :
                        rand.chance(0.4f) ? nethratium :
                        Blocks.ice
                    ).mapColor;
                    tinted = (
                        color == elithite.mapColor ? Blocks.ferricStone :
                        color == elbium.mapColor ? Blocks.rhyolite :
                        color == nethratium.mapColor ? Blocks.yellowStone :
                        Blocks.ice
                    ).mapColor;

                    meshes.add(new MatMesh(
                        new NoiseMesh(this, 8, 1, 0.03f + rand.random(0.045f) * 1.3f, 2, 0.6f, 0.38f, 20f,
                            color, tinted, 3, 0.6f, 0.38f, 0.5f),
                        new Mat3D().setToTranslation(Tmp.v31.setToRandomDirection(rand).setLength(rand.random(0.44f, 1.4f) * 1.3f)))
                    );
                }

                return new MultiMesh(meshes.toArray(GenericMesh.class));
            };
        }};

        //hide modded items from vanilla planets
        serpulo.hiddenItems.addAll(uxerdItems).addAll(lumoniItems);
        erekir.hiddenItems.addAll(uxerdItems).addAll(lumoniItems);

        //TODO Anuke said it's temporary but it works for now
        uxerd.hiddenItems.addAll(Vars.content.items()).removeAll(uxerdItems);
        lumoni.hiddenItems.addAll(Vars.content.items()).removeAll(lumoniItems);
    }
}
