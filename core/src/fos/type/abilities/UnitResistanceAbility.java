package fos.type.abilities;

import arc.graphics.g2d.*;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import fos.type.draw.FOSStats;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.world.meta.*;

public class UnitResistanceAbility extends Ability {
    public UnitType unitType;
    public float resistance;

    public UnitResistanceAbility(UnitType type, float resistance) {
        this.unitType = type;
        this.resistance = resistance;
    }

    @Override
    public void addStats(Table t) {
        t.add("[lightgray]" + Stat.unitType.localized() + ": [white]" + unitType.localizedName);
        t.row();
        t.add("[lightgray]" + FOSStats.unitDamageRes.localized() + ": [white]+" + Strings.autoFixed(resistance * 100, 1000) + StatUnit.percent.localized());
        t.row();
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        int units = Groups.unit.count(u -> u.type == unitType);
        for (int i = 0; i < units; i++) {
            Draw.color(unit.team.color);
            Draw.alpha(resistance);
            Fill.circle(unit.x, unit.y, unit.hitSize);
            Lines.stroke(2f);
            Lines.circle(unit.x, unit.y, unit.hitSize);
        }
    }

    @Override
    public void update(Unit unit) {
        // Apply damage resistance based on the amount of given unit type.
        int units = Groups.unit.count(u -> u.type == unitType);
        unit.healthMultiplier += resistance * units;
    }
}
