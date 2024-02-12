package fos.type.abilities;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.Table;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;

public class DamageFieldAbility extends Ability {
    public float range, damage;
    public Color borderColor;

    public DamageFieldAbility(float range, float damage) {
        this.range = range;
        this.damage = damage;
    }

    public DamageFieldAbility() {}

    public void addStats(Table t) {
        t.add("[lightgray]" + Stat.range.localized() + ": [white]" + range / tilesize + " " + StatUnit.blocks.localized());
        t.row();
    }

    @Override
    public void update(Unit unit) {
        Units.nearbyEnemies(unit.team, unit.x, unit.y, range, other -> {
            other.damageContinuous(damage);
        });
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        Draw.color(borderColor);
        Lines.stroke(2f);
        Lines.circle(unit.x, unit.y, range);

        Draw.reset();
    }
}
