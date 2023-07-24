package fos.controllers;

import arc.Core;
import arc.graphics.Texture;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Reflect;
import fos.type.content.LiquidCapsule;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;
import mindustry.graphics.MultiPacker;
import mindustry.type.Liquid;

public class CapsulesController {
    public static final float liquidInCapsule = 20;
    public static MultiPacker packer;

    public Seq<LiquidCapsule> capsules = new Seq<>();

    public void load() {
        // remove already created capsules
        ObjectMap<String, MappableContent> itemsMap =
                ((ObjectMap<String, MappableContent>[]) Reflect.get(Vars.content, "contentNameMap"))
                        [ContentType.item.ordinal()];
        capsules.each(capsule -> {
            Vars.content.items().remove(capsule);
            itemsMap.remove(capsule.name);
        });
        capsules.clear();

        Vars.content.liquids().each(liquid -> {
            if (Vars.content.getByName(ContentType.item, "capsule-" + liquid.name) != null) {
                Log.warn("[FOS][Capsule Controller] Capsule with name '@' already defined." +
                        " Is capsule controller loaded multiply?", "capsule-" + liquid.name);
            }
            capsules.add(new LiquidCapsule("capsule-" + liquid.name, liquid));
        });

        packer = new MultiPacker();
        capsules.each(LiquidCapsule::constructIcon);
        packer.flush(Core.settings.getBool("linear", true) ? Texture.TextureFilter.linear
                : Texture.TextureFilter.nearest, Core.atlas);
    }

    @Nullable
    public LiquidCapsule toCapsule(Liquid liquid) {
        return capsules.find(capsule -> capsule.liquid == liquid);
    }
}
