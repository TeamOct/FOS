package fos.type.units.comp;

import arc.math.Mathf;
import fos.type.units.types.FOSUnitType;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;

import static mindustry.annotations.Annotations.*;

@Component
public abstract class DamageAbsorbComp implements Unitc {
    @Import float armor, healthMultiplier;
    @Import UnitType type;

    @Override
    @Replace
    public void damage(float amount) {
        if (type instanceof FOSUnitType fu) {
            rawDamage(Damage.applyArmor(amount * Mathf.clamp(1 - fu.absorption), armor) / healthMultiplier / Vars.state.rules.unitHealthMultiplier);
        } else {
            rawDamage(Damage.applyArmor(amount, armor) / healthMultiplier / Vars.state.rules.unitHealthMultiplier);
        }
    }
}
