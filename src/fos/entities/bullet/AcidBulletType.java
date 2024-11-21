package fos.entities.bullet;

import fos.entities.Acid;
import fos.mod.AcidController;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class AcidBulletType extends BasicBulletType {
    public float acidDamage = 20f / 60;
    public float acidLifetime = 60f;
    public float acidRadius = 6f;

    public AcidBulletType() {
        super();
        despawnHit = true;
    }

    @Override
    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);

        Acid.at(AcidController.acid2, b.team, acidDamage, acidLifetime, b.x, b.y, acidRadius);
    }
}
