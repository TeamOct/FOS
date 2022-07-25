package fos.entities.bullets;

import mindustry.entities.bullet.BasicBulletType;

public class SmallStandard extends BasicBulletType {
    public SmallStandard(){
        width = 4; height = 8;
        damage = 20;
        speed = 1.5f; lifetime = 120;
    }
}
