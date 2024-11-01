package fos.type.units.types;

import fos.gen.Burrowc;
import mindustry.game.Team;
import mindustry.gen.Unit;

public class BurrowUnitType extends BugUnitType {
    public <T extends Unit> BurrowUnitType(String name, Class<T> type) {
        super(name, type);
    }

    //hopefully I did not break everything.
    @Override
    public void draw(Unit unit) {
        if (unit instanceof Burrowc b) {
            if (b.burrowed()) return;
        }

        super.draw(unit);
    }

    @Override
    public boolean targetable(Unit unit, Team targeter) {
        return targetable && (!(unit instanceof Burrowc b) || !b.burrowed());
    }
}
