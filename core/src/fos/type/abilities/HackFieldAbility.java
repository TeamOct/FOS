package fos.type.abilities;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import fos.content.FOSStatuses;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.type.StatusEffect;
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;

public class HackFieldAbility extends Ability {
    public StatusEffect status;
    public float range, chance;

    public HackFieldAbility(StatusEffect status, float range, float chance) {
        this.status = status;
        this.range = range;
        this.chance = chance;
    }

    @Override
    public void addStats(Table t) {
        t.add("[lightgray]" + Stat.range.localized() + ": [white]" + range / tilesize + " " + StatUnit.blocks.localized());
        t.row();
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
