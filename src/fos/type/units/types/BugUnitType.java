package fos.type.units.types;

import arc.*;
import arc.graphics.Color;
import fos.ai.bugs.*;
import fos.content.*;
import fos.gen.Bugc;
import fos.mod.FOSEventTypes;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.meta.BlockFlag;

/** Just a template for bugs. */
public class BugUnitType extends FOSUnitType {
    public <T extends Unit> BugUnitType(String name, Class<T> type) {
        this(name, type, false);
    }

    public <T extends Unit> BugUnitType(String name, Class<T> type, boolean flying) {
        super(name, type);
        isEnemy = false;
        canDrown = false; // FIXME: they still drown for some reason
        omniMovement = flying;
        this.flying = flying;
        targetAir = flying;
        targetGround = true;
        immunities.addAll(FOSStatuses.hacked, FOSStatuses.injected, StatusEffects.sapped, FOSStatuses.dissolving);

        playerControllable = false;
        targetFlags = new BlockFlag[]{BlockFlag.generator, BlockFlag.drill, BlockFlag.factory, BlockFlag.core};
        controller = u -> flying ? new FlyingBugAI() : new BugAI();

        lightOpacity = lightRadius = 0f;
        drawCell = false;
        drawBody = false;
        outlineColor = Color.valueOf("452319");
        //createWreck = false;
        createScorch = false;
        deathExplosionEffect = FOSFx.bugDeath1;
        deathSound = Sounds.plantBreak;
        groundLayer = Layer.legUnit - 0.01f;
    }
    public <T extends Unit> BugUnitType(String name, Class<T> type, boolean flying, boolean melee) {
        this(name, type, flying);

        // weaponless unit's range depends on its mineRange, apparently...
        if (melee) this.mineRange = 0.01f;
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);

        if (!(unit instanceof Bugc b)) return;

        // aggro immediately after being attacked.
        if (b.health() < b.maxHealth()) {
            if (b.isFollowed()) {
                b.invading(true);
            } else if (b.following() instanceof Bugc other) {
                other.invading(true);
            }
            Events.fire(new FOSEventTypes.InsectInvasionEvent());
        }
    }

    @Override
    public void killed(Unit unit) {
        super.killed(unit);
        Events.fire(new FOSEventTypes.InsectDeathEvent(unit.tileOn()));

        // keep track of killed bugs for bestiary
        if (Vars.state.isCampaign() && !Vars.net.client()) {
            int cur = Core.settings.getInt(name + "-count", 0);
            cur++;
            Core.settings.put(name + "-count", cur);
        }
    }
}
