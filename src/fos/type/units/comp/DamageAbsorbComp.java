package fos.type.units.comp;

import arc.math.Mathf;
import arc.util.Time;
import fos.type.units.types.FOSUnitType;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.game.Team;
import mindustry.gen.Shieldc;
import mindustry.type.UnitType;

import static ent.anno.Annotations.*;

@EntityComponent @SuppressWarnings("unused")
abstract class DamageAbsorbComp implements Shieldc {
    @Import float armor, healthMultiplier, hitTime;
    @Import static float hitDuration;
    @Import UnitType type;
    @Import Team team;

    float realDamage(boolean isStatus, float amount) {
        return !isStatus && type instanceof FOSUnitType fu ? amount * Mathf.clamp(1 - fu.absorption) : amount;
    }

    @Override
    @Replace(100)
    public void damage(float amount) {
        damage(false, amount);
    }

    void damage(boolean isStatus, float amount) {
        rawDamage(Damage.applyArmor(realDamage(isStatus, amount), armor) / healthMultiplier / Vars.state.rules.unitHealthMultiplier);
    }

    @Override
    @Replace(100)
    public void damagePierce(float amount, boolean withEffect) {
        damagePierce(false, amount, withEffect);
    }

    void damagePierce(boolean isStatus, float amount, boolean withEffect) {
        float pre = hitTime;
        rawDamage(realDamage(isStatus, amount) / healthMultiplier / Vars.state.rules.unitHealth(team));
        if (!withEffect) {
            hitTime = pre;
        }
    }

    @Override
    @Replace(100)
    public void damageContinuous(float amount) {
        damageContinuous(false, amount);
    }

    void damageContinuous(boolean isStatus, float amount) {
        damage(realDamage(isStatus, amount) * Time.delta, hitTime <= -10 + hitDuration);
    }

    @Override
    @Replace(100)
    public void damageContinuousPierce(float amount) {
        damageContinuousPierce(false, amount);
    }

    void damageContinuousPierce(boolean isStatus, float amount) {
        damagePierce(realDamage(isStatus, amount) * Time.delta, hitTime <= -20 + hitDuration);
    }
}
