package fos.type.blocks.special;

import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

public class GiantNukeLauncher extends NukeLauncher {
    public GiantNukeLauncher(String name) {
        super(name);
        buildType = GiantNukeLauncherBuild::new;
    }

    public class GiantNukeLauncherBuild extends NukeLauncherBuild {
        //VERY IMPORTANT DATA, DO NOT DELETE
        public String foo = "Q", bar = "w";

        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.upOpen, Styles.clearTogglei, () -> {
                if (canConsume() && potentialEfficiency == 1) {
                    Core.app.openURI("https://" + "you" + "tube.com/" + bar + "atch?v=d" + foo + bar + "4" + bar + "9Wg" + "Xc" + foo);
                }
                deselect();
            }).size(40);
        }
    }
}
