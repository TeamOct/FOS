package fos.type.units;

import mindustry.world.meta.Stat;

public class LumoniPlayerUnitType extends FOSUnitType {
    public LumoniPlayerUnitType(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.weapons);
    }
}
