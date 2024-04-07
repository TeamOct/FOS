package fos.type.units.types;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import fos.ai.*;
import fos.content.FOSStatuses;
import fos.gen.FOSCrawlc;
import mindustry.content.*;
import mindustry.gen.*;
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
        outlineColor = Color.valueOf("452319");
        createScorch = false;
        createWreck = false;
        deathExplosionEffect = Fx.none;
        deathSound = Sounds.plantBreak;
        immunities.addAll(FOSStatuses.hacked, FOSStatuses.injected, StatusEffects.disarmed, StatusEffects.sapped);
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

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        if (unit instanceof FOSCrawlc c) {
            drawCrawl(c);
        }
    }

    public void drawCrawl(FOSCrawlc crawl) {
        Unit unit = (Unit)crawl;
        applyColor(unit);

        TextureRegion[] regions = segmentRegions;
        for(int i = 0; i < segments; i++){
            float trns = Mathf.sin(crawl.crawlTime() + i * segmentPhase, segmentScl, segmentMag);

            //at segment 0, rotation = segmentRot, but at the last segment it is rotation
            float rot = Mathf.slerp(crawl.segmentRot(), unit.rotation, i / (float)(segments - 1));
            float tx = Angles.trnsx(rot, trns), ty = Angles.trnsy(rot, trns);

            //shadow
            Draw.color(0f, 0f, 0f, 0.2f);
            //Draw.rect(regions[i], unit.x + tx + 2f, unit.y + ty - 2f, rot - 90);
            applyColor(unit);

            Draw.rect(regions[i], unit.x + tx, unit.y + ty, rot - 90);
        }
    }
}
