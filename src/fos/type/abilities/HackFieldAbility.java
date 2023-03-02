package fos.type.abilities;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import fos.content.*;
import mindustry.content.StatusEffects;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

public class HackFieldAbility extends Ability {
    public StatusEffect status;
    public float range, chance;

    public HackFieldAbility(StatusEffect status, float range, float chance) {
        this.status = status;
        this.range = range;
        this.chance = chance;
    }

    @Override
    public String localized() {
        return Core.bundle.format("ability.hackfield", range / 8f, FOSStatuses.hacked.emoji());
    }

    @Override
    public void update(Unit unit) {
        Units.nearbyEnemies(unit.team, unit.x, unit.y, range, other -> {
            if (Mathf.chance(chance)) {
                //do not take over players
                if (other.isPlayer()) return;
                //DO NOT affect bosses, it's extremely overpowered
                if (other.isBoss()) return;
                //do not hack anyone immune to the effect
                if (other.isImmune(FOSStatuses.hacked)) return;

                other.team = unit.team;
                if (other.isBoss()) other.unapply(StatusEffects.boss);
                other.apply(status);
            }
        });
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        Drawf.circles(unit.x, unit.y, range, Color.valueOf("8ae3df"));
    }
}
