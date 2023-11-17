package fos.type.units;

import mindustry.gen.Unit;

import static arc.Core.settings;

//Used for bosses that unlock certain contents upon defeat.
public class BossUnitType extends LumoniPlayerUnitType {

    public BossUnitType(String name) {
        super(name);
    }

    @Override
    public void killed(Unit unit) {
        super.killed(unit);
        settings.put(this.name + "-defeated", true);
    }
}
