package fos.type.bullets;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import fos.type.blocks.power.PlasmaLauncher;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.MassDriverBolt;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

public class PlasmaBall extends BasicBulletType {
    public PlasmaBall(){
        super(2f, 500);
        despawnEffect = Fx.absorb;
    }

    @Override
    public void draw(Bullet b) {
        Draw.color(Pal.redLight);
        Draw.rect(Core.atlas.find("fos-plasma-ball-back"), b.x, b.y, 8f, 8f);

        Draw.color(Pal.power);
        Draw.rect(Core.atlas.find("fos-plasma-ball"), b.x, b.y, 6f, 6f);

        if (Mathf.random() < 0.03) Fx.circleColorSpark.at(b.x, b.y);

        Draw.reset();
    }

    @Override
    public void update(Bullet b){
        if(!(b.data() instanceof PlasmaLauncher.LauncherBulletData data)){
            hit(b);
            return;
        }
        float hitDst = 7f;
        if(data.to.dead()){
            return;
        }

        float baseDst = data.from.dst(data.to);
        float dst1 = b.dst(data.from);
        float dst2 = b.dst(data.to);

        boolean intersect = false;

        if(dst1 > baseDst){
            float angleTo = b.angleTo(data.to);
            float baseAngle = data.to.angleTo(data.from);

            if(Angles.near(angleTo, baseAngle, 2f)){
                intersect = true;
                b.set(data.to.x + Angles.trnsx(baseAngle, hitDst), data.to.y + Angles.trnsy(baseAngle, hitDst));
            }
        }
        if(Math.abs(dst1 + dst2 - baseDst) < 4f && dst2 <= hitDst){
            intersect = true;
        }
        if(intersect){
            data.to.handlePayload(b);
        }
    }
}
