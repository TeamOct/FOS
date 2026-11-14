package fos.world.blocks.special;

import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

/**
 * A placeholder node for the tech tree that indicates that more content will be added in the future.
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
    public void quietUnlock() {
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
