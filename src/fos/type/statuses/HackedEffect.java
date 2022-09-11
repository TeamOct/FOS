package fos.type.statuses;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import fos.type.draw.FOSStats;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class HackedEffect extends StatusEffect {
    public float lifetime = 1800f;

    public HackedEffect(String name) {
        super(name);
        effectChance = 1;
        healthMultiplier = 0.5f;
        effect = new Effect(60, e -> Draw.color(Color.valueOf("51a0b0"), Color.valueOf("8ae3df"), e.fin()));
        permanent = true;
    }

    @Override
    public void update(Unit unit, float time) {
        unit.damageContinuousPierce(unit.maxHealth / lifetime);
        effect.at(unit.x, unit.y);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(FOSStats.lifetime, lifetime / 60f, StatUnit.seconds);
    }
}
