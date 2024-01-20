package fos.type.units;

import mindustry.type.UnitType;
import mindustry.world.meta.Stat;

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
