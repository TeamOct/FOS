package fos.type;

import arc.Core;
import arc.graphics.*;
import mindustry.gen.Icon;
import mindustry.graphics.MultiPacker;
import mindustry.type.*;

public class ChallengeSectorPreset extends SectorPreset {
    public ChallengeSectorPreset(String name, Planet planet, int sector) {
        super(name, planet, sector);
    }

    @Override
    public void loadIcon() {
        fullIcon = uiIcon = Core.atlas.find("fos-challenge-icon");
    }

    @Override
    public void createIcons(MultiPacker packer) {
        // don't generate the same icon multiple times
        if (!packer.has("fos-challenge-icon")) {
            // color image
            Pixmap base = Core.atlas.getPixmap(Icon.terrain.getRegion()).crop();
            base.each((x, y) -> base.setRaw(x, y, Color.muli(base.getRaw(x, y), Color.red.rgba())));

            packer.add(MultiPacker.PageType.ui, "fos-challenge-icon", base);
        }
    }
}
