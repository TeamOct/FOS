package fos.content;

import fos.ai.SubDiveAI;
import mindustry.ai.UnitCommand;

public class FOSCommands {
    public static UnitCommand diveCommand;

    public static void init() {
        diveCommand = new UnitCommand("fos-dive", "down", u -> new SubDiveAI()){{
            resetTarget = false;
        }};
    }
}
