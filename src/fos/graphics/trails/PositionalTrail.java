package fos.graphics.trails;

import arc.graphics.Color;
import arc.math.geom.Vec2;
import mindustry.graphics.Trail;

public abstract class PositionalTrail extends Trail {

    public PositionalTrail(int length) {
        super(length);
    }

    public void draw(Vec2 pos, float rot, Color color, float width, boolean ground) {

    }
}
