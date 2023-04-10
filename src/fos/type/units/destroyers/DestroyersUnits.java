package fos.type.units.destroyers;

import mindustry.content.Liquids;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.LiquidExplodeAbility;

public class DestroyersUnits {
    public static SmokeUnitType smoke;

    public static void load() {
        smoke = new SmokeUnitType("smoke"){{
            health = 1000;
            speed = 16;
            armor = 10;
            flying = true;
            abilities.add(new LiquidExplodeAbility(){{
                liquid = Liquids.gallium;
            }});
        }};
    }
}
