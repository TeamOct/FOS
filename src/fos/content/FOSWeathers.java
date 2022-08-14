package fos.content;

import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.type.weather.*;

public class FOSWeathers {
    public static Weather wind;

    public static void load() {
        wind = new ParticleWeather("wind"){{
            drawParticles = false;
            drawNoise = true;
            useWindVector = true;
            baseSpeed = 3.6f;
            force = 0.1f;
            sound = Sounds.wind;
            soundVol = 0.9f;
            duration = 7f * Time.toMinutes;
            attrs.set(FOSAttributes.windPower, 1f);
        }};
    }
}
