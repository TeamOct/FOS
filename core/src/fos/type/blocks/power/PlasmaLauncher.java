package fos.type.blocks.power;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.OrderedSet;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import fos.type.bullets.PlasmaBall;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.power.Battery;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/** ALL OF THIS WAS HORRIBLY REWRITTEN FROM MassDriver */
public class PlasmaLauncher extends Battery {
    public BulletType bullet = new PlasmaBall();
    public float rotateSpeed = 2f;
    public float translation = 7f;
    public float range = 60f * 8;
    public float reload = 100f;

    public PlasmaLauncher(String name){
        super(name);
        hasPower = true;
        update = true;
        configurable = true;
        outlineIcon = true;
        //envRequired = Env.space;
        consumePowerBuffered(1000);
        sync = true;

        config(Point2.class, (PlasmaLauncherBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (PlasmaLauncherBuild tile, Integer point) -> tile.link = point);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.shootRange, range / 8f, StatUnit.blocks);
        stats.add(Stat.reload, 60f / reload, StatUnit.perSecond);
    }

    public static class LauncherBulletData implements Pool.Poolable {
        public PlasmaLauncherBuild from, to;

        @Override
        public void reset() {
            from = to = null;
        }
    }

    @SuppressWarnings("unused")
    public class PlasmaLauncherBuild extends BatteryBuild {
        public int link = -1;
        public float rotation = 90f;
        public float reloadCounter = 0f;
        public LauncherState state = LauncherState.idle;
        public OrderedSet<Building> waiting = new OrderedSet<>();

        public Building currentShooter() {
            return waiting.isEmpty() ? null : waiting.first();
        }

        @Override
        public void drawConfigure(){
            float sin = Mathf.absin(Time.time, 6f, 1f);

            Draw.color(Pal.accent);
            Lines.stroke(1f);
            Drawf.circles(x, y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.accent);

            for(var shooter : waiting){
                Drawf.circles(shooter.x, shooter.y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(shooter.x, shooter.y, x, y, size * tilesize + sin, 4f + sin, Pal.place);
            }

            if(linkValid()){
                Building target = world.build(link);
                Drawf.circles(target.x, target.y, (target.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(x, y, target.x, target.y, size * tilesize + sin, 4f + sin);
            }

            Drawf.dashCircle(x, y, range, Pal.accent);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(this == other){
                if(link == -1) deselect();
                configure(-1);
                return false;
            }

            if(link == other.pos()){
                configure(-1);
                return false;
            }else if(other.block == block && other.dst(tile) <= range && other.team == team){
                configure(other.pos());
                return false;
            }

            return true;
        }

        @Override
        public void updateTile() {
            super.updateTile();

            Building link = world.build(this.link);
            boolean hasLink = linkValid();

            if (hasLink) this.link = link.pos();

            if (reloadCounter > 0f) reloadCounter = Mathf.clamp(reloadCounter - edelta() / reload);

            Building current = currentShooter();

            if (current != null && !otherValid(current)) waiting.remove(current);

            if (state == LauncherState.idle) {
                if (!waiting.isEmpty() && power.status <= 0.5f) state = LauncherState.accepting;
                else if(hasLink) state = LauncherState.shooting;
            }

            if (state == LauncherState.accepting) {
                if (current == null || power.status >= 0.5f) {
                    state = LauncherState.idle;
                    return;
                }

                rotation = Angles.moveToward(rotation, angleTo(current), rotateSpeed);
            } else if (state == LauncherState.shooting) {
                if (!hasLink || (!waiting.isEmpty() && power.status <= 0.5f)) {
                    state = LauncherState.idle;
                    return;
                }

                float targetRot = angleTo(link);

                if (power.status >= 0.5f && link.power.status <= 0.5f) {
                    PlasmaLauncherBuild other = (PlasmaLauncherBuild) link;
                    other.waiting.add(this);
                    if (reloadCounter <= 0.0001f) {
                        rotation = Angles.moveToward(rotation, targetRot, rotateSpeed);

                        if (other.currentShooter() == this && other.state == LauncherState.accepting && Angles.near(rotation, targetRot, 2f) && Angles.near(other.rotation, targetRot + 180f, 2f)){
                            fire(other);
                            float timeToArrive = Math.min(bullet.lifetime, dst(other) / bullet.speed);
                            Time.run(timeToArrive, () -> {
                                other.waiting.remove(this);
                                other.state = LauncherState.idle;
                            });
                            state = LauncherState.idle;
                        }
                    }
                }
            }
        }

        protected void fire(PlasmaLauncherBuild target) {
            LauncherBulletData data = Pools.obtain(LauncherBulletData.class, LauncherBulletData::new);
            data.from = this; data.to = target;

            reloadCounter = 1f;
            power.status -= 0.5f;
            float angle = tile.angleTo(target);
            bullet.create(this, team, x + Angles.trnsx(angle, translation), y + Angles.trnsy(angle, translation), angle, -1f, bullet.speed, bullet.lifetime, data);

            bullet.shootEffect.at(x + Angles.trnsx(angle, translation), y + Angles.trnsy(angle, translation), angle);
            bullet.smokeEffect.at(x + Angles.trnsx(angle, translation), y + Angles.trnsy(angle, translation), angle);
        }

        public void handlePayload(Bullet bullet) {
            power.status += 0.5f;

            reloadCounter = 1f;
            bullet.remove();
        }

        protected boolean linkValid() {
            return link != -1 && world.build(this.link) instanceof PlasmaLauncherBuild other && other.block == block && other.team == team && within(other, range);
        }

        protected boolean otherValid(Building other) {
            return other instanceof PlasmaLauncherBuild entity && other.isValid() && other.efficiency > 0 && entity.block == block && entity.link == pos() && within(other, range);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(link);
            write.f(rotation);
            write.b((byte)state.ordinal());
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            link = read.i();
            rotation = read.f();
            state = LauncherState.all[read.b()];
        }
    }
    public enum LauncherState {
        idle, accepting, shooting;
        public static final LauncherState[] all = values();
    }
}
