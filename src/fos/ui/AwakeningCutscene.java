package fos.ui;

import arc.Core;
import arc.Events;
import arc.util.Reflect;
import arc.util.Time;
import fos.content.FOSFx;
import fos.content.FOSSectors;
import fos.content.FOSTeams;
import mindustry.game.EventType;

import static mindustry.Vars.renderer;
import static mindustry.Vars.state;

public class AwakeningCutscene {
    public static void load() {
        Events.run(EventType.Trigger.newGame, () -> {
            // remove landing cutscene on Awakening
            if (state.rules.sector != null && state.rules.sector == FOSSectors.awakening.sector) {
                FOSFx.blackScreen.at(Core.camera.position);

                Reflect.set(renderer, "landTime", 0f);
                var core = FOSTeams.corru.core();
                Time.run(160f, () -> FOSFx.corruLogo.at(core.x, core.y));
            }
        });
    }
}
