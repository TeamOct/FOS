package fos.type.units.types;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Unit;
import mindustry.graphics.Trail;

public class TrailUnitType extends FOSUnitType {
    public Class<? extends Trail> trailType = Trail.class;

    public <T extends Unit> TrailUnitType(String name, Class<T> type) {
        super(name, type);
    }

    @Override
    public void drawTrail(Unit unit) {
        if(unit.trail == null) {
            try {
                unit.trail = trailType.getDeclaredConstructor(int.class).newInstance(trailLength);
            } catch (Exception ignored) {

            }
        }
        Trail trail = unit.trail;
        if (trail != null) {
            trail.draw(trailColor == null ? unit.team.color : trailColor, (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * (useEngineElevation ? unit.elevation : 1f)) * trailScl);
        }

    }
}
