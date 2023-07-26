package fos.core;

import arc.Core;
import arc.math.Mathf;
import arc.struct.Seq;

import java.util.Calendar;

import static arc.Core.bundle;

/** Sets mod subtitle to random splashes **/
public class SplashTexts {
    public static Seq<String> splashes = new Seq<>();

    public static void load() {
        int i = 1;
        while (Core.bundle.has("splash" + i)) {
            splashes.add(Core.bundle.get("splash" + i++));
        }
    }

    public static void init() {
        boolean isNewYear = FOSVars.date.get(Calendar.MONTH) == Calendar.JANUARY &&
                FOSVars.date.get(Calendar.DAY_OF_MONTH) == 1;
        FOSVars.mod.meta.subtitle = isNewYear ? bundle.get("splashnewyear") : SplashTexts.splashes.get(
                Mathf.floor((float) Math.random() * SplashTexts.splashes.size)
        );
    }
}
