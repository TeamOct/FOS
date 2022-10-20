package fos.type.audio;

import arc.*;
import arc.audio.*;
import arc.math.Mathf;
import arc.struct.*;
import arc.util.Time;
import fos.content.*;
import mindustry.Vars;
import mindustry.audio.SoundControl;
import mindustry.content.StatusEffects;
import mindustry.game.SpawnGroup;
import mindustry.type.*;

import static mindustry.game.EventType.*;
import static mindustry.Vars.state;

public class MusicHandler {
    public Seq<Music> uxerdAmbient = new Seq<>();
    public Seq<Music> luminaAmbient = new Seq<>();

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
            } else if (curPlanet == FOSPlanets.lumina) {
                control.ambientMusic = control.darkMusic = luminaAmbient;
            }
        });
        Events.on(WaveEvent.class, e -> {
            SpawnGroup boss = state.rules.spawns.find(group -> group.getSpawned(state.wave - 2) > 0 && group.effect == StatusEffects.boss);
            if (boss == null) return;

            if (boss.type == FOSUnits.citadel) {
                control.bossMusic = Seq.with(FOSMusic.livingSteam);
            } else {
                control.bossMusic = vBoss;
            }
        });
        //this should hopefully reset the music back to vanilla
        Events.on(StateChangeEvent.class, e -> {
            if (curPlanet == FOSPlanets.uxerd || curPlanet == FOSPlanets.lumina) return;

            control.ambientMusic = vAmbient;
            control.darkMusic = vDark;
            control.bossMusic = vBoss;
        });
    }

    public void reload(){
        uxerdAmbient = Seq.with(FOSMusic.dive);
        luminaAmbient = Seq.with(FOSMusic.abandoned);

        vAmbient = control.ambientMusic;
        vDark = control.darkMusic;
        vBoss = control.bossMusic;
    }
}
