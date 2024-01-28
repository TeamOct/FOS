package fos.type.blocks.units;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Structs;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.LAccess;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.units.UnitBlock;

import static mindustry.Vars.state;

/**
 * A recreation of the old Unit Factory from Mindustry v5.
 */
public class V5UnitFactory extends UnitBlock {
    public UnitType unitType;
    public ItemStack[] unitRequirements;
    public float produceTime = 1000f;
    public int maxSpawn = 4;
    public int[] capacities = {};

    public TextureRegion topRegion;

    public V5UnitFactory(String name) {
        super(name);
        update = true;
        hasPower = true;
        hasItems = true;
        solid = true;
        //commandable = true;
        ambientSound = Sounds.respawning;
    }

    @Override
    public void init() {
        super.init();
        capacities = new int[Vars.content.items().size];
        for(ItemStack stack : unitRequirements) {
            capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
            itemCapacity = Math.max(itemCapacity, stack.amount * 2);
        }
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("progress", (V5UnitFactoryBuild e) -> new Bar("bar.progress", Pal.ammo, e::fraction));

        addBar("units", (V5UnitFactoryBuild e) ->
            new Bar(
                () -> unitType == null ? "[lightgray]" + Iconc.cancel :
                    Core.bundle.format("bar.unitcap",
                        Fonts.getUnicodeStr(unitType.name),
                        e.spawned,
                        maxSpawn
                    ),
                () -> Pal.power,
                () -> (float) e.spawned.size / maxSpawn
            )
        );
    }

    public class V5UnitFactoryBuild extends UnitBuild {
        float progress;
        float speedScl;
        Seq<Unit> spawned = new Seq<>();

        public float fraction() {
            return progress / produceTime;
        }

        // some logic crap but I guess I'll add that here too
        @Override
        public Object senseObject(LAccess sensor) {
            if(sensor == LAccess.config) return unitType;
            return super.senseObject(sensor);
        }

        @Override
        public boolean shouldActiveSound() {
            return shouldConsume();
        }

        // more logic crap...
        @Override
        public double sense(LAccess sensor) {
            if(sensor == LAccess.progress) return Mathf.clamp(fraction());
            if(sensor == LAccess.itemCapacity) return Mathf.round(itemCapacity * state.rules.unitCost(team));
            return super.sense(sensor);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            return false;
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            Draw.draw(Layer.blockOver, () -> Drawf.construct(this, unitType, rotdeg() - 90f, progress / produceTime, speedScl, time));

            Draw.z(Layer.blockOver);

            payRotation = rotdeg();
            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion, x, y);
        }

        @Override
        public void updateTile() {
            super.updateTile();

            for (Unit u : spawned) {
                if (u == null || u.dead()) {
                    spawned.remove(u);
                }
            }

            //do nothing if a unit is banned.
            if (unitType.isBanned()) {
                return;
            }

            if (efficiency > 0 && payload == null) {
                time += edelta() * speedScl * Vars.state.rules.unitBuildSpeed(team);
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team);
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
            } else {
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            }

            if (progress >= produceTime) {
                progress %= 1f;

                Unit unit = unitType.create(team);
/*
                if(commandPos != null && unit.isCommandable()){
                    unit.command().commandPosition(commandPos);
                }
*/
                payload = new UnitPayload(unit);
                payVector.setZero();
                consume();
                spawned.add(unit);
                Events.fire(new EventType.UnitCreateEvent(payload.unit, this));

                progress = Mathf.clamp(progress, 0, produceTime);
            } else {
                progress = 0f;
            }
        }

        @Override
        public boolean shouldConsume() {
            return enabled && payload == null;
        }

        @Override
        public int getMaximumAccepted(Item item) {
            return Mathf.round(capacities[item.id] * state.rules.unitCost(team));
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.get(item) < getMaximumAccepted(item) &&
                Structs.contains(unitRequirements, stack -> stack.item == item);
        }
    }
}
