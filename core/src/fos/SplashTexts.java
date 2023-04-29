package fos;

import arc.Core;
import arc.struct.Seq;
import arc.util.Log;

public class SplashTexts {
    public static Seq<String> splashes = new Seq<>();

    public static void load() {
        int i = 1;
        while (Core.bundle.has("splash" + i)) {
            splashes.add(Core.bundle.get("splash" + i++));
        }
    }
}
