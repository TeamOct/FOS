package fos.type.blocks.units;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import fos.content.FOSUnitTypes;
import mindustry.Vars;
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
        scaledHealth = 400;
        solid = false;
        canOverdrive = false;
        unitCapModifier = 15;
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
            progress += delta();

            if (progress >= interval) {
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

            //unit tier depends on two factors: current wave count and sector difficulty
            int curTier = Mathf.round(Mathf.floor(
                Vars.state.rules.sector != null ? ((Vars.state.wave / 20f) + Vars.state.rules.sector.threat / 2) / 2
                : Vars.state.wave / 20f));
            if (curTier > units[0].length) curTier = units[0].length;
            return units[Mathf.random(units.length - 1)][curTier];
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();

            //spawn a bunch of bugs on nest destruction
            for (int i = 0; i < Mathf.random(3, 6); i++) {
                getBug().spawn(this.team, x + Mathf.random(-20f, 20f), y + Mathf.random(-20f, 20f));
            }
        }
    }
}
