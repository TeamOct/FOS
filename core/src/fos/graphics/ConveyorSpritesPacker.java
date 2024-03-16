package fos.graphics;

import arc.Core;
import arc.graphics.g2d.TextureAtlas;
import arc.struct.Seq;
import arc.util.Log;
import fos.core.FOSVars;
import mindustry.Vars;

import java.lang.reflect.Field;

public class ConveyorSpritesPacker { // TODO rename
    public static Field packer;
    static {
        try {
            packer = Vars.mods.getClass().getDeclaredField("packer");
            packer.setAccessible(true);
        } catch (Exception somethingWentWrong) {
            throw new RuntimeException(somethingWentWrong);
        }
    }

    public static String getPrefix() {
        return FOSVars.mod.name + "-conveyor";
    }

    public static void pack(){ // TODO rename
        Log.info("Conveyor sprites packer working.");
        String prefix = getPrefix();
        Log.info("prefix: @", prefix);
        for (TextureAtlas.AtlasRegion region : Core.atlas.getRegions())
            if (region.name.startsWith(prefix))
                generateRegions(region, prefix);
    }

    static Seq<TextureAtlas.AtlasRegion> generatedRegions = new Seq<>();
    public static void generateRegions(TextureAtlas.AtlasRegion region, String prefix) { // TODO generate via template
        Log.info("generating regions for @", region);
        String name = region.name.substring(prefix.length()+1);
        Log.info("  name @", name);

        String baseName = FOSVars.mod.name + "-" + name;

        generatedRegions.clear();
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                TextureAtlas.AtlasRegion newRegion = new TextureAtlas.AtlasRegion(
                        region.texture, region.getX() + x * 32, region.getY() + y * 32, 32, 32);

                if(region.splits != null){
                    newRegion.splits = region.splits;
                    newRegion.pads = region.pads;
                }

                newRegion.name = baseName + (x == 4 ? "-top-" + y : "-" + y + "-" + x);
                newRegion.offsetX = region.offsetX;
                newRegion.offsetY = (int)(region.originalHeight - region.height - region.offsetY);
                newRegion.originalWidth = region.originalWidth;
                newRegion.originalHeight = region.originalHeight;

                generatedRegions.add(newRegion);
            }
        }

/*
        TextureAtlas.AtlasRegion mainRegion = new TextureAtlas.AtlasRegion(generatedRegions.first());
        mainRegion.name = baseName;
        generatedRegions.add(mainRegion);
*/

        Log.info("  generated regions @", generatedRegions);

        Core.atlas.getRegions().addAll(generatedRegions);
        generatedRegions.each(r -> {
            Core.atlas.getRegionMap().put(r.name, r);
        });
    }
}
