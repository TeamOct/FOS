package fos.ui;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.core.GameState;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Build;

import static mindustry.Vars.state;
import static mindustry.Vars.world;
import static mindustry.game.EventType.*;

public class DamageDisplay {
    /** Entities health check frequency in ticks (1 second = 60 ticks) **/
    public float updateFrequency = 30f;
    /** Stores previous entity health values. */
    public static final ObjectFloatMap<Healthc> entities = new ObjectFloatMap<>();
    /** Stores previous entity health values. */
    public static final ObjectFloatMap<Building> nonUpdateBuildings = new ObjectFloatMap<>();

    public static Effect damageShowEffect = new Effect(60f, 80f, (e) -> {
        HealthInfo data = e.data();

        float scale = Mathf.sin(e.fin() * 3.14f / 2f);

        if (scale == 0) return;

        Color color = null;
        TextureRegionDrawable forDraw = null;
        switch (data.type) {
            case 0 -> {
                forDraw = Icon.defense;
                color = Pal.gray;
            }
            case 1 -> {
                forDraw = Icon.modeAttack;
                color = Pal.health;
            }
            case 2 -> {
                forDraw = Icon.wrench;
                color = Pal.regen;
            }
        }

        Draw.z(Layer.effect + 20);
        Draw.color(color.cpy().a(0.6f));

        float realScale = scale * 0.5f;

        TextureRegion r = forDraw.getRegion();
        Draw.rect(forDraw.getRegion(), e.x, e.y, r.width * realScale / 2f, r.height * realScale / 2f);

        GlyphLayout g = Fonts.def.draw(Strings.fixed(data.amount, 0), e.x + r.width * realScale / 4f,
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

                        // for updatable entities
                        Groups.all.each(e -> {
                            if (e instanceof Healthc he) {
                                if (!e.isAdded()) {
                                    entities.remove(he, 0);
                                    return;
                                }

                                if (entities.containsKey(he)) {
                                    float delta = entities.get(he, 0) - he.health();
                                    if (delta != 0)
                                        damageShowEffect.at(he.x(), he.y(), 0, new HealthInfo(
                                                delta > 0 ? 1 : 2, Math.abs(delta)));
                                }

                                entities.put(he, he.health());
                            }
                        });

                        // for non-update buildings
                        world.tiles.eachTile(tile -> {
                            Building building = tile.build;
                            if (building == null) return;
                            if (!building.isAdded()) {
                                if (nonUpdateBuildings.containsKey(building)) {
                                    float delta = nonUpdateBuildings.get(building, 0) - building.health();
                                    if (delta != 0)
                                        damageShowEffect.at(building.x(), building.y(), 0, new HealthInfo(
                                                delta > 0 ? 1 : 2, Math.abs(delta)));
                                }

                                nonUpdateBuildings.put(building, building.health);
                            }
                        });

                        // do not delete entries inside for-loop
                        forRemove.clear();
                        nonUpdateBuildings.each(e -> {
                            if (e.key.tile.build != e.key) forRemove.add(e.key);
                        });
                        forRemove.each(b -> nonUpdateBuildings.remove(b, 0));
                    }
                }
            }
        });
    }

    static class HealthInfo {
        int type;
        float amount;

        public HealthInfo(int t, float a) {
            type = t;
            amount = a;
        }
    }
}
