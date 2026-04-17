package fos.type.units.types;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.*;
import fos.graphics.trails.PositionalTrail;
import mindustry.gen.Unit;
import mindustry.graphics.*;

public class TrailUnitType extends FOSUnitType {
    public Class<? extends Trail> trailType = Trail.class;
    public Seq<Vec2> trailCoords = new Seq<>();

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
            if (!trailCoords.isEmpty() && trail instanceof PositionalTrail pt) {
                for (Vec2 pos : trailCoords) {
                    pt.draw(pos, unit.rotation, trailColor == null ? unit.team.color : trailColor, (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * (useEngineElevation ? unit.elevation : 1f)) * trailScl, unit.isGrounded());
                }
            } else
                trail.draw(trailColor == null ? unit.team.color : trailColor, (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * (useEngineElevation ? unit.elevation : 1f)) * trailScl);
        }

    }
}
