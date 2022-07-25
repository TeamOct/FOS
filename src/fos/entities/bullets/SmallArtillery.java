package fos.entities.bullets;

import mindustry.entities.bullet.ArtilleryBulletType;

public class SmallArtillery extends ArtilleryBulletType {
    public SmallArtillery(){
        width = height = 6;
        damage = 5;
        splashDamage = 18;
        splashDamageRadius = 8;
        speed = 2; lifetime = 108;
        collidesTiles = collidesGround = true;
    }
}
