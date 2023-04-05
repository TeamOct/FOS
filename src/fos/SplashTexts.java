package fos;

import arc.Core;
import arc.struct.Seq;

public class SplashTexts {
    public static Seq<String> splashes = new Seq<>();

    public static void load() {
        int i = 0;
        while (Core.bundle.has("splash" + i)) {
            splashes.add(Core.bundle.get("splash" + i++));
        }
    }
}
