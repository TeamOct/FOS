package fos.entities.bullet;

import arc.math.Mathf;
import arc.math.geom.*;
import mindustry.entities.*;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.gen.Bullet;
import mindustry.type.Liquid;

import static mindustry.Vars.*;

public class FragLiquidBulletType extends LiquidBulletType {
    public FragLiquidBulletType(Liquid liquid) {
        super(liquid);
    }

    // oh COME ON anuke why doesn't it create frags by default.
    @Override
    public void hit(Bullet b, float x, float y){
        hitEffect.at(x, y, b.rotation(), hitColor);
        hitSound.at(x, y, hitSoundPitch, hitSoundVolume);

        Effect.shake(hitShake, hitShake, b);

        if(fragOnHit){
            createFrags(b, x, y);
        }
        createPuddles(b, x, y);
        createIncend(b, x, y);
        createUnits(b, x, y);

        if(suppressionRange > 0){
            //bullets are pooled, require separate Vec2 instance
            Damage.applySuppression(b.team, b.x, b.y, suppressionRange, suppressionDuration, 0f, suppressionEffectChance, new Vec2(b.x, b.y));
        }

        createSplashDamage(b, x, y);

        for(int i = 0; i < lightning; i++){
            Lightning.create(b, lightningColor, lightningDamage < 0 ? damage : lightningDamage, b.x, b.y, b.rotation() + Mathf.range(lightningCone/2) + lightningAngle, lightningLength + Mathf.random(lightningLengthRand));
        }

        Puddles.deposit(world.tileWorld(x, y), liquid, puddleSize);

        if(liquid.temperature <= 0.5f && liquid.flammability < 0.3f){
            float intensity = 400f * puddleSize/6f;
            Fires.extinguish(world.tileWorld(x, y), intensity);
            for(Point2 p : Geometry.d4){
                Fires.extinguish(world.tileWorld(x + p.x * tilesize, y + p.y * tilesize), intensity);
            }
        }
    }
}
