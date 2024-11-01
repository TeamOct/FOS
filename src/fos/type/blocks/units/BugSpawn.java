package fos.type.blocks.units;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import fos.content.FOSUnitTypes;
import fos.core.FOSVars;
import fos.gen.BWorkerc;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.type.*;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.meta.BuildVisibility;

import static mindustry.Vars.net;

public class BugSpawn extends UnitBlock {
    public float interval;

    public BugSpawn(String name) {
        super(name);
        hasItems = true;
        itemCapacity = 100;
        solid = false;
        canOverdrive = false;
        rotate = false;
        noUpdateDisabled = true;
        buildVisibility = BuildVisibility.editorOnly;
    }

    @SuppressWarnings("unused")
    public class BugSpawnBuild extends UnitBuild {
        @Override
        public void draw() {
            Draw.z(Layer.groundUnit + 1f);
            super.draw();
        }

        @Override
        public void updateTile() {
            if (net.client()) return;

            if (evo() < 0.05f) return;

            progress += delta() * (1 + Math.max(0, evo() - 0.25f));

            float minInterval = interval - 300f;
            float maxInterval = interval + 300f;

            if (Mathf.chance((progress - minInterval) / (maxInterval - minInterval) * (Time.delta / 60f))) {
                var type = getBug();
                Unit unit = type.create(team);
                if (unit instanceof BWorkerc miner) miner.nest(this);
                payload = new UnitPayload(unit);
                payVector.setZero();
                unit.x(this.x); unit.y(this.y);
                unit.add();
                Events.fire(new EventType.UnitCreateEvent(payload.unit, this));

                spawned();
                if (type.getFirstRequirements() != null)
                    items.remove(type.getFirstRequirements());
            }
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();

            if (net.client()) return;

            //spawn a bunch of bugs on nest destruction
            for (int i = 0; i < Mathf.random(3, 6); i++) {
                getBug().spawn(this.team, x + Mathf.random(-20f, 20f), y + Mathf.random(-20f, 20f));
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return item.hardness > 0 && items.get(item) < getMaximumAccepted(item);
        }

        @SuppressWarnings("ConstantConditions")
        private UnitType getBug() {
            UnitType[][] units = {
                { FOSUnitTypes.bugSmall, FOSUnitTypes.bugMedium },
                { FOSUnitTypes.spewer},
                { FOSUnitTypes.bugScout }
            };

            UnitType[] workers = { FOSUnitTypes.bugWorker };

            //unit tier depends on evolution, and a bit of random chance for +1 tier
            int tier = evo() < 0.5f ? Mathf.ceil(evo() / 0.25f) : 3;
            if (Mathf.chance(evo())) tier++;
            int branch = Mathf.random(units.length - 1);

            if (tier > units[branch].length) tier = units[branch].length;
            var type = units[branch][tier-1];
            return (!items.has(type.getFirstRequirements())) ? workers[Math.min(tier-1, workers.length-1)] : type;
        }

        private float evo() {
            return FOSVars.evoController.getTotalEvo();
        }
    }
}
