package fos.type.statuses;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class HackedEffect extends StatusEffect {
    /*TODO public Team originalTeam;*/

    public HackedEffect(String name) {
        super(name);
        effectChance = 1;
        healthMultiplier = 0.5f;
        effect = new Effect(60, e -> Draw.color(Color.valueOf("51a0b0"), Color.valueOf("8ae3df"), e.fin()));
        permanent = true;
    }

    @Override
    public void update(Unit unit, float time) {
        unit.damage(unit.maxHealth / 1800f);
        effect.at(unit.x, unit.y);
        /*if (!permanent && time <= 1) unit.team = originalTeam;*/
    }


}
