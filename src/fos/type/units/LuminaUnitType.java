package fos.type.units;

import mindustry.type.*;
import mindustry.world.meta.*;

public class LuminaUnitType extends UnitType {
    public LuminaUnitType(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.weapons);
    }
}
