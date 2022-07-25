package fos.type;

import arc.math.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.*;
import fos.entities.bullets.*;

public class MeteoriteWall extends Wall {
    public MeteoriteWall(String name) {
        super(name);
    }

    public class MeteoriteWallBuild extends WallBuild {
        @Override
        public boolean collision(Bullet bullet) {
            super.collision(bullet);

            if (Mathf.chance(0.4)) {
                BulletType bulletType = new MeteorSpark();
                bulletType.create(this, team, x, y, bullet.rotation() + 180f + Mathf.range(10f));
            }

            return true;
        }
    }
}
