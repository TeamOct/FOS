package fos.type.content;

import arc.Core;
import mindustry.type.*;

public class ChallengeSectorPreset extends SectorPreset {
    public ChallengeSectorPreset(String name, Planet planet, int sector) {
        super(name, planet, sector);
    }

    @Override
    public void loadIcon() {
        uiIcon = fullIcon = Core.atlas.find("fos-challenge");
    }
}
