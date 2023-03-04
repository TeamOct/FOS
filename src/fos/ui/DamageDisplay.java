package fos.ui;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectFloatMap;
import arc.util.Align;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.core.GameState;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;

import static mindustry.Vars.state;
import static mindustry.Vars.world;
import static mindustry.game.EventType.*;

public class DamageDisplay {
    /** Stores previous building health values. */
    public static ObjectFloatMap<Building> buildings;
    /** Stores previous unit health values. */
    public static ObjectFloatMap<Unit> units;

    static {
        // ?????????????????????????????????
        Events.on(UnitDamageEvent.class, e -> {
            if (!Core.settings.getBool("fos-damagedisplay")) return;

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
            if (!Core.settings.getBool("fos-damagedisplay")) return;

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

    public DamageDisplay() {
        Events.on(StateChangeEvent.class, e -> {
            if (e.to == GameState.State.paused) return;
            buildings = null;
            units = null;
        });

        Events.run(Trigger.update, () -> {
            if (!Core.settings.getBool("fos-damagedisplay")) {
                if (buildings != null || units != null) {
                    buildings = null;
                    units = null;
                }
                return;
            }

            if (buildings == null) {
                buildings = new ObjectFloatMap<>();
                for (int x = 0; x < world.width(); x++) {
                    for (int y = 0; y < world.height(); y++) {
                        Building b = world.build(x, y);
                        if (b != null) {
                            buildings.put(b, b.health);
                        }
                    }
                }
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

        Table table = new Table(Styles.none).margin(4);
        table.touchable = Touchable.disabled;
        table.update(() -> {
            if(state.isMenu()) table.remove();
            Vec2 v = Core.camera.project(worldx, worldy);
            table.setPosition(v.x, v.y, Align.center);
        });
        table.actions(Actions.delay(1.5f), Actions.remove());
        table.add("0").style(Styles.outlineLabel).color(Pal.gray);
        table.pack();
        table.act(0f);
        //make sure it's at the back
        Core.scene.root.addChildAt(0, table);

        Events.run(Trigger.update, () -> table.update(() -> {
            table.y += 1f;
            float a = Time.delta / 60f;
            table.getChildren().each(e -> e.color.a -= a);
        }));
    }

    private static void showDamage(Integer damage, @Nullable Bullet bullet, Teamc target) {
        boolean pierce = bullet != null && bullet.type.pierceArmor;
        Color color = target.team().color;
        float scl = 1 + (Mathf.floor(damage / 200f) / 10f);
        float worldx = target.x() + Mathf.random(-6f, 6f) * scl;
        float worldy = target.y() + Mathf.random(16f, 24f) * scl;

        Table table = new Table(Styles.none).margin(4);
        table.touchable = Touchable.disabled;
        table.update(() -> {
            if(state.isMenu()) table.remove();
            Vec2 v = Core.camera.project(worldx, worldy);
            table.setPosition(v.x, v.y, Align.center);
        });
        table.actions(Actions.delay(1f), Actions.remove());
        //hi meep :)
        if (bullet != null && bullet.owner instanceof Building b && b.block.name.equals("prog-mats-caliber") && damage > bullet.type.damage) {
            table.add("CRITICAL").color(Color.green).fontScale(Math.max(1, scl)).center();
            table.row();
            table.add("HIT!!!").color(Color.green).fontScale(Math.max(1, scl)).center();
            table.row();
        }
        if (damage == 0) {
            table.add("0").style(Styles.outlineLabel).color(Pal.gray);
        } else {
            table.add(damage.toString()).style(Styles.outlineLabel).color(color).fontScale(bullet == null ? 0.5f : Math.max(1, scl));
            if (pierce) table.image(Icon.modeAttackSmall).color(color).fontScale(Math.max(1, scl));
        }
        table.pack();
        table.act(0f);
        //make sure it's at the back
        Core.scene.root.addChildAt(0, table);

        Events.run(Trigger.update, () -> table.update(() -> {
            table.y += 1f;
            float a = Time.delta / 60f;
            table.getChildren().each(e -> e.color.a -= a);
        }));
    }

    private static void showHeal(Integer amount, Teamc target) {
        Color color = target.team().color;
        float worldx = target.x() + Mathf.random(-6f, 6f);
        float worldy = target.y() + Mathf.random(16f, 24f);

        Table table = new Table(Styles.none).margin(4);
        table.touchable = Touchable.disabled;
        table.update(() -> {
            if(state.isMenu()) table.remove();
            Vec2 v = Core.camera.project(worldx, worldy);
            table.setPosition(v.x, v.y, Align.center);
        });
        table.actions(Actions.delay(1.5f), Actions.remove());
        float scl = 1 + (Mathf.floor(amount / 200f) / 10f);
        table.add(amount.toString()).style(Styles.outlineLabel).color(Pal.heal).fontScale(Math.max(1, scl));
        table.image(Icon.wrench).color(color).fontScale(Math.max(1, scl));
        table.pack();
        table.act(0f);
        //make sure it's at the back
        Core.scene.root.addChildAt(0, table);

        Events.run(Trigger.update, () -> table.update(() -> {
            table.y += 1f;
            float a = Time.delta / 60f;
            table.getChildren().each(e -> e.color.a -= a);
        }));
    }
}
