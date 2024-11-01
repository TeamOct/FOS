package fos.type;

import arc.graphics.*;
import arc.util.Tmp;
import fos.core.FOSVars;
import mindustry.graphics.MultiPacker;
import mindustry.type.*;

/** Must be created via {@link LiquidCapsule#LiquidCapsule(java.lang.String, mindustry.type.Liquid)} and after loading mods. **/
public class LiquidCapsule extends Item {
    private static final Pixmap top;
    private static final Pixmap bottom;

    static {
        top = new Pixmap(FOSVars.internalTree.child("sprites/items/capsule.png"));
        bottom = new Pixmap(FOSVars.internalTree.child("sprites/items/capsule-liquid.png"));
    }

    public Liquid liquid;

    public LiquidCapsule(String name, Liquid liquid) {
        super(name, liquid.color);
        this.liquid = liquid;

        explosiveness = liquid.explosiveness * 0.8f;
        flammability = liquid.flammability * 0.7f;
        radioactivity = 0;

        hidden = true;
        alwaysUnlocked = liquid.alwaysUnlocked;
        generateIcons = true;
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);

        Pixmap pixmap = new Pixmap(32, 32);

        bottom.each((x, y) -> {
            int c = bottom.get(x, y);
            Tmp.c4.set(Color.ri(c), Color.gi(c), Color.bi(c), Color.ai(c)).mul(liquid.color);
            bottom.set(x, y, Tmp.c4);
        });

        pixmap.draw(bottom);
        pixmap.draw(top, true);

        packer.add(MultiPacker.PageType.main, name, pixmap);
    }
}
