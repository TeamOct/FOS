package fos.content;

import fos.type.statuses.*;
import mindustry.type.*;

public class FOSStatuses {
    public static StatusEffect hacked;

    public static void load() {
        hacked = new HackedEffect("hacked");
    }
}
