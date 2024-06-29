package fos.type.blocks.legacy;

import mindustry.world.*;
import mindustry.world.blocks.legacy.LegacyBlock;

public class LegacyReplaceBlock extends LegacyBlock {
    public Block replacement;

    public LegacyReplaceBlock(String name, Block replacement) {
        super(name);
        this.replacement = replacement;
    }

    @Override
    public void removeSelf(Tile tile) {
        // FIXME: always spawns derelict blocks with 0 rotation
        int rot = tile.build == null ? 0 : tile.build.rotation;
        tile.setBlock(replacement, tile.team(), rot);
    }
}
