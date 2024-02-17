package fos.content;

import arc.graphics.Color;
import arc.math.Rand;
import arc.math.geom.Mat3D;
import arc.struct.Seq;
import arc.util.Tmp;
import fos.core.FOSVars;
import fos.maps.generators.*;
import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.world.meta.Env;

import java.util.Calendar;

import static fos.content.FOSBlocks.*;
import static fos.content.FOSItems.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Planets.*;
import static mindustry.type.Weather.WeatherEntry;

public class FOSPlanets {
    public static Planet
        /* star */ caldemolt,
        /* planets */ lumoni,
        /* asteroids */ uxerd;

    public static void load() {
        caldemolt = new Planet("caldemolt", null, 5f){{
            bloom = true;
            hasAtmosphere = false;
            drawOrbit = false;
            meshLoader = () -> new SunMesh(this, 5, 5, 0.3, 1.7, 1.2, 1, 1.1f,
                Color.valueOf("f7c265"), Color.valueOf("ffb380"), Color.valueOf("e8d174"), Color.valueOf("ffa95e"));
            iconColor = Color.valueOf("f7c265");
            solarSystem = this;

            boolean nya = FOSVars.debug || FOSVars.date.get(Calendar.MONTH) == Calendar.APRIL && FOSVars.date.get(Calendar.DAY_OF_MONTH) == 1;
            accessible = nya;
            alwaysUnlocked = nya;
            if (nya) {
                generator = new CaldemoltStarGenerator();
                sectors.add(new Sector(this, PlanetGrid.Ptile.empty));
                ruleSetter = r -> {
                    r.loadout = ItemStack.list();
                    r.defaultTeam = FOSTeam.corru;
                };
                defaultEnv = Env.space | Env.scorching;
            }
        }};

        uxerd = new Planet("uxerd", caldemolt, 0.12f){{
            hasAtmosphere = false;
            updateLighting = false;
            icon = "fos-asteroids";
            sectors.add(new Sector(this, PlanetGrid.Ptile.empty));
            camRadius = 0.68f * 1.3f;
            minZoom = 0.5f;
            drawOrbit = false;
            clipRadius = 2f;
            defaultEnv = Env.space;
            //launchCandidates.add(lumoni);
            solarSystem = caldemolt;
            allowLaunchLoadout = false;
            clearSectorOnLose = true;
            accessible = true;
            //alwaysUnlocked = true;
            itemWhitelist = uxerdItems;
            generator = new UxerdAsteroidGenerator(){{
                seed = 8;
                defaultFloor = ice;
                elithiteChance = 0.33f;
                elbiumChance = 0.5f;
                nethratiumChance = 0.4f;
            }};
            ruleSetter = r -> {
                r.loadout = ItemStack.list();
                r.planetBackground = new PlanetParams(){{
                    planet = parent;
                    zoom = 0.6f;
                    camPos = uxerd.position.cpy().sub(caldemolt.position);
                }};
                r.dragMultiplier = 0.2f;
                r.borderDarkness = false;
                r.waves = false;
                r.defaultTeam = FOSTeam.corru;
                r.waveTeam = Team.sharded;
                r.bannedBlocks.addAll(container, injectorFactory);
                r.hideBannedBlocks = true;
            };
            meshLoader = () -> {
                Rand rand = new Rand(8);
                Seq<GenericMesh> meshes = new Seq<>();
                Color color = (
                    rand.chance(0.33f) ? elithite :
                        rand.chance(0.5f) ? elbium :
                            rand.chance(0.4f) ? nethratium :
                                ice
                ).mapColor;
                Color tinted = (
                    color == elithite.mapColor ? ferricStone :
                        color == elbium.mapColor ? rhyolite :
                            color == nethratium.mapColor ? yellowStone :
                                ice
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
                                    ice
                    ).mapColor;
                    tinted = (
                        color == elithite.mapColor ? ferricStone :
                            color == elbium.mapColor ? rhyolite :
                                color == nethratium.mapColor ? yellowStone :
                                    ice
                    ).mapColor;

                    meshes.add(new MatMesh(
                        new NoiseMesh(this, 28 + j, 1, 0.021f + rand.random(0.052f) * 1.3f, 2, 0.6f, 0.38f, 20f,
                            color, tinted, 3, 0.6f, 0.38f, 0.5f),
                        new Mat3D().setToTranslation(Tmp.v31.setToRandomDirection(rand).setLength(rand.random(0.44f, 1.4f) * 1.3f)))
                    );
                }

                return new MultiMesh(meshes.toArray(GenericMesh.class));
            };
        }};

        lumoni = new Planet("lumoni", caldemolt, 0.9f, 2){{
            defaultCore = coreFortress;
            hasAtmosphere = true;
            bloom = false;
            atmosphereColor = Color.valueOf("288a5d27");
            iconColor = annite.mapColor;
            meshLoader = () -> new HexMesh(this, 5);
            startSector = 89;
            generator = new LumoniPlanetGenerator(){{
                defaultLoadout = FOSSchematics.lumoniLoadout;
            }};
            defaultEnv = Env.terrestrial | Env.oxygen | Env.groundWater;
            minZoom = 0.8f;
            camRadius += 0.4f;
            orbitSpacing = 6f;
            allowLaunchLoadout = true;
            accessible = true;
            alwaysUnlocked = true;
            //TODO you'll see why I did this :)
            launchCandidates.add(uxerd);
            solarSystem = caldemolt;
            itemWhitelist = lumoniItems;
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
                r.bannedBlocks.addAll(conveyor, junction, router, duo, mechanicalDrill, copperWall, copperWallLarge);
                r.hideBannedBlocks = true;
            };
        }};

        //hide modded items from vanilla planets
        serpulo.hiddenItems.addAll(uxerdItems).addAll(lumoniItems).removeAll(Items.serpuloItems);
        erekir.hiddenItems.addAll(uxerdItems).addAll(lumoniItems).removeAll(Items.erekirItems);
    }
}
