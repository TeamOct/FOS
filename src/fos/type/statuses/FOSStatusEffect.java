package fos.type.statuses;

import arc.Core;
import arc.graphics.*;
import arc.math.Mathf;
import arc.util.*;
import fos.gen.DamageAbsorbc;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.StatusEffect;

public class FOSStatusEffect extends StatusEffect {
    public FOSStatusEffect(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit, float time) {
        if(damage > 0){
            if (unit instanceof DamageAbsorbc abs)
                // status effects ignore damage reduction
                abs.damageContinuousPierce(true, damage);
            else
                unit.damageContinuousPierce(damage);
        }else if(damage < 0){ //heal unit
            unit.heal(-1f * damage * Time.delta);
        }

        if(effect != Fx.none && Mathf.chanceDelta(effectChance)){
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize/2f));
            effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
        }
    }

    @Override
    public void createIcons(MultiPacker packer) {
        //color image
        Pixmap base = Core.atlas.getPixmap(uiIcon).crop();
        Pixmap tint = base;
        base.each((x, y) -> tint.setRaw(x, y, Color.muli(tint.getRaw(x, y), color.rgba())));

        //outline the image
        Pixmap container = new Pixmap(tint.width + 6, tint.height + 6);
        container.draw(base, 3, 3, true);
        base = container.outline(Pal.gray, 3);
        packer.add(MultiPacker.PageType.ui, name, base);
    }
}
