package fos.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.Shader;
import arc.math.*;
import mindustry.graphics.Trail;

public class ShaderTrail extends Trail {
    public Shader shader;

    public ShaderTrail(int length, Shader shader) {
        super(length);
        this.shader = shader;
    }

    @Override
    public void draw(Color color, float width) {
        float[] items = points.items;
        float lastAngle = this.lastAngle;
        float size = width / (points.size / 3);

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
            float z1 = i == 0 ? z2 : lastAngle;
            if(w1 <= 0.001f || w2 <= 0.001f) continue;

            float
                cx = Mathf.sin(z1) * i/3f * size * w1,
                cy = Mathf.cos(z1) * i/3f * size * w1,
                nx = Mathf.sin(z2) * (i/3f + 1) * size * w2,
                ny = Mathf.cos(z2) * (i/3f + 1) * size * w2;

            DrawUtils.quad(
                x1 - cx, y1 - cy,
                x1 + cx, y1 + cy,
                x2 + nx, y2 + ny,
                x2 - nx, y2 - ny
            ).bind(0);

            Draw.blit(shader);

            lastAngle = z2;
        }

        Draw.reset();
    }
}
