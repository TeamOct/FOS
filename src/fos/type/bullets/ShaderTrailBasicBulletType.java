package fos.type.bullets;

import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.util.Time;
import fos.graphics.ShaderTrail;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

import static mindustry.Vars.headless;

public class ShaderTrailBasicBulletType extends BasicBulletType {
    public Shader shader;

    public ShaderTrailBasicBulletType(Shader shader) {
        super();
        this.shader = shader;
    }

    @Override
    public void updateTrail(Bullet b) {
        if(!headless && trailLength > 0){
            if(b.trail == null){
                b.trail = new ShaderTrail(trailLength, shader);
            }
            b.trail.length = trailLength;
            b.trail.update(b.x, b.y, trailInterp.apply(b.fin()) * (1f + (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0f)));
        }
    }
}
