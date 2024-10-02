package fos.ai.bugs;

import fos.gen.BWorkerc;
import mindustry.content.Blocks;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.Tile;

import static mindustry.Vars.indexer;

public class MinerBugAI extends AIController {
    public boolean mining = true;
    public Item targetItem;
    public Tile ore;

    @Override
    public void updateMovement(){
        BWorkerc bug = (BWorkerc)unit;

        Building core = bug.nest();

        if(!(unit.canMine()) || core == null) return;

        if(unit.mineTile != null && !unit.mineTile.within(unit, unit.type.mineRange)){
            unit.mineTile(null);
        }

        if(ore != null && !unit.validMine(ore, false)){
            ore = null;
            unit.mineTile = null;
        }

        if(mining){
            if(timer.get(timerTarget2, 60 * 4) || targetItem == null){
                targetItem = unit.type.mineItems.min(i -> indexer.hasOre(i) && unit.canMine(i), i -> core.items.get(i));
            }

            //core full of the target item, do nothing
            if(targetItem != null && core.acceptStack(targetItem, 1, unit) == 0){
                unit.clearItem();
                unit.mineTile = null;
                return;
            }

            //if inventory is full, drop it off.
            if(unit.stack.amount >= unit.type.itemCapacity || (targetItem != null && !unit.acceptsItem(targetItem))){
                mining = false;
            }else{
                if(timer.get(timerTarget3, 60) && targetItem != null){
                    ore = indexer.findClosestOre(unit, targetItem);
                }

                if(ore != null){
                    moveTo(ore, unit.type.mineRange / 2f, 20f);

                    if(ore.block() == Blocks.air && unit.within(ore, unit.type.mineRange)){
                        unit.mineTile = ore;
                    }

                    if(ore.block() != Blocks.air){
                        mining = false;
                    }
                }
            }
        }else{
            unit.mineTile = null;

            if(unit.stack.amount == 0){
                mining = true;
                return;
            }

            if(unit.within(core, unit.type.range)){
                if(core.acceptStack(unit.stack.item, unit.stack.amount, unit) > 0){
                    Call.transferItemTo(unit, unit.stack.item, unit.stack.amount, unit.x, unit.y, core);
                }

                unit.clearItem();
                mining = true;
            }

            circle(core, unit.type.range / 1.8f);
        }
    }
}
