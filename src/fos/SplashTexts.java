package fos;

import arc.Core;
import arc.struct.Seq;

public class SplashTexts {
    public static Seq<String> splashes = new Seq<>();

    public static void load(int amount) {
        for (int i = 0; i < amount; i++) {
            splashes.add(Core.bundle.get("splash" + (i+1)));
        }
    }
}
