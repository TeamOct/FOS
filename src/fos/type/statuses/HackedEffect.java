package fos.type.statuses;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Tmp;
import fos.gen.DamageAbsorbc;
import fos.graphics.FOSPal;
import fos.world.draw.FOSStats;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.world.meta.StatUnit;

public class HackedEffect extends FOSStatusEffect {
    public float lifetime = 1800f;

    public HackedEffect(String name) {
        super(name);
        effectChance = 1;
        healthMultiplier = 0.5f;
        effect = new Effect(60, e -> Draw.color(FOSPal.hackedBack, FOSPal.hacked, e.fin()));
        permanent = true;
    }

    @Override
    public void update(Unit unit, float time) {
        if (unit instanceof DamageAbsorbc abs)
            abs.damageContinuousPierce(true, (unit.maxHealth * healthMultiplier) / lifetime);
        else
            unit.damageContinuousPierce((unit.maxHealth * healthMultiplier) / lifetime);

        if(effect != Fx.none && Mathf.chanceDelta(effectChance)){
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize/2f));
            effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(FOSStats.lifetime, lifetime / 60f, StatUnit.seconds);
    }
}
