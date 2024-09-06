package fos.ui;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.scene.*;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.*;
import arc.util.Reflect;
import fos.core.FOSVars;
import fos.ui.menus.*;
import mindustry.game.*;
import mindustry.gen.Tex;
import mindustry.graphics.Layer;
import mindustry.mod.Mods;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.PlanetDialog;

import static arc.Core.*;
import static mindustry.Vars.*;

public class UIHandler {
    public static void init() {
        // disclaimer for non-debug
        if (FOSVars.earlyAccess && !FOSVars.debug)
            ui.showOkText("@fos.earlyaccesstitle", bundle.get("fos.earlyaccess"), () -> {});

        // unsupported on bleeding-edge notice
        if (becontrol.active())
            ui.showOkText("@fos.beunsupportedtitle", bundle.get("fos.beunsupported"), () -> {});

        // unlock every planet if debug
        if (FOSVars.debug)
            PlanetDialog.debugSelect = true;

        // check for "Fictional Octo System OST" mod. if it doesn't exist, prompt to download from GitHub
        Mods.LoadedMod ost = mods.getMod("fosost");
        if (ost == null) {
            if (!settings.getBool("fos-ostdontshowagain")) {
                ui.showCustomConfirm("@fos.noosttitle", bundle.get("fos.noost"),
                    "@mods.browser.add", "@no",
                    () -> {
                        ui.mods.githubImportMod("TeamOct/FOS-OST", true);
                    },
                    () -> {});
            }
        } else if (!ost.enabled()) {
            ui.showCustomConfirm("@fos.ostdisabledtitle", bundle.get("fos.ostdisabled"),
                "@yes", "@no",
                () -> {
                    mods.setEnabled(ost, true);
                    ui.showInfoOnHidden("@mods.reloadexit", () -> app.exit());
                }, () -> {});
        }

        Element menu = ((Element) Reflect.get(ui.menufrag, "container")).parent.parent;
        Group menuCont = menu.parent;
        menuCont.addChildBefore(menu, new Element(){
            @Override
            public void draw() {
                FOSVars.menuRenderer.render();
            }
        });

        int tn = settings.getInt("fos-menutheme");
        MenuBackground bg = (
            tn == 2 ? FOSMenus.uxerdSpace :
            tn == 3 ? FOSMenus.lumoniSpace :
            tn == 4 ? FOSMenus.random :
            tn == 5 ? FOSMenus.solarSystem :
            tn == 6 ? FOSMenus.caldemoltSystem :
            tn == 7 ? FOSMenus.lumoniTerrain : null);
        if (bg != null) {
            FOSVars.menuRenderer.changeBackground(bg);
        }

        ui.editor.shown(() -> {
            // java sucks
            WidgetGroup teambuttons = (WidgetGroup) ui.editor.getChildren().get(0);
            teambuttons = (WidgetGroup)teambuttons.getChildren().get(0);
            teambuttons = (WidgetGroup)teambuttons.getChildren().get(0);

            ((Table)teambuttons).row();

            for (int i = 69; i <= 70; i++) {
                Team team = Team.get(i);

                ImageButton button = new ImageButton(Tex.whiteui, Styles.clearNoneTogglei);
                button.margin(4f);
                button.getImageCell().grow();
                button.getStyle().imageUpColor = team.color;
                button.clicked(() -> editor.drawTeam = team);
                button.update(() -> button.setChecked(editor.drawTeam == team));

                ((Table)teambuttons).add(button);
            }
        });

        // an anti-cheat system from long ago, is it really necessary now?
        Mods.LoadedMod xf = mods.list().find(m ->
            /* some mods don't even have the author field, apparently. how stupid. */ m.meta.author != null &&
            (m.meta.author.equals("XenoTale") || m.meta.author.equals("goldie")));
        if (xf != null) {
            ui.showOkText("@fos.errortitle", bundle.format("fos.errortext", xf.meta.displayName), () -> app.exit());
        }


        // debug-only insect pathfinder test
        if (settings.getBool("fos-pathfinder-debug", false)) {
            Events.run(EventType.Trigger.draw, () -> {
                for (int i = 0; i < FOSVars.deathMapController.deathMap.length; i++) {
                    if (FOSVars.deathMapController.deathMap[i] == 0) continue;

                    Draw.z(Layer.light);
                    Draw.alpha(FOSVars.deathMapController.deathMap[i] / 100f);

                    Draw.rect("empty", world.tiles.geti(i).worldx(), world.tiles.geti(i).worldy());

                    Draw.reset();
                }
            });
        }
    }
}
