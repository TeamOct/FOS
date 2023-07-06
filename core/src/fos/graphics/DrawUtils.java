package fos.graphics;

import arc.Core;
import arc.math.geom.Vec2;
import arc.util.Tmp;

public class DrawUtils {
    public static Vec2 parallax(float x, float y, float height, boolean ignoreCamDst) {
        Tmp.v1.set(1f, 1f);
        Tmp.v2.set(Core.camera.position);

        Tmp.v1.setAngle(Tmp.v2.sub(x, y).angle() + 180f).setLength(ignoreCamDst ? height : height * Tmp.v2.dst(0f, 0f)).add(x, y);

        return Tmp.v1;
    }
}
