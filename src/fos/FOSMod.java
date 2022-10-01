package fos;

import arc.*;
import arc.discord.DiscordRPC;
import arc.discord.DiscordRPC.RichPresence;
import arc.math.*;
import arc.util.*;
import fos.content.*;
import fos.type.audio.MusicHandler;
import fos.type.blocks.special.OrbitalAccelerator.OrbitalAcceleratorBuild;
import mindustry.content.SectorPresets;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.type.*;

import static mindustry.Vars.*;
import static mindustry.game.EventType.*;

public class FOSMod extends Mod {
    public MusicHandler handler;

    public FOSMod() {
        Events.on(ClientLoadEvent.class, e -> {
            content.units().each(u -> {
                u.description += ("\n" + Core.bundle.get("unittype") + (
                    u.constructor.get() instanceof MechUnit ? Core.bundle.get("unittype.infantry") :
                    u.constructor.get() instanceof UnitEntity ? Core.bundle.get("unittype.flying") :
                    u.constructor.get() instanceof LegsUnit ? Core.bundle.get("unittype.spider") :
                    u.constructor.get() instanceof UnitWaterMove ? Core.bundle.get("unittype.ship") :
                    u.constructor.get() instanceof PayloadUnit ? Core.bundle.get("unittype.payload") :
                    u.constructor.get() instanceof TimedKillUnit ? Core.bundle.get("unittype.timedkill") :
                    u.constructor.get() instanceof TankUnit ? Core.bundle.get("unittype.tank") :
                    u.constructor.get() instanceof ElevationMoveUnit ? Core.bundle.get("unittype.hover") :
                    u.constructor.get() instanceof BuildingTetherPayloadUnit ? Core.bundle.get("unittype.tether") :
                    u.constructor.get() instanceof CrawlUnit ? Core.bundle.get("unittype.crawl") :
                    ""
                    )
                + (u.weapons.contains(w -> w.bullet.heals()) ? Core.bundle.get("unittype.support") : ""));
            });

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

            if (SectorPresets.planetaryTerminal.sector.info.wasCaptured && !FOSPlanets.uxerd.unlocked()) FOSPlanets.uxerd.unlock();
        });
    }

    @Override
    public void init() {
        LoadedMod mod = mods.locateMod("fos");

        SplashTexts.load(12);

        int n = Mathf.floor((float) Math.random() * SplashTexts.splashes.size);
        if (mod != null) mod.meta.subtitle = SplashTexts.splashes.get(n);

        FOSTeam.load();

        handler = new MusicHandler();
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
}
