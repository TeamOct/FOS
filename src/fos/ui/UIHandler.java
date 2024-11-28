package fos.ui;

import arc.Core;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.Seq;
import arc.util.*;
import fos.core.FOSVars;
import fos.type.WeaponSet;
import fos.ui.menus.*;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.mod.Mods;
import mindustry.ui.*;
import mindustry.ui.dialogs.PlanetDialog;

import static arc.Core.*;
import static mindustry.Vars.*;

public class UIHandler {
    public static void init() {
        // disclaimer for non-debug
        preReleaseDisclaimer();

        // unsupported on bleeding-edge notice
        bleedingEdgeIsBroken();

        // unlock every planet if debug
        if (FOSVars.debug)
            PlanetDialog.debugSelect = true;

        // check for "Fictional Octo System OST" mod. if it doesn't exist, prompt to download from GitHub
        ostPrompt();

        // menu background (can be space or surface)
        modifyMenu();

        // 2 more team buttons in editor UI
        modifyEditorUI();

        // an anti-cheat system from long ago, is it really necessary now?
        anticheat();

        // debug-only insect pathfinder test
/*
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
*/

        // dedicated weapon module tab in core DB
        ui.database.shown(UIHandler::modifyCoreDb);

        Element w2 = ui.database.getChildren().get(1);
        w2 = ((Group) w2).getChildren().get(0);
        w2 = ((Group) w2).getChildren().get(1);
        TextField search = (TextField)w2;

        search.typed(c -> modifyCoreDb());
    }

    static void preReleaseDisclaimer() {
        if (FOSVars.earlyAccess && !FOSVars.debug)
            ui.showOkText("@fos.earlyaccesstitle", bundle.get("fos.earlyaccess"), () -> {});
    }

    static void bleedingEdgeIsBroken() {
        if (becontrol.active())
            ui.showOkText("@fos.beunsupportedtitle", bundle.get("fos.beunsupported"), () -> {});
    }

    static void ostPrompt() {
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
    }

    static void modifyMenu() {
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
    }

    static void modifyEditorUI() {
        ui.editor.shown(() -> {
            // java sucks
            Element teambuttons = ui.editor.getChildren().get(0);
            teambuttons = ((Group)teambuttons).getChildren().get(0);
            teambuttons = ((Group)teambuttons).getChildren().get(0);

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
    }

    static void anticheat() {
        Mods.LoadedMod xf = mods.list().find(m ->
            /* some mods don't even have the author field, apparently. how stupid. */ m.meta.author != null &&
            (m.meta.author.equals("XenoTale") || m.meta.author.equals("goldie")));
        if (xf != null) {
            ui.showOkText("@fos.errortitle", bundle.format("fos.errortext", xf.meta.displayName), () -> app.exit());
        }
    }

    static void modifyCoreDb() {
        Element w = ui.database.getChildren().get(1);
        w = ((Group) w).getChildren().get(1);
        w = ((Group) w).getChildren().get(0);
        Table t = (Table)w;

        Element w2 = ui.database.getChildren().get(1);
        w2 = ((Group) w2).getChildren().get(0);
        w2 = ((Group) w2).getChildren().get(1);
        TextField search = (TextField)w2;

        String text = search.getText();

        Seq<WeaponSet> array = WeaponSet.sets.select(ws -> text.isEmpty() || ws.localizedName.toLowerCase().contains(text.toLowerCase()));

        t.add("@content.fos-weaponmodules.name").growX().left().color(Pal.accent);
        t.row();
        t.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
        t.row();
        t.table(list -> {
            list.left();

            int cols = (int) Mathf.clamp((Core.graphics.getWidth() - Scl.scl(30)) / Scl.scl(32 + 12), 1, 22);
            int count = 0;

            for(int i = 0; i < array.size; i++){
                UnlockableContent unlock = array.get(i);

                Image image = unlocked(unlock) ? new Image(unlock.uiIcon).setScaling(Scaling.fit) : new Image(Icon.lock, Pal.gray);

                //banned cross
                if(state.isGame() && state.rules.tags.get("fos-bannedMountUpgrades") != null && state.rules.tags.get("fos-bannedMountUpgrades").contains(unlock.name)) {
                    list.stack(image, new Image(Icon.cancel){{
                        setColor(Color.scarlet);
                        touchable = Touchable.disabled;
                    }}).size(8 * 4).pad(3);
                }else{
                    list.add(image).size(8 * 4).pad(3);
                }

                ClickListener listener = new ClickListener();
                image.addListener(listener);
                if(!mobile && unlocked(unlock)){
                    image.addListener(new HandCursorListener());
                    image.update(() -> image.color.lerp(!listener.isOver() ? Color.lightGray : Color.white, Mathf.clamp(0.4f * Time.delta)));
                }

                if(unlocked(unlock)){
                    image.clicked(() -> {
                        if(Core.input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(unlock.name) != 0){
                            Core.app.setClipboardText((char)Fonts.getUnicode(unlock.name) + "");
                            ui.showInfoFade("@copied");
                        }else{
                            ui.content.show(unlock);
                        }
                    });
                    image.addListener(new Tooltip(tt -> tt.background(Tex.button).add(unlock.localizedName + (settings.getBool("console") ? "\n[gray]" + unlock.name : ""))));
                }

                if((++count) % cols == 0){
                    list.row();
                }
            }
        }).growX().left().padBottom(10);
    }

    static boolean unlocked(UnlockableContent content) {
        return (!Vars.state.isCampaign() && !Vars.state.isMenu()) || content.unlocked();
    }
}
