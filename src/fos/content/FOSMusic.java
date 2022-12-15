package fos.content;

import arc.audio.Music;
import mindustry.Vars;

public class FOSMusic {
    public static Music
        /* Uxerd */ dive,
        /* Lumoni */ abandoned, scavenger,
        /* bosses */ livingSteam, uncountable;

    public static void load() {
        dive = Vars.tree.loadMusic("dive");

        abandoned = Vars.tree.loadMusic("abandoned");
        scavenger = Vars.tree.loadMusic("scavenger");

        livingSteam = Vars.tree.loadMusic("living-steam");
        uncountable = Vars.tree.loadMusic("uncountable");
    }
}
