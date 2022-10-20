package fos.content;

import arc.audio.Music;
import mindustry.Vars;

public class FOSMusic {
    public static Music
        /* Uxerd */ dive,
        /* Lumina */ abandoned,
        /* bosses */ livingSteam;

    public static void load() {
        dive = Vars.tree.loadMusic("dive");

        abandoned = Vars.tree.loadMusic("abandoned");

        livingSteam = Vars.tree.loadMusic("living-steam");
    }
}
