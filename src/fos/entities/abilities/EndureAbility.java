package fos.entities.abilities;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.game.EventType;
import mindustry.gen.Unit;

public class EndureAbility extends Ability {
    /** Invulnerability ticks after enduring a fatal blow. */
    public float time = 60f;
    /** Visual effect during endurance. */
    public Effect endureEffect = Fx.flakExplosionBig;
    public float endureEffectChance = 0.05f;

    private EventType.UnitDestroyEvent e;

    /** Whether this ability was already activated on a unit. */
    boolean proc() {
        return data == 1f;
    }



    public EndureAbility() {}

    public EndureAbility(float time) {
        this.time = time;
    }

    @Override
    public void addStats(Table t) {
        super.addStats(t);
        t.add(Core.bundle.format("ability.endure.stat", time / 60f));
    }

    @Override
    public void death(Unit unit) {
        if (!proc()) {
            unit.dead = false;
            unit.health = 1f;
            unit.apply(StatusEffects.invincible, time);

            endureEffect.at(unit);

            // mark as activated so it doesn't activate again (I use data variable to sync state)
            data = 1f;
        }
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);

/*
        if (!proc() && (unit.dead || unit.health <= 0)) {
            unit.dead = false;
            unit.health = 0.1f;
            unit.apply(StatusEffects.invincible, time);

            endureEffect.at(unit);

            // mark as activated so it doesn't activate again (I use data variable to sync state)
            data = 1f;
        }
*/

        if (unit.hasEffect(StatusEffects.invincible) && Mathf.random() < endureEffectChance) {
            endureEffect.at(unit);
        }
    }
}
