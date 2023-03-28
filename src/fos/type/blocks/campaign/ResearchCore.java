package fos.type.blocks.campaign;

import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import fos.FOSVars;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.world.blocks.payloads.*;

public class ResearchCore extends PayloadBlock {
    public ResearchCore(String name) {
        super(name);
        update = true;
        configurable = true;
        hasPower = true;
        hasItems = true;
        itemCapacity = 5000;
        acceptsPayload = true;
    }

    @SuppressWarnings("unused")
    public class ResearchCoreBuild extends PayloadBlockBuild<Payload> {
        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            return items.total() < itemCapacity;
        }

        @Override
        public void handlePayload(Building source, Payload payload) {
            float scrap = 0f;

            ItemStack[] stack = payload.requirements();
            for (ItemStack i : stack) {
                scrap += i.item.cost * i.amount;
            }

            scrap = Mathf.round(scrap);

            this.payload = null;
            items.add(Items.scrap, (int) scrap);
        }

        @Override
        public void buildConfiguration(Table table) {
            deselect();

            FOSVars.rcdialog.rc = this;
            FOSVars.rcdialog.show();
        }
    }
}
