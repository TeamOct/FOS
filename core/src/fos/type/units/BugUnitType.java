package fos.type.units;

import arc.graphics.Color;
import fos.ai.*;
import mindustry.content.Fx;
import mindustry.gen.Sounds;
import mindustry.type.UnitType;
import mindustry.world.meta.BlockFlag;

/** Just a template for bugs. */
public class BugUnitType extends UnitType {
    public BugUnitType(String name, boolean flying) {
        super(name);
        isEnemy = false;
        lightOpacity = lightRadius = 0f;
        drawCell = false;
        drawBody = false;
        outlineColor = Color.valueOf("5a2a1b");
        createScorch = false;
        createWreck = false;
        deathExplosionEffect = Fx.none;
        deathSound = Sounds.noammo;
        omniMovement = flying;
        this.flying = flying;
        targetAir = flying;
        targetGround = true;
        targetFlags = new BlockFlag[]{BlockFlag.drill, BlockFlag.factory, BlockFlag.core, null};
        controller = u -> flying ? new FlyingBugAI() : new BugAI();
    }
    public BugUnitType(String name, boolean flying, boolean melee) {
        this(name, flying);
        if (melee) this.range = 0.01f;
    }
}
