package fos;

import arc.*;
import arc.math.Mathf;
import arc.util.*;
import fos.content.*;
import fos.type.audio.MusicHandler;
import mindustry.Vars;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.type.*;

import static mindustry.Vars.*;
import static mindustry.Vars.mods;
import static mindustry.game.EventType.*;

public class FOSMod extends Mod {
    public MusicHandler handler;

    public FOSMod(){
        Events.on(UnitSpawnEvent.class, e -> {
            //debug stuff, so only limited to myself
            if (!steamPlayerName.equals("Slotterleet")) return;

            Unit u = e.unit; Team team = u.team; UnitType type = u.type;
            Log.info("Spawned " + type.name + " as " + team.name + " (" + team.id + ", " + team.color.toString() + ")");
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
    public void loadContent(){
        FOSMusic.load();
        FOSAttributes.load();
        FOSWeathers.load();
        FOSItems.load();
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
