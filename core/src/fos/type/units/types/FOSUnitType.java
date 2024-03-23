package fos.type.units.types;

import arc.graphics.Color;
import mindustry.type.UnitType;

public class FOSUnitType extends UnitType {
    public FOSUnitType(String name) {
        super(name);
        outlineColor = Color.valueOf("2b2f36");
    }
}
