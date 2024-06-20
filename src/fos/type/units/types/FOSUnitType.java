package fos.type.units.types;

import arc.graphics.Color;
import fos.gen.EntityRegistry;
import fos.type.draw.FOSStats;
import mindustry.gen.*;
import mindustry.type.UnitType;

public class FOSUnitType extends UnitType {
    /**
     * Physical damage reduction fraction, 0 to 1. Applied before armour.
     * Status effect damage ignores this.
     */
    public float absorption = 0f;

    @SuppressWarnings("unchecked")
    public <T extends Unit> FOSUnitType(String name, Class<T> type) {
        super(name);
        outlineColor = Color.valueOf("2b2f36");
        constructor = EntityRegistry.content(name, type, n -> EntityMapping.map(this.name));
        if (constructor == null) throw new IllegalArgumentException("Unit entity class `" + type + "` not registered.");
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(FOSStats.damageReduction, (absorption * 100) + "%");
    }
}
