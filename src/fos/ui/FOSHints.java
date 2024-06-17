package fos.ui;

import arc.*;
import arc.func.Boolp;
import arc.struct.ObjectSet;
import arc.util.*;
import fos.type.blocks.production.UndergroundDrill;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.ui.fragments.HintsFragment;
import mindustry.world.Block;

import static fos.content.FOSBlocks.*;
import static mindustry.Vars.*;
import static mindustry.content.Items.sand;

public class FOSHints {
    static ObjectSet<String> events = new ObjectSet<>();
    static ObjectSet<Block> placedBlocks = new ObjectSet<>();

    public void load() {
        ui.hints.hints.addAll(FOSHint.values());
        for (var h : FOSHint.values()) {
            // remove already finished hints
            if (h.finished()) ui.hints.hints.remove(h);
        }

        Events.on(EventType.BlockBuildEndEvent.class, e -> {
            if (!e.breaking && e.unit == player.unit()) {
                placedBlocks.add(e.tile.block());
            }
        });

        Events.on(FOSEventTypes.InsectInvasionEvent.class, e -> {
            events.add("insects");
        });
    }

    public enum FOSHint implements HintsFragment.Hint {
        insectInvasion(
            () -> events.contains("insects"),
            () -> false
        ),
        drillsPoweredByDetector(
            () -> placedBlocks.contains(zincDrill) && placedBlocks.contains(oreDetector),
            () -> false
        ),
        sandUnderground(
            () -> control.input.block == siliconSynthesizer || placedBlocks.contains(siliconSynthesizer),
            () -> Groups.build.contains(b -> b.team == Vars.player.team() && b instanceof UndergroundDrill.UndergroundDrillBuild ud && ud.dominantItem == sand)
        ),
        detonatorIntro(
            () -> surfaceDetonator.unlocked(),
            () -> placedBlocks.contains(surfaceDetonator)
        );

        @Nullable
        String text;
        int visibility = visibleAll;
        HintsFragment.Hint[] dependencies = {};
        boolean finished, cached;
        Boolp complete, shown = () -> true;

        FOSHint(Boolp complete){
            this.complete = complete;
        }

        FOSHint(int visibility, Boolp complete){
            this(complete);
            this.visibility = visibility;
        }

        FOSHint(Boolp shown, Boolp complete){
            this(complete);
            this.shown = shown;
        }

        FOSHint(int visibility, Boolp shown, Boolp complete){
            this(complete);
            this.shown = shown;
            this.visibility = visibility;
        }

        @Override
        public boolean finished(){
            if(!cached){
                cached = true;
                finished = Core.settings.getBool("fos-" + name() + "-hint-done", false);
            }
            return finished;
        }

        @Override
        public void finish(){
            Core.settings.put("fos-" + name() + "-hint-done", finished = true);
        }

        @Override
        public String text(){
            if(text == null){
                text = Vars.mobile && Core.bundle.has("hint.fos-" + name() + ".mobile") ? Core.bundle.get("hint.fos-" + name() + ".mobile") : Core.bundle.get("hint.fos-" + name());
                if(!Vars.mobile) text = text.replace("tap", "click").replace("Tap", "Click");
            }
            return text;
        }

        @Override
        public boolean complete(){
            return complete.get();
        }

        @Override
        public boolean show(){
            return shown.get() && (dependencies.length == 0 || !Structs.contains(dependencies, d -> !d.finished()));
        }

        @Override
        public int order(){
            return ordinal();
        }

        @Override
        public boolean valid(){
            return (Vars.mobile && (visibility & visibleMobile) != 0) || (!Vars.mobile && (visibility & visibleDesktop) != 0);
        }
    }
}
