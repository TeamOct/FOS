package fos.type.blocks.special;

import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

/**
 * A placeholder that indicates that more content will be added in the future later.
 * Not researchable.
 * @author Slotterleet
 */
public class PlaceholderBlock extends Block {
    public PlaceholderBlock() {
        super("tbd");
        unlocked = false;
        buildVisibility = BuildVisibility.hidden;
    }

    @Override
    public void unlock() {
        //haha no
    }

    @Override
    public boolean unlocked() {
        //haha no
        return false;
    }

    @Override
    public boolean unlockedNow() {
        //haha no
        return false;
    }

    @Override
    public boolean unlockedNowHost() {
        //haha no
        return false;
    }
}
