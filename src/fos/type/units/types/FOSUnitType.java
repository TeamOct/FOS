package fos.type.units.types;

import arc.graphics.Color;
import mindustry.type.UnitType;

public class FOSUnitType extends UnitType {
    /**
     * Physical damage reduction fraction, 0 to 1. Applied before armour.
     * Does not affect damage from status effects.
     * FIXME: doesn't work.
     */
    public float absorption = 0f;

    public FOSUnitType(String name) {
        super(name);
        outlineColor = Color.valueOf("2b2f36");
    }
}
