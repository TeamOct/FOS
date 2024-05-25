package fos.type.blocks.units;

import arc.Events;
import arc.func.Cons;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.*;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.entities.*;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;

public class WaveSpawnerBlock extends Block {
    /** Unit type to spawn. */
    public UnitType unitType;
    /** What wave to spawn this unit on. */
    public int wave;
    /** How much to wait before spawn. */
    public float spawnDelay;
    /** Effect to display during spawn animation. */
    public @Nullable Effect[] animationEffects;
    public float effectDelay = 4f;
    public boolean boss = true;

    public DrawBlock drawer = new DrawDefault();

    public WaveSpawnerBlock(String name) {
        super(name);
        scaledHealth = armor = Float.POSITIVE_INFINITY; // no
        buildVisibility = BuildVisibility.editorOnly;
        targetable = false;
        destructible = false;
        update = true;
        drawTeamOverlay = false;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    protected TextureRegion[] icons() {
        return drawer.icons(this);
    }

    @Override
    public void drawPlan(BuildPlan plan, Eachable<BuildPlan> list, boolean valid) {
        drawer.drawPlan(this, plan, list);
    }

    @SuppressWarnings("unused")
    public class WaveSpawnerBuild extends Building {
        public Cons<EventType.WaveEvent> listener;
        public float progress;

        @Override
        public void draw() {
            drawer.draw(this);

            if (animationEffects != null && progress > 0 && progress < 1 && Time.time % effectDelay < 1f) {
                for (var e : animationEffects) {
                    e.at(this);
                }
            }
        }

        @Override
        public void drawLight() {
            drawer.drawLight(this);
        }

        @Override
        public void created() {
            listener = e -> {
                // mindustry's wave counter is a mess, so add 1
                if (isValid() && Vars.state.wave == wave + 1) {
                    Time.run(spawnDelay - 5, () -> {
                        // LEAVE THE SPAWN AREA IMMEDIATELY
                        // annihilation imminent
                        Damage.damage(null, x, y, 80f, 9999999f, true);
                    });
                    Time.run(spawnDelay, () -> {
                        Unit u = unitType.spawn(team, this);
                        u.rotation(90f);
                        if (boss) u.apply(StatusEffects.boss);

                        // consider this unit spawned from a wave.
                        Events.fire(new EventType.UnitSpawnEvent(u));

                        tileOn().remove();
                    });
                }
            };
            Events.on(EventType.WaveEvent.class, listener);
        }

        @Override
        public void onRemoved() {
            super.onRemoved();
            Events.remove(EventType.WaveEvent.class, listener);
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if (Vars.state.wave == wave + 1) {
                // for drawers only, does not affect anything functionally
                progress = Mathf.approachDelta(progress, 1, 1f / 120);
            } else {
                progress = Mathf.approachDelta(progress, 0, 1f / 120);
            }
        }

        @Override
        public float totalProgress() {
            return progress;
        }

        @Override
        public void write(Writes write) {
            // this actually does nothing IO-related, I just want to remove the event listener after world exit.
            Events.remove(EventType.WaveEvent.class, listener);
        }
    }
}
