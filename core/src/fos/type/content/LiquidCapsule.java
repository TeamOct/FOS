package fos.type.content;

import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.util.Log;
import arc.util.Tmp;
import fos.controllers.CapsulesController;
import fos.core.FOSMod;
import fos.core.FOSVars;
import mindustry.Vars;
import mindustry.graphics.MultiPacker;
import mindustry.type.Item;
import mindustry.type.Liquid;

/** Must be created via {@link LiquidCapsule#LiquidCapsule(java.lang.String, mindustry.type.Liquid)} and after loading mods. **/
public class LiquidCapsule extends Item {
    private static Pixmap top, bottom;

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

        hidden = liquid.hidden;
        alwaysUnlocked = liquid.alwaysUnlocked;
        generateIcons = true;
    }

    @Override
    public void load() {
        super.load();
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
