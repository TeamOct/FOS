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
import arc.util.Reflect;
import arc.util.Time;
import mindustry.entities.Damage;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;

import static mindustry.Vars.*;
import static mindustry.game.EventType.*;

public class DamageDisplay {
    /** Stores previous building health values for splash damage display. */
    public static ObjectFloatMap<Building> buildings = new ObjectFloatMap<>();

    static {
        // ?????????????????????????????????
        Events.on(UnitDamageEvent.class, e -> {
            if (!Core.settings.getBool("fos-damagedisplay")) return;

            if (e.bullet.type.continuousDamage() > 0 || e.bullet.collides(e.unit) && e.bullet.damage > 0) {
                showDamage(calculateDamage(e.bullet, e.unit), e.bullet, e.unit);
            }
            if (e.bullet.type.splashDamage > 0) {
                showSplashDamage(unitSplashDamage(e.bullet, e.unit), e.bullet, e.unit);
            }
        });
        Events.on(BuildDamageEvent.class, e -> {
            if (!Core.settings.getBool("fos-damagedisplay")) return;

            //I'm too lazy to do anything with Progression Ministry's sandbox walls
            if (e.build.block.name.contains("prog-mats-sandbox-wall")) {
                showZero(e.build);
                return;
            }

            if (world.tileWorld(e.source.x, e.source.y).build == e.build && e.source.damage > 0) {
                showDamage(calculateDamage(e.source, e.build), e.source, e.build);
            }
            if (e.source.type.splashDamage > 0) {
                float prev = buildings.get(e.build, 0f);
                if (prev <= 0f) return;

                int dmg = Mathf.round(prev - e.build.health);
                showSplashDamage(dmg, e.source, e.build);
                buildings.increment(e.build, 0f, -dmg);
                //remove a destroyed building
                if (buildings.get(e.build, 0f) <= 0f) {
                    buildings.remove(e.build, 0f);
                }
            }
        });
    }

    public DamageDisplay() {
        Events.on(WorldLoadEvent.class, e -> {
            for (int x = 0; x < world.width(); x++) {
                for (int y = 0; y < world.height(); y++) {
                    Building b = world.build(x, y);
                    if (b != null) {
                        buildings.put(b, b.health);
                    }
                }
            }
        });
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
        Events.on(StateChangeEvent.class, e -> buildings.clear());
    }

    private static int calculateDamage(Bullet bullet, Teamc target) {
        int result = bullet.type.pierceArmor ? Mathf.round(bullet.damage) : Mathf.round(Damage.applyArmor(bullet.damage,
            target instanceof Unit u ? u.armor : target instanceof Building b ? b.block.armor : 0f));
        if (target instanceof Building) result *= bullet.type.buildingDamageMultiplier;

        return result;
    }

    private static int unitSplashDamage(Bullet bullet, Unit t) {
        float dmg;
        dmg = Reflect.invoke(Damage.class, "calculateDamage",
            new Object[]{
                bullet.type.scaledSplashDamage ? Math.max(0, t.dst(bullet.x, bullet.y) - t.hitSize() / 2f) : t.dst(bullet.x, bullet.y),
                bullet.type.splashDamageRadius,
                bullet.type.splashDamage
            },
            float.class, float.class, float.class);

        return bullet.type.pierceArmor ? Mathf.round(dmg) : Mathf.round(Damage.applyArmor(dmg, t.armor));
    }

    private static int calculateHeal(Bullet bullet, Building build) {
        return Mathf.round(bullet.type.healAmount + (bullet.type.healPercent * build.health));
    }

    private static void showZero(Building target) {
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

    private static void showDamage(Integer damage, Bullet bullet, Teamc target) {
        boolean pierce = bullet.type.pierceArmor;
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
        if (damage == 0) {
            table.add("0").style(Styles.outlineLabel).color(Pal.gray);
        } else {
            table.image(Icon.downSmall).color(color);
            table.add(damage.toString()).style(Styles.outlineLabel).color(color);
            if (pierce) table.image(Icon.modeAttackSmall).color(color);
        }
        int scl = Mathf.floor(damage / 200f);
        for (int i = 0; i < scl; i++) {
            table.scaleBy(1.1f);
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

    private static void showSplashDamage(Integer damage, Bullet bullet, Teamc target) {
        boolean pierce = bullet.type.pierceArmor;
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
        table.image(Icon.commandRallySmall).color(color);
        table.add(damage.toString()).style(Styles.outlineLabel).color(color);
        if (pierce) table.image(Icon.modeAttackSmall).color(color);
        int scl = Mathf.floor(damage / 200f);
        for (int i = 0; i < scl; i++) {
            table.scaleBy(1.1f);
        }
        table.pack();
        table.act(0f);
        //make sure it's at the back
        Core.scene.root.addChildAt(0, table);

        Events.run(Trigger.update, () -> table.update(() -> {
            table.y += 1f;
            float a = Time.delta / 60f;
            table.getChildren().get(0).color.a -= a;
            table.getChildren().get(1).color.a -= a;
            if (table.getChildren().size >= 3) table.getChildren().get(2).color.a -= a;
        }));
    }

    private static void showHeal(Integer amount, Color color, float worldx, float worldy) {
        Table table = new Table(Styles.none).margin(4);
        table.touchable = Touchable.disabled;
        table.update(() -> {
            if(state.isMenu()) table.remove();
            Vec2 v = Core.camera.project(worldx, worldy);
            table.setPosition(v.x, v.y, Align.center);
        });
        table.actions(Actions.delay(1.5f), Actions.remove());
        table.image(Icon.downSmall).color(Pal.heal);
        table.add(amount.toString()).style(Styles.outlineLabel).color(Pal.heal);
        table.image(Icon.wrench).color(color);
        int scl = Mathf.floor(amount / 200f);
        for (int i = 0; i < scl; i++) {
            table.scaleBy(1.1f);
        }
        table.pack();
        table.act(0f);
        //make sure it's at the back
        Core.scene.root.addChildAt(0, table);

        table.getChildren().first().act(0f);

        Events.run(Trigger.update, () -> table.update(() -> {
            table.y += 1f;
            float a = Time.delta / 60f;
            table.getChildren().get(0).color.a -= a;
            table.getChildren().get(1).color.a -= a;
            if (table.getChildren().size >= 3) table.getChildren().get(2).color.a -= a;
        }));
    }
}
