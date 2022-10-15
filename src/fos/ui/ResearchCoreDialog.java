package fos.ui;

import arc.struct.Seq;
import fos.content.FOSVars;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import mindustry.ui.dialogs.ResearchDialog;

public class ResearchCoreDialog extends ResearchDialog {
    public Building rc;

    public ResearchCoreDialog() {
        super();
        titleTable.clear();

        switchTree(FOSVars.mechTree);
    }

    @Override
    public void rebuildItems() {
        items = new ItemSeq(
            Seq.with(ItemStack.with(Items.scrap, rc.items.get(Items.scrap))
            )
        );
    }
}
