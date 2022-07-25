package fos.entities.bullets;

import arc.graphics.*;
import mindustry.entities.bullet.*;

public class MeteorSpark extends BasicBulletType {
    public MeteorSpark() {
        super(4f, 40f, "fos-meteor-shot");
        lifetime = 20;
        width = 6; height = 6;
        incendAmount = 1;
        backColor = Color.valueOf("ad6b00");
        frontColor = Color.valueOf("ff9d00");
    }
}
