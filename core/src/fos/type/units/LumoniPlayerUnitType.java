package fos.type.units;

import mindustry.type.*;
import mindustry.world.meta.*;

public class LumoniPlayerUnitType extends UnitType {
    public LumoniPlayerUnitType(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.weapons);
    }
}
