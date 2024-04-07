package fos.type.blocks.crafting;

import multicraft.MultiCrafter;

public class ResourceExtractor extends MultiCrafter {
    public ResourceExtractor(String name) {
        super(name);
    }

    //I made this just to fix the block's pistons lmao.
    @SuppressWarnings("unused")
    public class ResourceExtractorBuild extends MultiCrafterBuild {
        public float totalProgress;

        @Override
        public void updateTile() {
            super.updateTile();

            totalProgress += edelta();
        }

        @Override
        public float totalProgress() {
            return totalProgress;
        }
    }
}
