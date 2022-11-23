package fos.type.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
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

import static mindustry.Vars.*;

public class UndergroundDrill extends Drill {
    public UndergroundDrill(String name){
        super(name);
        drillTime = 360f;
        buildType = UndergroundDrillBuild::new;
    }

    //placeable on drill bases or surface ores, or replaceable by other underground drills
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (isMultiblock()) {
            for(Tile other : tile.getLinkedTilesAs(this, tempTiles)) {
                //if mining surface ores (only works with FOS's ones), place immediately
                if (other.overlay().minfo.mod != null && (!(other.overlay() instanceof UndergroundOreBlock) && other.overlay().minfo.mod.name.equals("fos"))) return true;

                Building block = other.build;
                if (block != null && (block.block() instanceof DrillBase || block.block() instanceof UndergroundDrill) && block.team == team) return true;
            }
            return false;
        } else {
            Building block = tile.build;
            return (block != null && (block.block() instanceof DrillBase || block.block() instanceof UndergroundDrill) && block.team == team) ||
                (tile.overlay().minfo.mod != null && (!(tile.overlay() instanceof UndergroundOreBlock) && tile.overlay().minfo.mod.name.equals("fos")));
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Tile tile = world.tile(x, y);
        if(tile == null) return;

        countOre(tile);

        if(returnItem != null){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / getDrillTime(returnItem) * returnCount, 2), x, y, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(returnItem.fullIcon, dx, dy - 1, s, s);
            Draw.reset();
            Draw.rect(returnItem.fullIcon, dx, dy, s, s);

            if(drawMineItem){
                Draw.color(returnItem.color);
                Draw.rect(itemRegion, tile.worldx() + offset, tile.worldy() + offset);
                Draw.color();
            }
        }else{
            Tile to = tile.getLinkedTilesAs(this, tempTiles).find(t -> getUnderDrop(t.overlay()) != null && (getUnderDrop(t.overlay()).hardness > tier || getUnderDrop(t.overlay()) == blockedItem));
            Item item = to == null ? null : to.drop();
            if(item != null){
                drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, valid);
            }
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.drillTier);
        stats.add(Stat.drillTier, StatValues.blocks(b -> (b.name.equals("fos-ore-tin-surface") || b.itemDrop == Items.titanium || b instanceof UndergroundOreBlock) &&
            getUnderDrop(b) != null && getUnderDrop(b).hardness <= tier && getUnderDrop(b) != blockedItem && (indexer.isBlockPresent(b) || state.isMenu())));
    }

    @Override
    public boolean canMine(Tile tile) {
        if(tile == null || tile.block().isStatic()) return false;
        Item drops = getUnderDrop(tile.overlay());
        return drops != null && drops.hardness <= tier && drops != blockedItem;
    }

    @Override
    protected void countOre(Tile tile) {
        returnItem = null;
        returnCount = 0;

        oreCount.clear();
        itemArray.clear();

        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(canMine(other) && (other.overlay() instanceof UndergroundOreBlock || (other.overlay().minfo.mod != null && other.overlay().minfo.mod.name.equals("fos")) || getDrop(other) == Items.titanium)){
                oreCount.increment(getUnderDrop(other.overlay()), 0, 1);
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

    protected Item getUnderDrop(Block b) {
        return b instanceof UndergroundOreBlock u ? u.drop : b.itemDrop;
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
