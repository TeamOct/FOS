package fos.type.blocks.environment;

import arc.graphics.g2d.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

public class BugSpawnBlock extends SpawnBlock {
    public BugSpawnBlock(String name) {
        super(name);
        health = 8000;
        variants = 1;
        needsSurface = true;
    }

    @Override
    public void drawBase(Tile tile) {
        Draw.rect(region, tile.worldx(), tile.worldy());
    }
}
