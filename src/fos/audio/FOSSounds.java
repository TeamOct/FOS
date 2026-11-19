package fos.audio;

import arc.audio.Sound;

import static mindustry.Vars.tree;

public class FOSSounds {
    public static Sound
        radar = load("loops/radar"),
        buzz = load("loops/buzz"),
        sticky = load("sticky"),
        spit = load("spit"),
        respawning = load("respawning"),
        respawn = load("respawn"),
        bugScreech = load("bug-screech");

    static Sound load(String name) {
        return tree.loadSound(name);
    }
}
