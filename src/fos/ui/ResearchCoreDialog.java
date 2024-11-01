package fos.ui;

import fos.core.FOSVars;
import fos.world.blocks.campaign.ResearchCore.ResearchCoreBuild;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;

public class ResearchCoreDialog extends ResearchDialog {
    public ResearchCoreBuild rc;

    public ResearchCoreDialog() {
        super();

        shown(() -> {
            switchTree(FOSVars.mechTree);
            if (titleTable.hasChildren()) titleTable.getChildren().first().remove();
        });
    }

    @Override
    public void rebuildItems() {
        items = new ItemSeq(){
            {
                values[Items.scrap.id] = Math.max(rc.items.get(Items.scrap), 0);
            }
            @Override
            public void add(Item item, int amount) {
                if (amount < 0) {
                    amount = -amount;
                    rc.items.remove(item, amount);

                    amount = -amount;
                }
                super.add(item, amount);
            }
        };
    }
}
