package fos.content;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.entities.bullet.*;

public class FOSBullets {
    public static BulletType meteorSpark, smallArtillery, smallStandard, smallStandardFlak, thunderLaser;

    public static void load() {
        meteorSpark = new BasicBulletType(4f, 40f){{
            lifetime = 20;
            width = 6; height = 6;
            incendAmount = 1;
            backColor = Color.valueOf("ad6b00");
            frontColor = Color.valueOf("ff9d00");
        }};
        smallArtillery = new ArtilleryBulletType(2f, 5f){{
            width = height = 6;
            splashDamage = 18;
            splashDamageRadius = 8;
            lifetime = 108;
            collidesTiles = collidesGround = true;
        }};
        smallStandard = new BasicBulletType(1.5f, 20f){{
            width = 4; height = 8;
            lifetime = 120;
        }};
        smallStandardFlak = new BasicBulletType(4f, 20f){{
            width = 2; height = 4;
            lifetime = 30;
        }};
        thunderLaser = new ContinuousLaserBulletType(230f){{
            length = 200;
            hitSize = 18;
            lifetime = 40;
            drawSize = 420;
            incendChance = 0.4f;
            incendSpread = 5;
            incendAmount = 1;
            hitEffect = Fx.hitMeltdown;
        }};
    }
}
