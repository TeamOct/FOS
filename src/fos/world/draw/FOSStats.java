package fos.world.draw;

import mindustry.world.meta.*;

public class FOSStats {
    public static final Stat
    lifetime = new Stat("fos-lifetime"),
    hackChanceMultiplier = new Stat("fos-hackchancemultiplier"),

    unitDamageRes = new Stat("fos-unitdamageres"),

    maxBeams = new Stat("fos-maxbeams", StatCat.power),
    damageReduction = new Stat("fos-damagereduction", StatCat.general);
}
