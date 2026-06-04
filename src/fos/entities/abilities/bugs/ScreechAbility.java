package fos.entities.abilities.bugs;

import arc.Core;
import arc.audio.Sound;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import fos.audio.FOSSounds;
import fos.content.FOSStatuses;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.type.StatusEffect;
import mindustry.ui.Styles;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.ui;

public class ScreechAbility extends Ability {
    public StatusEffect status = FOSStatuses.enraged;
    public float range = 64f;
    public float statusDuration = 600f;

    public Sound screechSound = FOSSounds.bugScreech;
    public float soundPitchMin = 0.8f, soundPitchMax = 1.2f;
    public Effect screechEffect = Fx.flakExplosionBig;

    public ScreechAbility() {}

    @Override
    public void addStats(Table t) {
        t.table(t2 -> {
            super.addStats(t2);
            t2.add(Core.bundle.get("stat.shootrange") + ": [accent]" + (range / 8f) + "[] " + StatUnit.blocks.localized()).left().padTop(4).padBottom(4);
            t2.row();
            t2.add("[accent]" + status.localizedName + "[] ~ [accent]" + (statusDuration / 60) + "[] " + StatUnit.seconds.localized()).left();
        });
        t.button("?", Styles.flatBordert, () -> ui.content.show(status)).size(40f).center().right().grow();
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);

        if (unit.healthf() < 1f && data == 0) {
            data = 1;

            screechSound.at(unit, Mathf.random(soundPitchMin, soundPitchMax));
            screechEffect.at(unit);
            Units.nearby(unit.team, unit.x, unit.y, range, other -> {
                if (other != unit) return;
                other.apply(status, statusDuration);
            });
        }
    }
}
