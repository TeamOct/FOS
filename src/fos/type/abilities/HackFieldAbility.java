package fos.type.abilities;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
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
        return Core.bundle.format("ability.hackfield", range / 8f, status.emoji());
    }

    @Override
    public void update(Unit unit) {
        Units.nearbyEnemies(unit.team, unit.x, unit.y, range, other -> {
            if (Mathf.chance(chance)) {
                //do not take over players
                if (other.isPlayer()) return;
                other.team = unit.team;
                if (other.isBoss()) other.unapply(StatusEffects.boss);
                other.apply(FOSStatuses.hacked);
            }
        });
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        Draw.color(Color.valueOf("8ae3df"));
        Drawf.circles(unit.x, unit.y, range);
    }
}
