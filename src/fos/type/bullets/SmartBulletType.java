package fos.type.bullets;

import arc.math.Mathf;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Teamc;

public class SmartBulletType extends BasicBulletType {
    public SmartBulletType(float speed, float damage) {
        super(speed, damage);
    }

    @Override
    public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {

        Teamc e = Units.closestTarget(team, x, y, lifetime * speed);
        return super.create(owner, team, x, y, e != null ? Mathf.angle(e.x() - x, e.y() - y) : angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY);
    }
}
