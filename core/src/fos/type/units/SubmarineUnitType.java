package fos.type.units;

import fos.ai.SubDiveAI;
import fos.content.FOSCommands;
import fos.type.units.constructors.SubmarineUnit;
import mindustry.ai.UnitCommand;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class SubmarineUnitType extends UnitType {
    public SubmarineUnitType(String name) {
        super(name);
        commands = new UnitCommand[]{
            UnitCommand.moveCommand,
            FOSCommands.diveCommand
        };
        constructor = SubmarineUnit::new;
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);

        //this AI isn't supposed to control the unit for long.
        if (unit.controller() instanceof SubDiveAI) {
            unit.controller(this.controller.get(unit));
        }
    }
}
