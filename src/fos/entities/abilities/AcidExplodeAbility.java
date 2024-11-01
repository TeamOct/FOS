package fos.entities.abilities;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import fos.content.FOSStatuses;
import fos.mod.AcidController;
import fos.entities.Acid;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.meta.StatUnit;

public class AcidExplodeAbility extends Ability {
    public int radius = 3;
    public float damage = 5f;
    public float lifetime = 60f;

    static final StatusEffect displayStatus = FOSStatuses.dissolving;

    @Override
    public void addStats(Table t) {
        t.add(/*"[lightgray]" + */Core.bundle.get("ability.acidexplode.description")/* + "[]"*/);
        t.row();
        t.add(Core.bundle.format("bullet.damage", damage * 60) + StatUnit.perSecond.localized());
        t.row();
        t.add(Core.bundle.format("bullet.range", radius));
        t.row();
        t.add(/*displayStatus.emoji() + " " +*/ "[lightgray][stat]" + displayStatus.localizedName + "[] ~ [stat]" + Mathf.round(lifetime / 60f) + "[] " + Core.bundle.get("unit.seconds"));
    }

    @Override
    public void death(Unit unit) {
        unit.tileOn().circle(radius, (x, y) -> {
            Acid.at(AcidController.acid2, unit.team, damage, lifetime, x * Vars.tilesize, y * Vars.tilesize);
        });
    }
}
