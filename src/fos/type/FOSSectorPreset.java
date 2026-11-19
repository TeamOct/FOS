package fos.type;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Pixmap;
import arc.util.Nullable;
import mindustry.game.Rules;
import mindustry.graphics.*;
import mindustry.type.*;

public class FOSSectorPreset extends SectorPreset {
    /** These rules will have higher priority over even campaign settings. */
    public @Nullable Cons<Rules> rulesOverride;

    public FOSSectorPreset(String name, Planet planet, int sector) {
        super(name, planet, sector);
    }

    public FOSSectorPreset(String name, String fileName, Planet planet, int sector) {
        super(name, fileName, planet, sector);
    }

    @Override
    public void createIcons(MultiPacker packer) {
        //color image
        Pixmap base = Core.atlas.getPixmap(Core.atlas.find("sector-" + name)).crop();
        Pixmap tint = base;
        //base.each((x, y) -> tint.setRaw(x, y, Color.muli(tint.getRaw(x, y), color.rgba())));

        //outline the image
        Pixmap container = new Pixmap(tint.width + 6, tint.height + 6);
        container.draw(base, 3, 3, true);
        base = container.outline(Pal.gray, 3);
        packer.add(MultiPacker.PageType.ui, "sector-" + name, base);
    }
}
