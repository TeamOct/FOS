package fos.annotations;

import mindustry.game.MapObjectives;

import java.lang.annotation.*;

public class FOSAnnotations {
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SupportedAnnotationTypes {
        Class<?>[] value();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Settings {
        String[] map();
    }

    /** Creates sound loader class. **/
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.TYPE)
    public @interface CreateSoundHost {
        String[] paths();
        /** Supported files extensions without the dot. **/
        String[] extensions();
        String className();
        /** Max search depth. -1 for disable. **/
        int depth();
    }
}
