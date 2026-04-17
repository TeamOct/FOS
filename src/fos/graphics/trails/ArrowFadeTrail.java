package fos.graphics.trails;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.graphics.*;

public class ArrowFadeTrail extends PositionalTrail {
    public ArrowFadeTrail(int length) {
        super(length);
    }

    @Override
    public void draw(Vec2 pos, float rot, Color color, float width, boolean ground) {
        Draw.color(color);
        Draw.z(ground ? Layer.groundUnit - 0.1f : Layer.light);

        float[] items = points.items;
        float lastAngle = this.lastAngle;
        float size = width / ((float) points.size / 3);
        var a0 = Tmp.c1.set(color).a(0).toFloatBits();

        Tmp.c2.set(color);

        Tmp.v5.set(pos).rotate(rot - 90f);
        float drawX = Tmp.v5.x, drawY = Tmp.v5.y;

        for(int i = 0; i < points.size; i += 3){
            float x1 = items[i], y1 = items[i + 1], w1 = items[i + 2];
            float x2, y2, w2;

            //last position is always lastX/Y/W
            if(i < points.size - 3){
                x2 = items[i + 3];
                y2 = items[i + 4];
                w2 = items[i + 5];
            }else{
                x2 = lastX;
                y2 = lastY;
                w2 = lastW;
            }

            float z2 = -Angles.angleRad(x1, y1, x2, y2);
            //end of the trail (i = 0) has the same angle as the next.
            float z1 = /*i == 0 ? z2 :*/ lastAngle;
            if(w1 <= 0.001f || w2 <= 0.001f) continue;

            float
                cx = Mathf.sin(z1) * i/3f * size * w1,
                cy = Mathf.cos(z1) * i/3f * size * w1,
                nx = Mathf.sin(z2) * (i/3f + 1) * size * w2,
                ny = Mathf.cos(z2) * (i/3f + 1) * size * w2;

            var fcolor = i % 2 == 0 ? Tmp.c2.a((float)i / points.size).toFloatBits() : a0;

            Fill.quad(
                x1 - cx + drawX, y1 - cy + drawY, fcolor,
                x1 - cy + drawX, y1 + cx + drawY, fcolor,
                x2 - ny + drawX, y2 + nx + drawY, fcolor,
                x2 - nx + drawX, y2 - ny + drawY, fcolor
            );
            Fill.quad(
                x1 - cy + drawX, y1 + cx + drawY, fcolor,
                x1 + cx + drawX, y1 + cy + drawY, fcolor,
                x2 + nx + drawX, y2 + ny + drawY, fcolor,
                x2 - ny + drawX, y2 + nx + drawY, fcolor
            );

            Draw.blend(Blending.additive);
            Drawf.light(x1 + drawX, y1 + drawY, width * ((float)i / points.size) * 2, color, (float)i / points.size / 2);
            Draw.blend();

            lastAngle = z2;
        }

        Draw.reset();
    }
}
