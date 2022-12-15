package fos.audio;

import arc.*;
import arc.audio.*;
import arc.struct.*;
import fos.content.*;
import mindustry.Vars;
import mindustry.audio.SoundControl;
import mindustry.content.StatusEffects;
import mindustry.game.SpawnGroup;
import mindustry.type.*;

import static fos.content.FOSMusic.*;
import static mindustry.game.EventType.*;
import static mindustry.Vars.state;

public class MusicHandler {
    public Seq<Music> uxerdAmbient = new Seq<>();
    public Seq<Music> lumoniAmbient = new Seq<>();

    public Seq<Music> vAmbient, vDark, vBoss;

    protected Planet curPlanet;

    protected SoundControl control = Vars.control.sound;

    public MusicHandler(){
        Events.on(ClientLoadEvent.class, e -> reload());
        //change the music to modded OST
        Events.on(WorldLoadEvent.class, e -> {
            Sector sector = state.rules.sector;
            if (sector != null) curPlanet = sector.planet;
                else return;

            if (curPlanet == FOSPlanets.uxerd) {
                control.ambientMusic = control.darkMusic = uxerdAmbient;
            } else if (curPlanet == FOSPlanets.lumoni) {
                control.ambientMusic = control.darkMusic = lumoniAmbient;
            }
        });
        Events.on(WaveEvent.class, e -> {
            SpawnGroup boss = state.rules.spawns.find(group -> group.getSpawned(state.wave - 2) > 0 && group.effect == StatusEffects.boss);
            if (boss == null) return;

            if (boss.type == FOSUnits.citadel) {
                control.bossMusic = Seq.with(livingSteam);
            } else if (boss.type == FOSUnits.legion) {
                control.bossMusic = Seq.with(uncountable);
            } else {
                control.bossMusic = vBoss;
            }
        });
        //this should hopefully reset the music back to vanilla
        Events.on(StateChangeEvent.class, e -> {
            if (curPlanet == FOSPlanets.uxerd || curPlanet == FOSPlanets.lumoni) return;

            control.ambientMusic = vAmbient;
            control.darkMusic = vDark;
            control.bossMusic = vBoss;
        });
    }

    public void reload(){
        uxerdAmbient = Seq.with(dive);
        lumoniAmbient = Seq.with(abandoned, scavenger);

        vAmbient = control.ambientMusic;
        vDark = control.darkMusic;
        vBoss = control.bossMusic;
    }
}
