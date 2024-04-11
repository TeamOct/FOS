package fos.type.blocks.units;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import fos.content.FOSUnitTypes;
import fos.core.FOSVars;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.meta.BuildVisibility;

public class BugSpawn extends UnitBlock {
    public float interval;

    public BugSpawn(String name) {
        super(name);
        solid = false;
        canOverdrive = false;
        rotate = false;
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
            if (evo() < 0.05f) return;

            progress += delta() * (1 + Math.max(0, evo() - 0.25f));

            float minInterval = interval - 300f;
            float maxInterval = interval + 300f;

            if (Mathf.chance((progress - minInterval) / (maxInterval - minInterval) * (Time.delta / 60f))) {
                Unit unit = getBug().create(team);
                payload = new UnitPayload(unit);
                payVector.setZero();
                unit.x(this.x); unit.y(this.y);
                unit.add();
                Events.fire(new EventType.UnitCreateEvent(payload.unit, this));

                spawned();
            }
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();

            //spawn a bunch of bugs on nest destruction
            for (int i = 0; i < Mathf.random(3, 6); i++) {
                getBug().spawn(this.team, x + Mathf.random(-20f, 20f), y + Mathf.random(-20f, 20f));
            }
        }


        @SuppressWarnings("ConstantConditions")
        private UnitType getBug() {
            UnitType[][] units = {
                {FOSUnitTypes.bugSmall, FOSUnitTypes.bugMedium}
            };

            //unit tier depends on evolution, and a bit of random chance for +1 tier
            int curTier = evo() < 0.5f ? Mathf.ceil(evo() / 0.25f) : 3;
            if (Mathf.chance(evo())) curTier++;

            if (curTier > units[0].length) curTier = units[0].length;
            return units[Mathf.random(units.length - 1)][curTier - 1];
        }

        private float evo() {
            return FOSVars.evoController.getTotalEvo();
        }
    }
}
