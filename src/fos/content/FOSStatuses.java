package fos.content;

import arc.graphics.Color;
import fos.type.statuses.*;
import mindustry.type.*;

public class FOSStatuses {
    public static StatusEffect hacked;

    public static void load() {
        hacked = new HackedEffect("hacked"){{
            color = Color.valueOf("51a0b0");
        }};
    }
}
