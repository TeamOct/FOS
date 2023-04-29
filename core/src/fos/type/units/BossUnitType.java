package fos.type.units;

import mindustry.ctype.UnlockableContent;
import mindustry.gen.Unit;

//Used for bosses that unlock certain contents upon defeat.
public class BossUnitType extends LumoniPlayerUnitType {
    public UnlockableContent content;

    public BossUnitType(String name, UnlockableContent content) {
        super(name);
        this.content = content;
    }

    @Override
    public void killed(Unit unit) {
        super.killed(unit);
        content.unlock();
    }
}
