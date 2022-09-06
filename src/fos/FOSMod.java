package fos;

import arc.*;
import arc.audio.Music;
import arc.audio.Sound;
import arc.util.*;
import fos.content.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.type.*;

import static mindustry.game.EventType.*;

public class FOSMod extends Mod {
    Sound s;
    public FOSMod(){
        Events.on(ClientLoadEvent.class, e -> FOSTeam.load());
        Events.on(UnitSpawnEvent.class, e -> {
            //debug stuff, so only limited to myself
            if (!Vars.steamPlayerName.equals("Slotterleet")) return;

            Unit u = e.unit; Team team = u.team; UnitType type = u.type;
            Log.info("Spawned " + type.name + " as " + team.name + " (" + team.id + ", " + team.color.toString() + ")");
        });
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
