package fos.type.units;

import arc.math.Mathf;
import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

// save it
public class FOSUnitType extends UnlockableContent {
    public static final Seq<FOSUnit> fosUnits = new Seq<>();

    public float maxHealth;

    public FOSUnitType(String name) {
        super(name);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.unit;
    }

    public class FOSUnit implements Entityc, Healthc {
        public boolean added;
        public int id;

        public float x, y;
        public float hitTime;
        public float health;

        public boolean dead;

        @Override
        public <T extends Entityc> T self() {
            return (T) this;
        }

        @Override
        public <T> T as() {
            return (T) this;
        }

        @Override
        public boolean isAdded() {
            return added;
        }

        @Override
        public boolean isLocal() {
            return false;
        }

        @Override
        public boolean isNull() {
            return false;
        }

        @Override
        public boolean isRemote() {
            return false;
        }

        @Override
        public boolean serialize() {
            return false;
        }

        @Override
        public int classId() {
            return 0;
        }

        @Override
        public int id() {
            return id;
        }

        @Override
        public void add() {
            fosUnits.add(fosUnits);
        }

        @Override
        public void afterRead() {

        }

        @Override
        public void id(int i) {
            id = i;
        }

        @Override
        public void read(Reads reads) {
            id = reads.i();
            added = reads.bool();
        }

        @Override
        public void write(Writes writes) {
            writes.i(id);
            writes.bool(added);
        }

        @Override
        public void remove() {
            fosUnits.remove(this);
        }

        @Override
        public void damagePierce(float v) {
            damagePierce(v, true);
        }

        @Override
        public void damagePierce(float v, boolean b) {
            this.damage(v, b);
        }

        @Override
        public void healFract(float v) {
            health(health + v * maxHealth);
        }

        @Override
        public void heal(float v) {
            health(health + v);
        }

        @Override
        public boolean damaged() {
            return maxHealth - health < 0.01f;
        }

        @Override
        public boolean dead() {
            return dead;
        }

        @Override
        public boolean isValid() {
            return !dead && added;
        }

        @Override
        public float health() {
            return health;
        }

        @Override
        public float healthf() {
            return health / maxHealth;
        }

        @Override
        public float hitTime() {
            return hitTime;
        }

        @Override
        public float maxHealth() {
            return maxHealth;
        }

        @Override
        public void clampHealth() {
            health(Mathf.clamp(health, 0f, maxHealth));
        }

        @Override
        public void damage(float v) {
            if (!dead) {
                hitTime = Time.time;

                if (!Vars.net.client()) {
                    health(health - v);
                }
            }
        }

        @Override
        public void damage(float v, boolean b) {
            float pre = hitTime;
            this.damage(v);
            if (!b) {
                hitTime = pre;
            }
        }

        @Override
        public void damageContinuous(float v) {
            this.damage(v * Time.delta, this.hitTime <= -1.0F);
        }

        @Override
        public void damageContinuousPierce(float v) {
            this.damagePierce(v * Time.delta, this.hitTime <= -11.0F);
        }

        @Override
        public void dead(boolean b) {
            dead = b;
        }

        @Override
        public void heal() {
            health(maxHealth);
            dead = false;
        }

        @Override
        public void health(float v) {
            health = v;
            healthChanged();
        }

        public void healthChanged() {

        }

        @Override
        public void hitTime(float v) {

        }

        @Override
        public void kill() {
            // TODO packet sending
        }

        @Override
        public void killed() {
            // TODO packed received
            remove();
            dead = true;
            health = 0f;
        }

        @Override
        public void maxHealth(float v) {

        }

        @Override
        public void update() {

        }

        @Override
        public Floor floorOn() {
            return Vars.world.floor((int) (x / Vars.tilesize), (int) (y / Vars.tilesize));
        }

        @Override
        public Building buildOn() {
            return Vars.world.build((int) (x / Vars.tilesize), (int) (y / Vars.tilesize));
        }

        @Override
        public boolean onSolid() {
            return Vars.world.tile((int) (x / Vars.tilesize), (int) (y / Vars.tilesize)).solid();
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }

        @Override
        public float x() {
            return getX();
        }

        @Override
        public float y() {
            return getY();
        }

        @Override
        public int tileX() {
            return (int) (x() / Vars.tilesize);
        }

        @Override
        public int tileY() {
            return (int) (y() / Vars.tilesize);
        }

        @Override
        public Block blockOn() {
            return Vars.world.tile((int) (x / Vars.tilesize), (int) (y / Vars.tilesize)).block();
        }

        @Override
        public Tile tileOn() {
            return Vars.world.tile((int) (x / Vars.tilesize), (int) (y / Vars.tilesize));
        }

        @Override
        public void set(Position position) {
            x = position.getX();
            y = position.getY();
        }

        @Override
        public void set(float v, float v1) {
            x = v;
            y = v1;
        }

        @Override
        public void trns(Position position) {
            x += position.getX();
            y += position.getY();
        }

        @Override
        public void trns(float v, float v1) {
            x += v;
            y += v1;
        }

        @Override
        public void x(float v) {
            x = v;
        }

        @Override
        public void y(float v) {
            y = v;
        }
    }
}
