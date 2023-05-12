package fos.game;

import arc.*;
import mindustry.game.EventType;

public class EndlessBoostHandler {
    static {
        Events.run(EventType.Trigger.update, () -> {
            if (!Core.settings.getBool("fos-endless-enabled", true)) return;

            //do something, maybe?
        });
    }
}
