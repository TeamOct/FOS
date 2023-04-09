package fos.type.blocks.defense;

import arc.math.Mathf;
import arc.util.Nullable;
import fos.type.bullets.OhioBeamBulletType;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.turrets.LaserTurret;

import static mindustry.Vars.tilesize;

/** Literally just a {@link LaserTurret} with a modified {@link LaserTurret.LaserTurretBuild} {@code updateTile()} and {@code targetPosition(Posc)} methods. */
public class DeathrayTurret extends LaserTurret {
    public DeathrayTurret(String name) {
        super(name);
        rotate = false;
        /* the entire circle */ shootCone = 360f;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, minRange, Pal.placing);
    }

    @SuppressWarnings("unused")
    public class DeathrayTurretBuild extends LaserTurretBuild {
        @Override
        public void drawLight() {
            Drawf.light(x, y, lightRadius * potentialEfficiency, lightColor, 0.7f * potentialEfficiency);
        }

        @Override
        public void updateTile() {
            super.updateTile();

            bullets.removeAll(b -> !b.bullet.isAdded() || b.bullet.type == null || b.life <= 0f || b.bullet.owner != this);

            if(bullets.any()){
                wasShooting = true;
                heat = 1f;
                curRecoil = 1f;
            }else if(reloadCounter > 0){
                wasShooting = true;

                if(coolant != null){
                    Liquid liquid = liquids.current();
                    float maxUsed = coolant.amount;
                    float used = (cheating() ? maxUsed : Math.min(liquids.get(liquid), maxUsed)) * delta();
                    reloadCounter -= used * liquid.heatCapacity * coolantMultiplier;
                    liquids.remove(liquid, used);

                    if(Mathf.chance(0.06 * used)){
                        coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                    }
                }else{
                    reloadCounter -= edelta();
                }
            }
        }

        @Override
        protected void findTarget() {
            float range = range();
            Bullet b = Groups.bullet.find(e -> e.type instanceof OhioBeamBulletType);
            float cx = b != null ? b.x : this.x;
            float cy = b != null ? b.y : this.y;

            target = Units.closestTarget(team, cx, cy, range - Mathf.dst(this.x, this.y, cx, cy),
                e -> (!Mathf.within(this.x, this.y, e.x, e.y, minRange)) && !e.dead() && unitFilter.get(e) && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround),
                build -> !Mathf.within(this.x, this.y, build.x, build.y, minRange) && targetGround && buildingFilter.get(build));
        }

        @Override
        public void drawSelect() {
            super.drawSelect();

            Drawf.dashCircle(x, y, minRange, team.color);
        }

        @Override
        protected void turnToTarget(float targetRot) {
            //do not turn
            rotation = 90f;
        }

        @Override
        public void targetPosition(Posc pos) {
            //no velocity prediction, just shoot straight at the target
            targetPos.set(pos);
        }

        @Override
        protected void handleBullet(@Nullable Bullet bullet, float offsetX, float offsetY, float angleOffset) {

        }
    }
}
