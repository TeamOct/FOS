package fos.type.blocks;

import arc.math.*;
import fos.content.FOSBullets;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.*;

public class MeteoriteWall extends Wall {
    public MeteoriteWall(String name) {
        super(name);
        buildType = MeteoriteWallBuild::new;
    }

    public class MeteoriteWallBuild extends WallBuild {
        @Override
        public boolean collision(Bullet bullet) {
            super.collision(bullet);

            if (Mathf.chance(0.4)) {
                BulletType bulletType = FOSBullets.meteorSpark;
                bulletType.create(this, team, x, y, bullet.rotation() + 180f + Mathf.range(10f));
            }

            return true;
        }
    }
}
