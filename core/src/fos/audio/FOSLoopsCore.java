package fos.audio;

import arc.Core;
import arc.audio.Sound;
import fos.core.FOSVars;

public class FOSLoopsCore {
    public static Sound radar = Core.audio.newSound(FOSVars.internalTree.child("sounds/loops/radar.mp3"));
}
