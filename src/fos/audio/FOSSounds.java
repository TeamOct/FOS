package fos.audio;

import arc.audio.Sound;

import static mindustry.Vars.tree;

public class FOSSounds {
    public static Sound
        radar = load("radar"),
        buzz = load("buzz");

    static Sound load(String name) {
        return tree.loadSound("loops/" + name);
    }
}
