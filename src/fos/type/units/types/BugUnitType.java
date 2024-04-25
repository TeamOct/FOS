package fos.type.units.types;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import fos.ai.*;
import fos.content.FOSStatuses;
import fos.gen.*;
import fos.ui.FOSEventTypes;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.abilities.Ability;
import mindustry.entities.part.DrawPart;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.player;

/** Just a template for bugs. */
public class BugUnitType extends UnitType {
    private static final Vec2 legOffset = new Vec2();

    public BugUnitType(String name, boolean flying) {
        super(name);
        isEnemy = false;
        lightOpacity = lightRadius = 0f;
        drawCell = false;
        drawBody = false;
        outlineColor = Color.valueOf("452319");
        createScorch = false;
        //createWreck = false;
        canDrown = false; // FIXME: they still drown for some reason
        deathExplosionEffect = Fx.none;
        deathSound = Sounds.plantBreak;
        immunities.addAll(FOSStatuses.hacked, FOSStatuses.injected, StatusEffects.disarmed, StatusEffects.sapped);
        omniMovement = flying;
        this.flying = flying;
        targetAir = flying;
        targetGround = true;
        targetFlags = new BlockFlag[]{BlockFlag.turret, BlockFlag.drill, BlockFlag.factory, BlockFlag.core, null};
        controller = u -> flying ? new FlyingBugAI() : new BugAI();
    }
    public BugUnitType(String name, boolean flying, boolean melee) {
        this(name, flying);
        if (melee) this.range = 0.01f;
    }

    @Override
    public void draw(Unit unit) {
        if(unit.inFogTo(Vars.player.team())) return;

        boolean isPayload = !unit.isAdded();

        Mechc mech = unit instanceof Mechc ? (Mechc)unit : null;
        float z = isPayload ? Draw.z() : unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);

        if(unit.controller().isBeingControlled(player.unit())){
            drawControl(unit);
        }

        if(!isPayload && (unit.isFlying() || shadowElevation > 0)){
            Draw.z(Math.min(Layer.darkness, z - 1f));
            drawShadow(unit);
        }

        Draw.z(z - 0.02f);

        if(mech != null){
            drawMech(mech);

            //side
            legOffset.trns(mech.baseRotation(), 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 2f/Mathf.PI, 1) * mechSideSway, 0f, unit.elevation));

            //front
            legOffset.add(Tmp.v1.trns(mech.baseRotation() + 90, 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 1f/Mathf.PI, 1) * mechFrontSway, 0f, unit.elevation)));

            unit.trns(legOffset.x, legOffset.y);
        }

        if(unit instanceof Tankc){
            drawTank((Unit & Tankc)unit);
        }

        if(unit instanceof Legsc && !isPayload){
            drawLegs((Unit & Legsc)unit);
        }

        Draw.z(Math.min(z - 0.01f, Layer.bullet - 1f));

        if(unit instanceof Payloadc){
            drawPayload((Unit & Payloadc)unit);
        }

        drawSoftShadow(unit);

        Draw.z(z);

        // this is the only part that got changed for this unit type.
        if(unit instanceof FOSCrawlc c){
            drawCrawl(c);
        }

        if(drawBody) drawOutline(unit);
        drawWeaponOutlines(unit);
        if(engineLayer > 0) Draw.z(engineLayer);
        if(trailLength > 0 && !naval && (unit.isFlying() || !useEngineElevation)){
            drawTrail(unit);
        }
        if(engines.size > 0) drawEngines(unit);
        Draw.z(z);
        if(drawBody) drawBody(unit);
        if(drawCell) drawCell(unit);
        drawWeapons(unit);
        if(drawItems) drawItems(unit);
        drawLight(unit);

        if(unit.shieldAlpha > 0 && drawShields){
            drawShield(unit);
        }

        //TODO how/where do I draw under?
        if(parts.size > 0){
            for(int i = 0; i < parts.size; i++){
                var part = parts.get(i);

                WeaponMount first = unit.mounts.length > part.weaponIndex ? unit.mounts[part.weaponIndex] : null;
                if(first != null){
                    DrawPart.params.set(first.warmup, first.reload / weapons.first().reload, first.smoothReload, first.heat, first.recoil, first.charge, unit.x, unit.y, unit.rotation);
                }else{
                    DrawPart.params.set(0f, 0f, 0f, 0f, 0f, 0f, unit.x, unit.y, unit.rotation);
                }

                if(unit instanceof Scaled s){
                    DrawPart.params.life = s.fin();
                }

                part.draw(DrawPart.params);
            }
        }

        if(!isPayload){
            for(Ability a : unit.abilities){
                Draw.reset();
                a.draw(unit);
            }
        }

        if(mech != null){
            unit.trns(-legOffset.x, -legOffset.y);
        }

        Draw.reset();
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
}
