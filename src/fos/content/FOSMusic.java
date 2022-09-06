package fos.content;

import arc.audio.Music;
import mindustry.Vars;

public class FOSMusic {
    public static Music luminaBoss;

    public static void load() {
        luminaBoss = Vars.tree.loadMusic("boss3");
    }
}
