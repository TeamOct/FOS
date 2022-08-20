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
        FOSAttributes.load();
        FOSWeathers.load();
        FOSItems.load();
        FOSBullets.load();
        FOSStatuses.load();
        FOSUnits.load();
        FOSBlocks.load();
        FOSPlanets.load();
        FOSSectors.load();
        UxerdTechTree.load();
    }
}
