package fos;

import arc.Events;
import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;

import java.lang.reflect.Field;

/** Anuke's issue **/
public class FOSEvents {
    public static ObjectMap<Object, Seq<Cons<?>>> events = new ObjectMap<>();
    static {
        try {
            Field field = Events.class.getDeclaredField("events");
            field.setAccessible(true);
            events = (ObjectMap<Object, Seq<Cons<?>>>) field.get(null);
        } catch (Exception e) {
            Log.err(e);
        }
    }

    public static <T> void run(T type, Cons<T> listener){
        events.get(type, () -> new Seq<>(Cons.class)).add(listener);
    }

    public static <T> boolean remove(T type, Cons<T> listener){
        Log.info("Remover @", events.get(type, () -> new Seq<>(Cons.class)).indexOf(listener));
        return events.get(type, () -> new Seq<>(Cons.class)).remove(listener);
    }
}
