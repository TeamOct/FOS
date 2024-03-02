package fos.type.bullets;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.Turret;

public class PointLaserBulletType extends ContinuousBulletType {
    public float laserWidth = 0.2f;
    public Color laserColor = Color.scarlet;

    public TextureRegion laser, laserStart, laserEnd;

    public PointLaserBulletType(){
        super();
        damage = 6f;
        length = 250f;
        damageInterval = 10f;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);

        if (b.owner instanceof Turret.TurretBuild t) {
            float ang = t.angleTo(t.targetPos);
            b.x = t.x + Angles.trnsx(ang,
                Math.min(Mathf.dst(t.x, t.y, t.targetPos.x, t.targetPos.y), length));
            b.y = t.y + Angles.trnsy(ang,
                Math.min(Mathf.dst(t.x, t.y, t.targetPos.x, t.targetPos.y), length));
        }
    }

    @Override
    public void load() {
        super.load();

        laser = Core.atlas.find("point-laser");
        laserStart = Core.atlas.find("point-laser-start", "point-laser-end");
        laserEnd = Core.atlas.find("point-laser-end");
    }

    @Override
    public void draw(Bullet b) {
        Draw.z(Layer.bullet);
        var turret = (Turret.TurretBuild)b.owner;
        float ang = turret.angleTo(b.x, b.y);

        Draw.mixcol(laserColor, Mathf.absin(4f, 0.6f));

        Drawf.laser(laser, laserStart, laserEnd,
            turret.x, turret.y,
            b.x, b.y, turret.efficiency * laserWidth);

        Draw.mixcol();
    }
}
