package fos.ui;

import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.ObjectFloatMap;
import arc.util.*;
import mindustry.content.StatusEffects;
import mindustry.core.GameState;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.Fonts;

import static mindustry.Vars.world;
import static mindustry.game.EventType.*;

/**
 * A class for displaying damage values entities receive.
 * This is the older, more precise but less optimized version.
 * @author Slotterleet
 */
public class ClassicDamageDisplay {
    /** Stores previous building health values. */
    public static ObjectFloatMap<Building> buildings;
    /** Stores previous unit health values. */
    public static ObjectFloatMap<Unit> units;

    /**
     * An effect used for damage display.
     * Stolen from nekit508's newer DD system.
     */
    public static Effect damageShowEffect = new Effect(60f, 80f, (e) -> {
        DamageInfo data = e.data();
        var bullet = data.bullet;
        float damage = data.damage;

        float dmgScale = Mathf.clamp(1f + damage / 2500, 1f, 5f);
        float scale = Mathf.sin(e.fout() * 3.14f / 2f) * dmgScale;

        if (scale == 0) return;

        Color color =
            damage == 0 ? Color.gray :
            data.type == 2 ? Pal.heal :
            data.target.color;
        TextureRegionDrawable forDraw = null;
        switch (data.type) {
            case 1 -> forDraw = Icon.modeAttackSmall;
            case 2 -> forDraw = Icon.wrench;
        }

        Draw.z(Layer.effect + 20);
        Draw.color(color.cpy().a(0.8f));

        float realScale = scale * 0.5f;

        if (forDraw != null) {
            TextureRegion r = forDraw.getRegion();
            Draw.rect(r, e.x, e.y, r.width * realScale / 2f, r.height * realScale / 2f);
        }
        Fonts.def.draw(Strings.fixed(damage, 0), e.x + (forDraw == null ? 0f : forDraw.getRegion().width * realScale / 4f),
            e.y + (forDraw == null ? 0f : forDraw.getRegion().height * realScale / 4f), Draw.getColor(), realScale, false, Align.left);
        // hello again meep
        if (bullet != null && bullet.owner instanceof Building b && b.block.name.equals("prog-mats-caliber") && damage > bullet.type.damage) {
            Fonts.def.draw("CRITICAL", e.x, e.y - 16f,
                Color.green, 1f, false, Align.center);
            Fonts.def.draw("HIT!!!", e.x, e.y - 32f,
                Color.green, 1f, false, Align.center);
        }

        Draw.z(Layer.effect);
        Draw.color();
    });

    static {
        // ?????????????????????????????????
        Events.on(UnitDamageEvent.class, e -> {
            if (!Core.settings.getBool("fos-classicdamagedisplay")) return;

            //if a unit is invincible, just show 0 damage, as simple as that.
            if (e.unit.hasEffect(StatusEffects.invincible) || e.unit.health == Float.POSITIVE_INFINITY) {
                showZero(e.unit);
                return;
            }

            float prev = units.get(e.unit, 0f);
            if (prev <= 0f) return;

            int dmg = Mathf.round(prev - e.unit.health);
            showDamage(dmg, e.bullet, e.unit);
            units.increment(e.unit, 0f, -(prev - e.unit.health));
            //remove a killed unit
            if (units.get(e.unit, 0f) <= 0f) {
                units.remove(e.unit, 0f);
            }
        });
        Events.on(BuildDamageEvent.class, e -> {
            if (!Core.settings.getBool("fos-classicdamagedisplay")) return;

            //if a building is invincible, just show 0 damage, as simple as that.
            if (e.build.health == Float.POSITIVE_INFINITY) {
                showZero(e.build);
                return;
            }

            float prev = buildings.get(e.build, 0f);
            if (prev <= 0f) return;

            int dmg = Mathf.round(prev - e.build.health);
            showDamage(dmg, e.source, e.build);
            buildings.increment(e.build, 0f, -(prev - e.build.health));
            //remove a destroyed building
            if (buildings.get(e.build, 0f) <= 0f) {
                buildings.remove(e.build, 0f);
            }
        });
    }

    public ClassicDamageDisplay() {
        Events.on(StateChangeEvent.class, e -> {
            if (e.to == GameState.State.paused) return;
            buildings = null;
            units = null;
        });

        Events.run(Trigger.update, () -> {
            if (!Core.settings.getBool("fos-classicdamagedisplay")) {
                if (buildings != null || units != null) {
                    buildings = null;
                    units = null;
                }
                return;
            }

            if (buildings == null) {
                initBuildings();
            } else {
                buildings.each(b -> {
                    //make sure to track heals
                    if (b.key.health > b.value) {
                        showHeal(Mathf.round(b.key.health - b.value), b.key);
                        buildings.increment(b.key, 0f, b.key.health - b.value);
                    } else if (b.key.health < b.value) {
                        buildings.increment(b.key, 0f, b.key.health - b.value);
                    }
                });
            }

            //region units
            if (units == null) {
                units = new ObjectFloatMap<>();
            }
            Groups.unit.each(Unit::hittable, u -> units.put(u, u.health));
            units.each(u -> {
                //make sure to track heals
                if (u.key.health > u.value) {
                    showHeal(Mathf.round(u.key.health - u.value), u.key);
                    units.increment(u.key, 0f, u.key.health - u.value);
                } else if (u.key.health < u.value) {
                    units.increment(u.key, 0f, u.key.health - u.value);
                }
            });
            //endregion
        });

        //region buildings
        Events.on(BlockBuildEndEvent.class, e -> {
            if (buildings == null)
                initBuildings();
            Building b = world.build(e.tile.x, e.tile.y);
            if (b != null) {
                if (e.breaking) {
                    buildings.remove(b, 0f);
                } else {
                    buildings.put(b, b.health);
                }
            }
        });
        //endregion
    }

    private static void showZero(Teamc target) {
        float worldx = target.x() + Mathf.random(-6f, 6f);
        float worldy = target.y() + Mathf.random(16f, 24f);

        damageShowEffect.at(worldx, worldy, 0f, new DamageInfo(0, null, 0f, target.team()));
    }

    private static void showDamage(Integer damage, @Nullable Bullet bullet, Teamc target) {
        boolean pierce = bullet != null && bullet.type.pierceArmor;
        float scl = 1 + (Mathf.floor(damage / 200f) / 10f);
        float worldx = target.x() + Mathf.random(-6f, 6f) * scl;
        float worldy = target.y() + Mathf.random(16f, 24f) * scl;

        damageShowEffect.at(worldx, worldy, 0f, new DamageInfo(pierce ? 1 : 0, bullet, damage, target.team()));
    }

    private static void showHeal(Integer amount, Teamc target) {
        float worldx = target.x() + Mathf.random(-6f, 6f);
        float worldy = target.y() + Mathf.random(16f, 24f);

        damageShowEffect.at(worldx, worldy, 0f, new DamageInfo(2, null, amount, target.team()));
    }

    private static void initBuildings() {
        buildings = new ObjectFloatMap<>();
        for (int x = 0; x < world.width(); x++) {
            for (int y = 0; y < world.height(); y++) {
                Building b = world.build(x, y);
                if (b != null) {
                    buildings.put(b, b.health);
                }
            }
        }
    }

    static class DamageInfo {
        int type;
        @Nullable Bullet bullet;
        float damage;
        Team target;

        public DamageInfo(int t, Bullet b, float d, Team target) {
            type = t;
            bullet = b;
            damage = d;
            this.target = target;
        }
    }
}