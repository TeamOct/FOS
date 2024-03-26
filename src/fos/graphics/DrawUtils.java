package fos.graphics;

import arc.Core;
import arc.graphics.Texture;
import arc.graphics.g2d.*;
import arc.graphics.gl.FrameBuffer;
import arc.math.geom.Vec2;
import arc.util.*;

import static arc.Core.atlas;

public class DrawUtils {
    private static final float[] vertices = new float[24];
    private static final FrameBuffer buffer = new FrameBuffer();

    public static Vec2 parallax(float x, float y, float height, boolean ignoreCamDst) {
        Tmp.v1.set(1f, 1f);
        Tmp.v2.set(Core.camera.position);

        Tmp.v1.setAngle(Tmp.v2.sub(x, y).angle() + 180f).setLength(ignoreCamDst ? height : height * Tmp.v2.dst(0f, 0f)).add(x, y);

        return Tmp.v1;
    }

    public static Texture quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4){
        float color = Reflect.get(Batch.class, Core.batch, "colorPacked");
        return quad(x1, y1, color, x2, y2, color, x3, y3, color, x4, y4, color);
    }

    public static Texture quad(float x1, float y1, float c1, float x2, float y2, float c2, float x3, float y3, float c3, float x4, float y4, float c4){
        TextureRegion region = atlas.white();
        float mcolor = Reflect.get(Batch.class, Core.batch, "mixColorPacked");
        float u = region.u;
        float v = region.v;
        vertices[0] = x1;
        vertices[1] = y1;
        vertices[2] = c1;
        vertices[3] = u;
        vertices[4] = v;
        vertices[5] = mcolor;

        vertices[6] = x2;
        vertices[7] = y2;
        vertices[8] = c2;
        vertices[9] = u;
        vertices[10] = v;
        vertices[11] = mcolor;

        vertices[12] = x3;
        vertices[13] = y3;
        vertices[14] = c3;
        vertices[15] = u;
        vertices[16] = v;
        vertices[17] = mcolor;

        vertices[18] = x4;
        vertices[19] = y4;
        vertices[20] = c4;
        vertices[21] = u;
        vertices[22] = v;
        vertices[23] = mcolor;

        Draw.vert(region.texture, vertices, 0, vertices.length);

        return region.texture;
    }
}
