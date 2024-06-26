package fos.type.units.types;

import arc.graphics.g2d.Lines;
import fos.gen.Burrowc;
import mindustry.gen.Unit;

public class BurrowUnitType extends BugUnitType {
    public <T extends Unit> BurrowUnitType(String name, Class<T> type) {
        super(name, type);
    }

    //hopefully I did not break everything.
    @Override
    public void draw(Unit unit) {
        if (unit instanceof Burrowc b && b.burrowed()) {
            Lines.poly(unit.x, unit.y, 5, hitSize);
            return;
        }

        super.draw(unit);
    }
}
