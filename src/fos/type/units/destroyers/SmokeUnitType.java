package fos.type.units.destroyers;

import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;

public class SmokeUnitType extends UnitType {
    public SmokeUnitType(String name) {
        super(name);
        constructor = SmokeUnit::new;
    }

    @Override
    public void killed(Unit unit) {
        super.killed(unit);

    }

    public class SmokeUnit extends UnitEntity {

    }
}
