package fos.content;

import fos.type.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;

import static mindustry.type.ItemStack.*;

public class FOSBlocks {
    public static Block
    //crafting
    mechSeparator,
    //defense
    meteoriteWall, meteoriteWallLarge;

    public static void load() {
        //region crafting
        mechSeparator = new Separator("mechanical-separator"){{
            hasItems = true;
            size = 2;
            itemCapacity = 10;
            requirements(Category.crafting, with(Items.copper, 200, Items.lead, 50));
            craftTime = 120;
            spinnerSpeed = 1f;
            results = with(Items.lead, 3, Items.graphite, 1);
        }};
        //endregion crafting
        //region defense
        meteoriteWall = new MeteoriteWall("meteorite-wall"){{
            health = 520;
            size = 1;
            requirements(Category.defense, with(FOSItems.meteorite, 6));
        }};
        meteoriteWallLarge = new MeteoriteWall("meteorite-wall-large"){{
            health = 2080;
            size = 2;
            requirements(Category.defense, with(FOSItems.meteorite, 24));
        }};
    }
}
