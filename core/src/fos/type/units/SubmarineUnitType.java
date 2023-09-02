package fos.type.units;

import arc.graphics.g2d.Draw;
import arc.math.*;
import arc.util.Tmp;
import fos.ai.SubDiveAI;
import fos.content.FOSCommands;
import fos.gen.Submarine;
import fos.gen.Submarinec;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.entities.abilities.Ability;
import mindustry.entities.part.DrawPart;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

import static mindustry.Vars.player;

public class SubmarineUnitType extends UnitType {
    /** Whether this can surface or submerge. */
    public boolean canSurface = true;

    public SubmarineUnitType(String name) {
        super(name);
        commands = new UnitCommand[]{
            UnitCommand.moveCommand,
            FOSCommands.diveCommand
        };
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);

        //this AI isn't supposed to control the unit for long.
        if (unit.controller() instanceof SubDiveAI) {
            unit.controller(this.controller.get(unit));
        }
    }

    @Override
    public void applyColor(Unit unit) {
        super.applyColor(unit);

        Draw.mixcol(Tmp.c1.set(unit.tileOn().floor().mapColor).mul(0.83f), ((Submarinec)unit).submerged() ? 0.8f : 0f);
    }

    //hopefully I did not break everything.
    @Override
    public void draw(Unit unit) {
        if(unit.inFogTo(Vars.player.team())) return;

        boolean isPayload = !unit.isAdded();

        float z = isPayload ? Draw.z() : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f) + unit.elevation;

        if(unit.controller().isBeingControlled(player.unit())){
            drawControl(unit);
        }

        Draw.z(Math.min(z - 0.01f, Layer.bullet - 1f));

        if(unit instanceof Payloadc){
            drawPayload((Unit & Payloadc)unit);
        }

        drawSoftShadow(unit);

        Draw.z(z);
        Draw.alpha(((Submarinec)unit).submerged() ? 0.3f : 1f);

        if(drawBody) drawOutline(unit);
        drawWeaponOutlines(unit);

        if(drawBody) drawBody(unit);
        if(drawCell) drawCell(unit);
        drawWeapons(unit);
        if(drawItems) drawItems(unit);
        drawLight(unit);

        if(unit.shieldAlpha > 0 && drawShields){
            drawShield(unit);
        }

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

        Draw.reset();
    }

    @Override
    public void drawShadow(Unit unit) {
        if (!((Submarinec)unit).submerged()) super.drawShadow(unit);
    }

    @Override
    public void drawSoftShadow(Unit unit) {
        if (!((Submarinec)unit).submerged()) super.drawSoftShadow(unit);
    }
}
