package fos.ui;

import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.ui.Fonts;

import static mindustry.Vars.*;
import static mindustry.game.EventType.Trigger;

/** A class for displaying damage values entities receive. */
public class DamageDisplay {
    /** Entities health check frequency in ticks (1 second = 60 ticks) **/
    public float updateFrequency = 30f;
    /** Stores previous entity health values. */
    public final ObjectFloatMap<Healthc> entitiesHealth = new ObjectFloatMap<>();
    /** Stores previous entity health values. */
    public final ObjectFloatMap<Building> nonUpdateBuildingsHealth = new ObjectFloatMap<>();
    /** Stores entities. **/
    public final Seq<Healthc> entities = new Seq<>();
    /** Stores non-updated buildings. */
    public final Seq<Building> nonUpdateBuildings = new Seq<>();

    /** An effect used for damage display. */
    public static Effect damageShowEffect = new Effect(60f, 80f, (e) -> {
        HealthInfo data = e.data();

        float dmgScale = Mathf.clamp(1f + data.amount / 2500, 1f, 5f);
        float scale = Mathf.sin(e.fin() * 3.14f / 2f) * dmgScale;

        if (scale == 0) return;

        Color color = data.team.color;
        TextureRegionDrawable forDraw;
        switch (data.type) {
            case 1 -> forDraw = Icon.modeAttack;
            case 2 -> forDraw = Icon.wrench;
            default -> forDraw = Icon.defense;
        }

        Draw.z(Layer.effect + 20);
        Draw.color(color.cpy().a(0.8f));

        float realScale = scale * 0.5f;

        TextureRegion r = forDraw.getRegion();
        Draw.rect(forDraw.getRegion(), e.x, e.y, r.width * realScale / 2f, r.height * realScale / 2f);

        Fonts.def.draw(Strings.fixed(data.amount, 0), e.x + r.width * realScale / 4f,
                e.y + r.height * realScale / 4f, Draw.getColor(), realScale, false, Align.left);

        Draw.z(Layer.effect);
        Draw.color();
    });

    // temp variables for this method
    float progress;
    Seq<Building> forRemove = new Seq<>();
    public DamageDisplay() {
        Events.run(Trigger.update, () -> {
            if (Core.settings.getBool("fos-damagedisplay")) {
                if (state.isPlaying()) {
                    progress += Time.delta;
                    if (progress >= updateFrequency) {
                        progress %= updateFrequency;

                        update();
                    }
                }
            }
        });
    }

    void update() {
        // change frequency according to the slider
        updateFrequency = Core.settings.getInt("fos-damagedisplayfrequency", 30);

        // add all in-world entities
        Groups.all.each(e -> {
            if (e instanceof Healthc he)
            if (!entities.contains(he))
                entities.add(he);
        });
        world.tiles.eachTile(tile -> {
            Building building = tile.build;
            if (building == null) return;
            if (!nonUpdateBuildings.contains(building))
                nonUpdateBuildings.add(building);
        });

        // for updatable entitiesHealth
        entities.each(he -> {
            he.clampHealth();
            if (entitiesHealth.containsKey(he)) {
                float delta = entitiesHealth.get(he, 0) - he.health();
                if (delta != 0)
                    damageShowEffect.at(he.x() + Mathf.random(-8f, 8f), he.y() + Mathf.random(4f, 8f), 0, new HealthInfo(
                            delta > 0 ? 1 : 2, Math.abs(delta),
                            he instanceof Unit u ? u.team : he instanceof Building b ? b.team : Team.derelict));
            }

            if (!he.isAdded()) {
                entitiesHealth.remove(he, 0);
                entities.remove(he);
                return;
            }

            entitiesHealth.put(he, he.health());
        });

        // for non-update buildings
        nonUpdateBuildings.each(b -> {
            b.clampHealth();
            if (!b.isAdded()) {
                if (nonUpdateBuildingsHealth.containsKey(b)) {
                    float delta = nonUpdateBuildingsHealth.get(b, 0) - b.health();
                    if (delta != 0)
                        damageShowEffect.at(b.x() + Mathf.random(-8f, 8f), b.y() + Mathf.random(4f, 8f), 0, new HealthInfo(
                                delta > 0 ? 1 : 2, Math.abs(delta), b.team));
                }

                nonUpdateBuildingsHealth.put(b, b.health);
            }
        });

        // do not delete entries inside for-loop
        forRemove.clear();
        nonUpdateBuildingsHealth.each(e -> {
            if (e.key.tile.build != e.key) forRemove.add(e.key);
        });
        forRemove.each(b -> nonUpdateBuildingsHealth.remove(b, 0));
    }

    static class HealthInfo {
        int type;
        float amount;
        Team team;

        public HealthInfo(int t, float a, Team team) {
            type = t;
            amount = a;
            this.team = team;
        }
    }
}
