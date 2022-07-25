package fos;

import arc.util.*;
import fos.content.*;
import mindustry.mod.*;

public class FOSMod extends Mod{

    public FOSMod(){
        //nothing here right now
    }

    @Override
    public void loadContent(){
        FOSItems.load();
        FOSBlocks.load();
        Log.info("FOS content loaded... probably");
    }

}
