package fos.core;

import arc.Core;
import arc.discord.DiscordRPC;
import arc.util.*;
import mindustry.gen.Groups;

import static arc.discord.DiscordRPC.RichPresence;
import static mindustry.Vars.*;

public class RichPresenceHandler {
    public static final long appId = 1281506459309441074L;
    public static RichPresence presence = new RichPresence();

    public static void init() {
        if (mobile || !Core.settings.getBool("fos-moddedrichpresence", true)) return;

        presence.startTimestamp = System.currentTimeMillis();

        try {
            // close vanilla RP
            Log.info("[FOS] Closing vanilla Discord rich presence.");
            DiscordRPC.close();
            // there's no way Vars.platform isn't DesktopLauncher at any given moment
            Reflect.set(platform, "useDiscord", false);

            // then open our own one
            Log.info("[FOS] Opening modded Discord rich presence.");
            DiscordRPC.connect(appId);

            send();
        } catch (Exception e) {
            Log.err("[FOS] Modded rich presence failed to initialize!", e);
            return;
        }

        Timer.schedule(RichPresenceHandler::send, 0f, 5f, -1);
    }

    static void send() {
        boolean useDiscord = !OS.hasProp("nodiscord");
        if (useDiscord) {
            try {
                var planet = state.rules.planet;
                boolean isFOS = planet != null && !planet.isVanilla() && planet.minfo.mod == FOSVars.mod;

                String gameMode = "", gamePlayersSuffix = "", uiState = "";

                if (state.isGame()) {
                    String gameMapWithWave;
                    gameMapWithWave = Strings.capitalize(Strings.stripColors(state.map.name()));

                    if (state.rules.waves) {
                        gameMapWithWave += " | Wave " + state.wave;
                    }
                    gameMode = state.rules.pvp ? "PvP" : state.rules.attackMode ? "Attack" : state.rules.infiniteResources ? "Sandbox" : "Survival";
                    if (net.active() && Groups.player.size() > 1) {
                        gamePlayersSuffix = " | " + Groups.player.size() + " Players";
                    }

                    presence.details = (isFOS ? "[FOS] " : "") + gameMapWithWave;
                    presence.state = gameMode + gamePlayersSuffix;
                } else {
                    if (ui.editor != null && ui.editor.isShown()) {
                        uiState = "In Editor";
                    } else if (ui.planet != null && ui.planet.isShown()) {
                        uiState = "In Launch Selection";
                    } else {
                        uiState = "In Menu";
                    }

                    presence.details = "";
                    presence.state = uiState;
                }

                presence.largeImageKey = "logo";
                presence.label1 = "Try FOS now!";
                presence.url1 = "https://github.com/teamoct/fos/releases/latest";

                DiscordRPC.send(presence);


            } catch (Exception e) {
                Log.err("[FOS] Rich Presence failed to send!", e);
            }
        }
    }
}
