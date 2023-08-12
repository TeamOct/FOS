package fos.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
