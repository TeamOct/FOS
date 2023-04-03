package fos.type.blocks.defense;

import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.gen.Bullet;
import mindustry.gen.Posc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.turrets.LaserTurret;

import static mindustry.Vars.tilesize;

/** Literally just a {@link LaserTurret} with a modified {@link LaserTurret.LaserTurretBuild} {@code updateTile()} and {@code targetPosition(Posc)} methods. */
public class DeathrayTurret extends LaserTurret {
    public DeathrayTurret(String name) {
        super(name);
        rotate = false;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, minRange, Pal.placing);
    }

    @SuppressWarnings("unused")
    public class DeathrayTurretBuild extends LaserTurretBuild {
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
        public void drawSelect() {
            super.drawSelect();

            Drawf.dashCircle(x, y, minRange, team.color);
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
