package fos.type.units;

import arc.math.Mathf;
import mindustry.gen.Payloadc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.blocks.payloads.Payload;

// This one just carries payloads behind themselves.
public class CarrierUnitType extends UnitType {
    public CarrierUnitType(String name) {
        super(name);
    }

    @Override
    public <T extends Unit & Payloadc> void drawPayload(T unit) {
        if(unit.hasPayload()){
            Payload pay = unit.payloads().first();
            float x = unit.x - Mathf.cosDeg(unit.rotation) * 16, y = unit.y - Mathf.sinDeg(unit.rotation) * 16;
            pay.set(x, y, unit.rotation);
            pay.draw();
        }
    }
}
