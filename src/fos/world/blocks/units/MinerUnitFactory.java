package fos.world.blocks.units;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.*;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.LAccess;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.UnitTetherBlock;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.units.UnitBlock;

import static mindustry.Vars.state;

/**
 * A recreation of the old Unit Factory from Mindustry v5.
 */
public class MinerUnitFactory extends UnitBlock {
    public UnitType unitType;
    public ItemStack[] unitRequirements;
    public float produceTime = 1000f;
    public int maxSpawn = 4;
    public int[] capacities = {};

    public TextureRegion topRegion;

    public MinerUnitFactory(String name) {
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

        addBar("progress", (MinerUnitFactoryBuild e) -> new Bar("bar.progress", Pal.ammo, e::fraction));

        addBar("units", (MinerUnitFactoryBuild e) ->
            new Bar(
                () -> unitType == null ? "[lightgray]" + Iconc.cancel :
                    Core.bundle.format("bar.unitcap",
                        Fonts.getUnicodeStr(unitType.name),
                        e.units.size,
                        maxSpawn
                    ),
                () -> Pal.power,
                () -> (float) e.units.size / maxSpawn
            )
        );
    }

    public class MinerUnitFactoryBuild extends UnitBuild implements UnitTetherBlock {
        float progress;
        float speedScl;

        IntSeq readUnits = new IntSeq();
        Seq<Unit> units = new Seq<>();

        public float fraction() {
            return progress / produceTime;
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

            // get units from saved IDs
            if (!readUnits.isEmpty()) {
                //units.clear();
                // java sucks
                final boolean[] clear = {true};
                readUnits.each(i -> {
                    var unit = Groups.unit.getByID(i);
                    if (unit != null) {
                        units.add(unit);
                    } else {
                        clear[0] = false;
                    }
                });
                if (clear[0]) readUnits.clear();
            }

            // check if current units are alive
            for (var u : units) {
                if (u == null || u.dead()) {
                    units.remove(u);
                }
            }

            // do nothing if a unit is unavailable
            if (unitType.isBanned() || !unitType.unlockedNow()) {
                return;
            }

            if (efficiency > 0 && payload == null && units.size < maxSpawn) {
                time += edelta() * speedScl * Vars.state.rules.unitBuildSpeed(team);
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team);
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
            } else {
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            }

            moveOutPayload();

            if (progress >= produceTime) {
                Unit unit = unitType.create(team);
/*
                if(commandPos != null && unit.isCommandable()){
                    unit.command().commandPosition(commandPos);
                }
*/
                // OH YEAH HARD-CODING TIME
                payload = new UnitPayload(unit){
                    @Override
                    public boolean dump() {
                        //TODO should not happen
                        if(unit.type == null) return true;

                        if(unit.type.isBanned()){
                            overlayTime = 1f;
                            overlayRegion = null;
                            return false;
                        }

                        //check if unit can be dumped here
                        EntityCollisions.SolidPred solid = unit.solidity();
                        if(solid != null){
                            Tmp.v1.trns(unit.rotation, 1f);

                            int tx = World.toTile(unit.x + Tmp.v1.x), ty = World.toTile(unit.y + Tmp.v1.y);

                            //cannot dump on solid blocks
                            if(solid.solid(tx, ty)) return false;
                        }

                        //cannot dump when there's a lot of overlap going on
                        if(!unit.type.flying && Units.count(unit.x, unit.y, unit.physicSize(), o -> o.isGrounded() && (o.type.allowLegStep == unit.type.allowLegStep)) > 0){
                            return false;
                        }

                        //no client dumping
                        if(Vars.net.client()) return true;

                        //prevents stacking
                        unit.vel.add(Mathf.range(0.5f), Mathf.range(0.5f));
                        unit.add();
                        unit.unloaded();
                        Events.fire(new EventType.UnitUnloadEvent(unit));

                        return true;
                    }
                };

                payVector.setZero();
                consume();
                spawned(unit.id);
                if (unit instanceof BuildingTetherc bt) bt.building(this);
                Events.fire(new EventType.UnitCreateEvent(payload.unit, this));
            } else {
                progress = Mathf.clamp(progress, 0, produceTime);
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

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(progress);
            write.i(units.size);
            for (int i = 0; i < units.size; i++) {
                write.i(units.get(i).id);
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            progress = read.f();
            int units = read.i();
            for (int i = 0; i < units; i++) {
                readUnits.add(read.i());
            }
        }

        @Override
        public void spawned(int id) {
            Fx.spawn.at(x, y);
            progress = 0f;
            readUnits.add(id);
        }
    }
}
