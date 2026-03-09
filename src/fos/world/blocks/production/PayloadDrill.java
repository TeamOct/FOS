package fos.world.blocks.production;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.*;
import arc.util.*;
import fos.world.blocks.environment.UndergroundOreBlock;
import fos.world.blocks.storage.DetectorCoreBlock;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.logic.Ranged;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.blocks.payloads.BlockProducer;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class PayloadDrill extends BlockProducer {
    public float drillTime = 520f;

    protected @Nullable Block returnItem;
    protected int returnCount;
    protected final ObjectIntMap<Block> oreCount = new ObjectIntMap<>();
    protected final Seq<Block> itemArray = new Seq<>();

    public PayloadDrill(String name) {
        super(name);
    }

    //placeable on scanned tiles or replaceable by other underground drills
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (isMultiblock()) {
            for(Tile other : tile.getLinkedTilesAs(this, tempTiles)) {
                Building block = other.build;
                if (block != null && block.block() instanceof UndergroundDrill && block.team == team) return true;
            }
            return nearestDetector(team, tile.worldx(), tile.worldy()) != null;
        } else {
            Building block = tile.build;
            return (block != null && block.block() instanceof UndergroundDrill && block.team == team) ||
                nearestDetector(team, tile.worldx(), tile.worldy()) != null;
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Tile tile = world.tile(x, y);
        var detector = nearestDetector(player.team(), x*8, y*8);
        if (tile == null) return;

        if (detector == null) {
            drawPlaceText(Core.bundle.get("bar.detectorreq"), x, y, valid);
            return;
        }

        countOre(tile);

        if (returnItem != null) {
            float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / getDrillTime(returnItem) * returnCount, 2), x, y, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(returnItem.fullIcon, dx, dy - 1, s, s);
            Draw.reset();
            Draw.rect(returnItem.fullIcon, dx, dy, s, s);
        } else {
            Tile to = tile.getLinkedTilesAs(this, tempTiles).find(t -> getUnderDrop(t.overlay()) != null);
            Item item = to == null ? null : to.drop();
            if(item != null) {
                drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, valid);
            }
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.drillTier);
        stats.add(Stat.drillTier, drillables(drillTime, 0, size * size, null, b -> b instanceof UndergroundOreBlock &&
            getUnderDrop(b) != null && (indexer.isBlockPresent(b) || state.isMenu())));
    }


    public boolean canMine(Tile tile) {
        if(tile == null || tile.block().isStatic()) return false;
        Block drops = getUnderDrop(tile.overlay());
        return drops != null;
    }

    protected void countOre(Tile tile) {
        returnItem = null;
        returnCount = 0;

        oreCount.clear();
        itemArray.clear();

        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(canMine(other) && (other.overlay() instanceof UndergroundOreBlock)) {
                oreCount.increment(getUnderDrop(other.overlay()), 0, 1);
            }
        }

        for(Block item : oreCount.keys()){
            itemArray.add(item);
        }

        itemArray.sort((item1, item2) -> {
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

    // me 2 lazy :P
    public float getDrillTime(Block item) {
        return drillTime;
    }

    public Block getOutput(Tile tile) {
        countOre(tile);

        return returnItem;
    }

    protected Block getUnderDrop(Block b) {
        return b instanceof UndergroundOreBlock u && u.isBlockOre ? u.blockDrop : null;
    }

    protected Ranged nearestDetector(Team team, float wx, float wy) {
        return (Ranged)indexer.findTile(team, wx, wy, 999f, b -> (b.block instanceof OreDetector || b.block instanceof DetectorCoreBlock)
            && Mathf.within(wx, wy, b.x, b.y, ((Ranged)b).range()));
    }

    protected static StatValue drillables(float drillTime, float drillMultiplier, float size, ObjectFloatMap<Item> multipliers, Boolf<Block> filter) {
        return table -> {
            table.row();
            table.table(c -> {
                int i = 0;
                for (Block block : content.blocks()) {
                    if (!(block instanceof UndergroundOreBlock uo) || !filter.get(block)) continue;

                    c.table(Styles.grayPanel, b -> {
                        b.image(uo.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                        b.image(uo.drop.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                        b.table(info -> {
                            info.left();
                            info.add(uo.localizedName).left().row();
                            //info.add(block.itemDrop.emoji()).left(); there's no emoji for modded items.
                        }).grow();
                        if (multipliers != null) {
                            b.add(Strings.autoFixed(60f / (Math.max(drillTime + drillMultiplier * uo.drop.hardness, drillTime) / multipliers.get(uo.itemDrop, 1f)) * size, 2) + StatUnit.perSecond.localized())
                                .right().pad(10f).padRight(15f).color(Color.lightGray);
                        }
                    }).growX().pad(5);
                    if (++i % 2 == 0) c.row();
                }
            }).growX().colspan(table.getColumns());
        };
    }

    public class PayloadDrillBuild extends BlockProducerBuild {
        public float progress;
        public float warmup;
        public float timeDrilled;
        public float lastDrillSpeed;

        public int dominantItems;
        public Block dominantItem;

        @Override
        public Block recipe() {
            return null;
        }

/*
        @Override
        public void updateTile() {
            if(timer(timerDump, dumpTime)){
                payload = new BuildPayload(dominantItem, team);
                payVector.setZero();
                progress %= 1f;
            }

            moveOutPayload();

            if(dominantItem == null){
                return;
            }

            timeDrilled += warmup * delta();

            float delay = getDrillTime(dominantItem);

            if(items.total() < itemCapacity && dominantItems > 0 && efficiency > 0){
                float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;

                lastDrillSpeed = (speed * dominantItems * warmup) / delay;
                warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
                progress += delta() * dominantItems * speed * warmup;

//                if(Mathf.chanceDelta(updateEffectChance * warmup))
//                    updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));
            }else{
                lastDrillSpeed = 0f;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                return;
            }

            if(dominantItems > 0 && progress >= delay && items.total() < itemCapacity){
                offload(dominantItem);

                progress %= delay;

                // Fix drill effect chance being mistaken for update effect chance. TODO: If PR #10440 gets merged and v8 releases, then remove this override
                if(wasVisible && Mathf.chanceDelta(drillEffectChance * warmup)) drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), tileOn().floor().mapColor);
            }
        }
*/

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            dominantItem = getOutput(tile);
        }

/*
        @Override
        public void draw() {
            super.draw();

            //since drawMineItems is false, re-draw this thing
            Draw.color(dominantItem.color);
            Draw.rect(itemRegion, x, y);
            Draw.color();
        }
*/

        @Override
        public float efficiencyScale() {
            Ranged other = nearestDetector(team, x, y);
            return other != null && other.range() >= Mathf.dst(x, y, other.x(), other.y()) ? 1f : 0f;
        }

        @Override
        public void drawSelect() {
            super.drawSelect();

            var d = nearestDetector(team, x, y);
            if (d != null) {
                Drawf.dashLine(team.color, x, y, d.x(), d.y());
            }
        }
    }
}
