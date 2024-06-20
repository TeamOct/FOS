package fos.type.content;

import arc.math.Mathf;
import arc.util.*;
import fos.gen.DamageAbsorbc;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class FOSStatusEffect extends StatusEffect {
    public FOSStatusEffect(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit, float time) {
        if(damage > 0){
            if (unit instanceof DamageAbsorbc abs)
                // status effects ignore damage reduction
                abs.damageContinuousPierce(true, damage);
            else
                unit.damageContinuousPierce(damage);
        }else if(damage < 0){ //heal unit
            unit.heal(-1f * damage * Time.delta);
        }

        if(effect != Fx.none && Mathf.chanceDelta(effectChance)){
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize/2f));
            effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
        }
    }
}
