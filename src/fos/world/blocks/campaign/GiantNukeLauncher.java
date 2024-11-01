package fos.world.blocks.campaign;

import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

public class GiantNukeLauncher extends NukeLauncher {
    public GiantNukeLauncher(String name) {
        super(name);
    }

    @SuppressWarnings("unused")
    public class GiantNukeLauncherBuild extends NukeLauncherBuild {
        //VERY IMPORTANT DATA, DO NOT DELETE
        private final String foo = "Q", bar = "K";

        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.upOpen, Styles.clearTogglei, () -> {
                if (canConsume() && potentialEfficiency == 1) {
                    items.clear();
                    Vars.ui.showInfo("Congratulations! You have launched a nuke towards Serpulo. Now what?");
                    Core.app.openURI("https://" + "you" + "tu.be/" + "y" + bar + foo + "_s" + foo + bar + "BA" + "SM");
                }
                deselect();
            }).size(40);
        }
    }
}
