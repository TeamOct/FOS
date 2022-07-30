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
        FOSBullets.load();
        FOSStatuses.load();
        FOSUnits.load();
        FOSBlocks.load();
        FOSPlanets.load();
        Log.info("Never gonna give you up");
        Log.info("Never gonna let you down");
        Log.info("Never gonna run around");
        Log.info("And desert you");
    }

}
