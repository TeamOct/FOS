package fos.content;

import arc.audio.Music;
import mindustry.Vars;

public class FOSMusic {
    public static Music
        /* Uxerd */ dive,
        /* Lumina */ abandoned, luminaBoss;

    public static void load() {
        dive = Vars.tree.loadMusic("dive");

        abandoned = Vars.tree.loadMusic("abandoned");
        //TODO replace, unfinished and short
        luminaBoss = Vars.tree.loadMusic("boss3");
    }
}
