package fos.type.units.types;

import mindustry.Vars;
import mindustry.gen.Unit;

import static arc.Core.settings;

//Used for bosses that unlock certain contents upon defeat.
public class BossUnitType extends FOSUnitType {

    public <T extends Unit> BossUnitType(String name, Class<T> type) {
        super(name, type);
    }

    @Override
    public void killed(Unit unit) {
        super.killed(unit);

        if (!Vars.state.isCampaign() || unit.team != Vars.state.rules.waveTeam) return;
        settings.put(this.name + "-defeated", true);
    }
}
