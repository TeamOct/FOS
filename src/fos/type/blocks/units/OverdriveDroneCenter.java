package fos.type.blocks.units;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

// Original code by Anuke
public class OverdriveDroneCenter extends Block {
    public int unitsSpawned = 4;
    public UnitType droneType;
    public StatusEffect status = StatusEffects.overdrive;
    public float droneConstructTime = 60f * 3f;
    public float statusDuration = 60f * 2f;
    public float droneRange = 50f;

    public OverdriveDroneCenter(String name) {
        super(name);

        update = solid = true;
        configurable = true;
    }

    @Override
    public void init() {
        super.init();

        droneType.aiController = EffectDroneAI::new;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("units", (DroneCenterBuild entity) -> new Bar(
            () -> Core.bundle.format("bar.units", entity.units.size, unitsSpawned),
            () -> Pal.accent,
            () -> (float)entity.units.size / unitsSpawned));
    }

    @SuppressWarnings("unused")
    public class DroneCenterBuild extends Building {
        protected IntSeq readUnits = new IntSeq();

        public Seq<Unit> units = new Seq<>();
        public float droneProgress, droneWarmup, totalDroneProgress;

        @Override
        public void updateTile() {
            //TODO better effects?
            if(units.size < unitsSpawned && (droneProgress += edelta() / droneConstructTime) >= 1f){
                var unit = droneType.create(team);
                if(unit instanceof BuildingTetherc bt){
                    bt.building(this);
                }
                unit.set(x, y);
                unit.rotation = 90f;
                unit.add();

                Fx.spawn.at(unit);
                units.add(unit);
                droneProgress = 0f;
            }
        }

        @Override
        public void drawConfigure() {
            Drawf.square(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));
        }

        @Override
        public void draw() {
            super.draw();

            //TODO draw more stuff

            if(droneWarmup > 0){
                Draw.draw(Layer.blockOver + 0.2f, () ->
                    Drawf.construct(this, droneType.fullIcon,
                        Pal.accent, 0f, droneProgress, droneWarmup, totalDroneProgress, 14f));
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.s(units.size);
            for(var unit : units){
                write.i(unit.id);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            int count = read.s();
            readUnits.clear();
            for(int i = 0; i < count; i++){
                readUnits.add(read.i());
            }

            readUnits.each(i -> units.add(Groups.unit.getByID(i)));
        }
    }

    public class EffectDroneAI extends AIController {
        @Override
        public void updateUnit() {
            if(!(unit instanceof BuildingTetherc tether)) return;
            if(!(tether.building() instanceof OverdriveDroneCenter.DroneCenterBuild)) return;

            target = Vars.indexer.findTile(unit.team, unit.x, unit.y, droneRange, b -> b.block.canOverdrive && b.timeScale() == 1f);
            Building build = (Building)target;

            //TODO what angle?
            moveTo(target, build.hitSize() / 1.8f + droneRange - 10f);

            unit.lookAt(target);

            if(unit.within(target, droneRange + build.hitSize())) {
                build.applyBoost(1.5f * build.efficiency, statusDuration);
            }
        }
    }
}

