package fos.content;

import mindustry.world.meta.Attribute;

public class FOSAttributes {
    public static Attribute windPower;

    public static void load() {
        windPower = Attribute.add("windPower");
    }
}
