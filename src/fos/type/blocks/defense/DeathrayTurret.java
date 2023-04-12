package fos.type.blocks.defense;

import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
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
        targetInterval = 2f;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, minRange, Pal.placing);
    }

    @SuppressWarnings("unused")
    public class DeathrayTurretBuild extends LaserTurretBuild {
        public @Nullable Bullet bullet;

        @Override
        public void drawLight() {
            Drawf.light(x, y, lightRadius * potentialEfficiency, lightColor, 0.7f * potentialEfficiency);
        }

        @Override
        public boolean canConsume() {
            return super.canConsume() && liquids.currentAmount() > 0;
        }

        @Override
        public void updateTile() {
            super.updateTile();

            //if targeting, shoot indefinitely, otherwise stop shooting
            if (bullet != null) {
                if (isShooting() && canConsume()) {
                    bullet.lifetime += edelta();

                    Liquid liquid = liquids.current();
                    float maxUsed = coolant.amount;
                    float used = (cheating() ? maxUsed : Math.min(liquids.get(liquid), maxUsed)) * delta();
                    reloadCounter -= used * liquid.heatCapacity * coolantMultiplier;
                    liquids.remove(liquid, used);
                } else if (bullet.lifetime <= 0) {
                    bullet = null;
                }
            }
        }

        @Override
        protected void findTarget() {
            float range = range();
            Bullet b = this.bullet;
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
        public boolean isShooting() {
            return super.isShooting();
        }

        @Override
        protected void updateShooting() {
            if (bullet != null) return;

            if(reloadCounter <= 0 && efficiency > 0 && !charging() && shootWarmup >= minWarmup){
                BulletType type = peekAmmo();
                shoot(type);
                reloadCounter = reload;
            }
        }

        @Override
        protected void handleBullet(Bullet bullet, float offsetX, float offsetY, float angleOffset) {
            this.bullet = bullet;
        }
    }
}
