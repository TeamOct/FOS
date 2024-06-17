package fos.type.units.types;

import mindustry.gen.Unit;
import mindustry.world.meta.Stat;

public class LumoniPlayerUnitType extends FOSUnitType {
    public <T extends Unit> LumoniPlayerUnitType(String name, Class<T> type) {
        super(name, type);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.weapons);
    }
}
