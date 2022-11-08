package fos.content;

import mindustry.Vars;
import mindustry.game.Schematic;
import mindustry.game.Schematics;

import java.io.IOException;

public class FOSSchematics {
    public static Schematic uxerdLoadout, luminaLoadout;

    public static void load() {
        uxerdLoadout = loadSchem("fos-uxerd");
        luminaLoadout = loadSchem("fos-lumina");
    }

    static Schematic loadSchem(String name) {
        Schematic s;
        try {
            s = Schematics.read(Vars.tree.get("schematics/" + name + ".msch"));
        } catch (IOException e) {
            s = null;
            e.printStackTrace();
        }
        return s;
    }

    static Schematic loadBase64(String b64) {
        return Schematics.readBase64(b64);
    }
}
