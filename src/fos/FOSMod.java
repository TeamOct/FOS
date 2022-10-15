package fos;

import arc.*;
import arc.discord.DiscordRPC;
import arc.discord.DiscordRPC.RichPresence;
import arc.math.*;
import arc.util.*;
import fos.content.*;
import fos.type.blocks.special.OrbitalAccelerator.*;
import mindustry.content.SectorPresets;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.world.meta.Env;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.game.EventType.*;

public class FOSMod extends Mod {
    public FOSMod() {
        Events.on(ClientLoadEvent.class, e -> {
            loadSettings();

            content.units().each(u ->
                u.description += ("\n" + bundle.get("unittype") + (
                    u.constructor.get() instanceof MechUnit ? bundle.get("unittype.infantry") :
                    u.constructor.get() instanceof UnitEntity ? bundle.get("unittype.flying") :
                    u.constructor.get() instanceof LegsUnit ? bundle.get("unittype.spider") :
                    u.constructor.get() instanceof UnitWaterMove ? bundle.get("unittype.ship") :
                    u.constructor.get() instanceof PayloadUnit ? bundle.get("unittype.payload") :
                    u.constructor.get() instanceof TimedKillUnit ? bundle.get("unittype.timedkill") :
                    u.constructor.get() instanceof TankUnit ? bundle.get("unittype.tank") :
                    u.constructor.get() instanceof ElevationMoveUnit ? bundle.get("unittype.hover") :
                    u.constructor.get() instanceof BuildingTetherPayloadUnit ? bundle.get("unittype.tether") :
                    u.constructor.get() instanceof CrawlUnit ? bundle.get("unittype.crawl") :
                    ""
                    )
                + (u.weapons.contains(w -> w.bullet.heals()) ? bundle.get("unittype.support") : ""))
            );

            ui.showOkText("@fos.earlyaccesstitle", "@fos.earlyaccess", () -> {});
        });

        Events.run(Trigger.update, () -> {
            if (!mobile) {
                boolean useDiscord = !OS.hasProp("nodiscord");
                if (useDiscord) {
                    RichPresence presence = new RichPresence();
                    if (!state.isCampaign()) return;
                    if (state.rules.sector.planet == FOSPlanets.uxerd) {
                        Building a = indexer.findTile(Team.sharded, 350 * 8, 350 * 8, 4000, b -> b instanceof OrbitalAcceleratorBuild);

                        presence.state = "Orbital Accelerator Progress: " + (a != null ? Mathf.round((float) a.items().total() / (float) a.block().itemCapacity * 100) : "0") + "%";
                        presence.details = "Uxerd (FOS)";

                        presence.largeImageKey = "logo";

                        try {
                            DiscordRPC.send(presence);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            if (SectorPresets.planetaryTerminal.sector.info.wasCaptured && !FOSPlanets.uxerd.unlocked()) {
                FOSPlanets.uxerd.unlock();
            }

            if (settings.getBool("fos-realisticmode") && state.rules.sector != null && state.rules.sector.planet.defaultEnv == Env.space) {
                audio.soundBus.setVolume(0f);
            } else {
                audio.soundBus.setVolume(settings.getInt("sfxvol") / 100f);
            }
        });
    }

    @Override
    public void init() {
        LoadedMod mod = mods.locateMod("fos");

        SplashTexts.load(13);
        int n = Mathf.floor((float) Math.random() * SplashTexts.splashes.size);
        mod.meta.subtitle = SplashTexts.splashes.get(n);

        mod.meta.description += "\n\nCurrent Version: " + mod.meta.version;

        FOSIcons.load();
        FOSTeam.load();

        FOSVars.load();

        LoadedMod xf = mods.list().find(m -> m.meta.author.equals("XenoTale"));
        if (xf != null) {
            ui.showOkText("@fos.errortitle", bundle.format("fos.errortext", xf.meta.displayName), () -> app.exit());
        }
    }

    @Override
    public void loadContent() {
        FOSMusic.load();
        FOSAttributes.load();
        FOSWeathers.load();
        FOSItems.load();
        FOSLiquids.load();
        FOSWeaponModules.load();
        FOSBullets.load();
        FOSStatuses.load();
        FOSUnits.load();
        FOSBlocks.load();
        FOSPlanets.load();
        FOSSectors.load();

        LuminaTechTree.load();
        SerpuloTechTree.load();
        UxerdTechTree.load();
    }

    void loadSettings() {
        ui.settings.addCategory("@setting.fos-title", "fos-settings-icon", t -> {
            t.checkPref("fos-realisticmode", false);
        });
    }
}
