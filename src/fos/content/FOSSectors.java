package fos.content;

import mindustry.content.*;
import mindustry.type.*;

public class FOSSectors {
    public static SectorPreset
    /*Serpulo*/ siloTerminal;

    public static void load() {
        siloTerminal = new SectorPreset("siloTerminal", Planets.serpulo, 95);
    }
}
