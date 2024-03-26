package fos.content;

import arc.graphics.Color;
import arc.util.*;
import mindustry.content.Weathers;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.type.weather.*;

public class FOSWeathers {
    public static Weather wind;

    public static void load() {
        //add wind power attribute to vanilla weathers, such as:
        Weathers.sandstorm.attrs.set(FOSAttributes.windPower, 0.9f);
        Weathers.sporestorm.attrs.set(FOSAttributes.windPower, 0.8f);

        wind = new ParticleWeather("wind"){{
            drawParticles = false;
            drawNoise = true;
            noiseColor = Color.valueOf("b0dcb71b");
            //this thing is way too disturbing.
            opacityMultiplier = 0.3f;
            useWindVector = true;
            baseSpeed = 3.6f;
            force = 0f;
            sound = Sounds.wind;
            soundVol = 0.9f;
            duration = 7f * Time.toMinutes;
            attrs.set(FOSAttributes.windPower, 1f);
        }};
    }
}
