package fos.type.blocks.units;

import arc.Events;
import arc.math.Mathf;
import fos.content.FOSUnitTypes;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.meta.BuildVisibility;

public class BugSpawn extends UnitBlock {
    public float interval;

    public BugSpawn(String name) {
        super(name);
        scaledHealth = 1000;
        solid = false;
        canOverdrive = false;
        buildVisibility = BuildVisibility.debugOnly;
    }

    @SuppressWarnings("unused")
    public class BugSpawnBuild extends UnitBuild {
        @Override
        public void updateTile() {
            progress += delta();

            if(progress >= interval){
                Unit unit = getBug().create(team);
                payload = new UnitPayload(unit);
                payVector.setZero();
                unit.x(this.x); unit.y(this.y);
                unit.add();
                Events.fire(new EventType.UnitCreateEvent(payload.unit, this));

                spawned();
            }
        }

        @SuppressWarnings("ConstantConditions")
        private UnitType getBug() {
            UnitType[][] units = {
                //I actually made ACTUAL bugs spawn, yay!
                {FOSUnitTypes.bugSmall}
            };

            int curTier = Mathf.round(Mathf.floor(
                Vars.state.rules.sector != null ? ((Vars.state.wave / 20f) + Vars.state.rules.sector.threat / 2) / 2
                : Vars.state.wave / 20f));
            if (curTier > units[0].length) curTier = units[0].length;
            return units[Mathf.random(units.length - 1)][curTier];
        }
    }
}
