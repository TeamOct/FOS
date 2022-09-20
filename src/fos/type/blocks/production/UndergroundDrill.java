package fos.type.blocks.production;

import arc.math.*;
import fos.type.blocks.environment.UndergroundOreBlock;
import fos.type.blocks.production.DrillBase;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.indexer;
import static mindustry.Vars.state;

public class UndergroundDrill extends Drill {
    public UndergroundDrill(String name){
        super(name);
        drillTime = 360f;
        buildType = UndergroundDrillBuild::new;
    }

    //placeable on drill bases only
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (isMultiblock()) {
            for(Tile other : tile.getLinkedTilesAs(this, tempTiles)) {
                //if mining surface tin, place immediately
                if (other.overlay().name.equals("fos-ore-tin-surface")) return true;

                Building block = other.build;
                return block != null && block.block() instanceof DrillBase && block.team == team;
            }
            return false;
        } else {
            Building block = tile.build;
            return (block != null && block.block() instanceof DrillBase && block.team == team) || tile.overlay().name.equals("fos-ore-tin-surface");
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.drillTier);
        stats.add(Stat.drillTier, StatValues.blocks(b -> (b.name.equals("fos-ore-tin-surface") || b.itemDrop == Items.titanium || b instanceof UndergroundOreBlock) &&
            b.itemDrop != null && b.itemDrop.hardness <= tier && b.itemDrop != blockedItem && (indexer.isBlockPresent(b) || state.isMenu())));
    }

    @Override
    protected void countOre(Tile tile) {
        returnItem = null;
        returnCount = 0;

        oreCount.clear();
        itemArray.clear();

        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(canMine(other) && (other.overlay() instanceof UndergroundOreBlock || other.overlay().name.equals("fos-ore-tin-surface") || getDrop(other) == Items.titanium)){
                oreCount.increment(getDrop(other), 0, 1);
            }
        }

        for(Item item : oreCount.keys()){
            itemArray.add(item);
        }

        itemArray.sort((item1, item2) -> {
            int type = Boolean.compare(!item1.lowPriority, !item2.lowPriority);
            if(type != 0) return type;
            int amounts = Integer.compare(oreCount.get(item1, 0), oreCount.get(item2, 0));
            if(amounts != 0) return amounts;
            return Integer.compare(item1.id, item2.id);
        });

        if(itemArray.size == 0){
            return;
        }

        returnItem = itemArray.peek();
        returnCount = oreCount.get(itemArray.peek(), 0);
    }

    public Item getOutput(Tile tile) {
        countOre(tile);

        //if nothing's under the drill, mine sand
        return returnItem != null ? returnItem : Items.sand;
    }

    public class UndergroundDrillBuild extends DrillBuild {
        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            dominantItem = getOutput(tile);
            if (dominantItem == Items.sand) dominantItems = (int)Mathf.sqr(size);
        }
    }
}
