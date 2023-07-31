package fos.annotations;

/** One annotation per project. **/
public @interface CreateSoundHost {
    String[] paths();
    /** Supported files extensions without the dot. **/
    String[] extensions();
    String className();
    /** Max search depth. -1 for disable. **/
    int depth();
}
