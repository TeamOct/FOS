package fos.type.bullets;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;

public class InjectorHackLaserBulletType extends ContinuousLaserBulletType implements InjectorBulletType {
    public TextureRegion laser, laserCenter, laserEnd;
    public String laserName = "mend"; // TODO

    public float minHP, maxHP;
    /** Maximum time required to hack an enemy (with 0% chance), in seconds. */
    public float maxTime = 60f;
    /** Minimum time required to hack an enemy (with 100% chance), in seconds. */
    public float minTime = 3f;

    public InjectorHackLaserBulletType() {
        continuous = true;
        lifetime = maxTime * 60;
    }

    @Override
    public void load() {
        super.load();
        laser = Core.atlas.find("fos-" + laserName + "-laser");
        laserCenter = Core.atlas.find("fos-" + laserName + "-laser-center");
        laserEnd = Core.atlas.find("fos-" + laserName + "-laser-end");
    }

    @Override
    public float minChance() {
        return 0;
    }

    @Override
    public float maxChance() {
        return 1;
    }

    @Override
    public float minHP() {
        return minHP;
    }

    @Override
    public float maxHP() {
        return maxHP;
    }

    @Override
    public boolean attacksGuardians() {
        return true;
    }

    @Override
    public void update(Bullet b) {
        if (b.data == null)
            b.data = new HackBulletData();

        var d = (HackBulletData)b.data;
        if (d.target == null)
            d.target = Groups.unit.find(u -> u.x == ((Weaponsc)b.owner).aimX() && u.y == ((Weaponsc)b.owner).aimY());

        if (d.target == null || d.target.dead()) {
            b.remove();
            return;
        }

        float requiredTime = (minTime + (maxTime - minTime) * (1 - chance(b.owner, d.target))) * 60;

        d.progress += Time.delta;

        if (d.progress >= requiredTime) {
            onHit(b, d.target, true);
            b.remove();
        }
    }

    @Override
    public void draw(Bullet b) {
        var data = (HackBulletData)b.data;
        if (data == null) return;
        Drawf.laser(laser, laserEnd, b.x, b.y, data.target.x(), data.target.y());
    }

    public class HackBulletData {
        public Unitc target;
        public float progress;
    }
}
