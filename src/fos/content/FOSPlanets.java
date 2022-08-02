package fos.content;

import fos.type.gen.LuminaPlanetGenerator;
import mindustry.content.Planets;
import mindustry.graphics.g3d.HexMesh;
import mindustry.type.Planet;

public class FOSPlanets {
    public static Planet lumina;

    public static void load(){
        lumina = new Planet("lumina", Planets.serpulo, 0.6f, 2){{
            alwaysUnlocked = true;
            hasAtmosphere = true;
            meshLoader = () -> new HexMesh(this, 6);
            startSector = 40;
            generator = new LuminaPlanetGenerator();
            minZoom = 0.3f;
            camRadius += 0.8f;
        }};
    }
}
