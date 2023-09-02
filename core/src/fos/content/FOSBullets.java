package fos.content;

import arc.graphics.Color;
import mindustry.entities.bullet.*;

public class FOSBullets {
    public static BulletType meteorSpark, smallArtillery, smallStandard, smallStandardFlak, thunderLightning;

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
        thunderLightning = tLaser(0.1f, 5, "3030ff",
            tLaser(0.15f, 4, "25d5ff",
                tLaser(0.25f, 3, "ffff30",
                    tLaser(0.2f, 2, "dc5b2e",
                        tLaser(0.3f, 1, "ff3030", null)
                    )
                )
            )
        );
    }

    private static BulletType tLaser(float dmgMultiplier, int lenMultiplier, String color, BulletType frag) {
        return new LaserBulletType(1500f * dmgMultiplier){{
            speed = 0f; //just in case
            lifetime = 1f;
            width = 48f * (1f / lenMultiplier);
            length = 48f * lenMultiplier;
            colors = new Color[]{
                Color.valueOf(color).mul(1, 1, 1, 0.4f),
                Color.valueOf(color),
                Color.white
            };
            buildingDamageMultiplier = 0.25f;
            displayAmmoMultiplier = false;
            collidesAir = false;

            lightningColor = colors[1];
            lightningDamage = this.damage * 0.2f;
            lightningLength = 9;
            lightningSpacing = 19f + (38f * (lenMultiplier - 1));
            lightningDelay = 3f;
            lightningAngle = 20f;
            lightningAngleRand = 0f;

            intervalAngle = 0f;
            intervalSpread = intervalRandomSpread = 0f;
            bulletInterval = 1f;
            intervalBullets = 1;
            intervalBullet = frag;

            fragOnHit = false;
            fragAngle = 0f;
            fragSpread = fragRandomSpread = 0f;
            fragBullets = 1;
            fragBullet = this.copy();
            fragBullet.lifetime = 30;
            fragBullet.fragBullet = fragBullet.intervalBullet = null;
        }};
    }
}
