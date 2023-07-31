package fos.controllers;

import arc.Events;
import arc.struct.*;
import arc.util.*;
import fos.type.content.LiquidCapsule;
import mindustry.Vars;
import mindustry.async.AsyncProcess;
import mindustry.ctype.*;
import mindustry.game.EventType;
import mindustry.type.Liquid;

public class CapsulesController implements AsyncProcess {
    public static final float liquidInCapsule = 20;

    public Seq<LiquidCapsule> capsules = new Seq<>();

    public CapsulesController() {
        Vars.asyncCore.processes.add(this);

        Events.on(EventType.UnlockEvent.class, e -> {
            if (!(e.content instanceof Liquid))
                return;

            for (int i = 0; i < capsules.size; i++) {
                LiquidCapsule capsule = capsules.get(i);
                if (capsule.liquid.equals(e.content))
                    capsule.unlock();
            }
        });
    }

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
    }

    @Nullable
    public LiquidCapsule toCapsule(Liquid liquid) {
        return capsules.find(capsule -> capsule.liquid == liquid);
    }

    @Override
    public void init() {
        capsules.each(capsule -> {
            if (capsule.liquid.unlocked())
                capsule.unlock();
        });
    }

    @Override
    public boolean shouldProcess() {
        return false;
    }
}
