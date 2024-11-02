package fos.maps.bases;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.Seq;
import fos.world.blocks.environment.UndergroundOreBlock;
import mindustry.ai.BaseRegistry;
import mindustry.game.*;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.payloads.*;

import static mindustry.Vars.content;

public class UxerdBaseGenerator extends FOSBaseGenerator {
    public UxerdBaseGenerator() {
        schematics = new String[]{
            //core
            "bXNjaAF4nGWS7XKaUBCGF1BE/ASVINFMb4AfvZ5OfyCeJE6NZA44yeX0UrvveU8nnWnM8Li7736KPMm3SEa35s1Idv809lyfmt7UbWdN/V1mZ9O39vI+XLqbiMTX5mSuvYQ/fqayfu762nbtr7q19/7VWHr696Y19clezi9GcnheTTPUL+ZmbDN0VubwDZdb/XGxRpZfOed7O8gD7PZ+MvZyf6s/muu1vjZWS2X/BaRwE5i+u1tNN5+DbVp0+GcO290HnYzJ2Om5s4Om9LrNb/n6i0QC/YSi//qQAIjgjyRMJALHCZQjPAJFSMuJRi6oT3hV6bUxJTG1MbWxCwYyoXbizFASdk0oSpw3kqnvPnX2WJlIECtSYkbMJUBsgTYpB0zZNGW91A84Y9OZM2NlIuFE02eBy4JnCd2curnXzaEDXHDB4MLPuJBYhwInnonn1A27QBaworWWYKSukIiIETEmYmJCJMRUgkSR0poRuvlUCy9xgCVKx2olxBSSFQ4CK6dzIxGwlWik8hT7rFEFWNOZSZQoclobwifsiIJ4IEpiT1TEI3Egjmib8dz533Pja+a+uuAWw2RaLQSOuHjOw+eIiaJALIcE1p54pPNI6wkJG+RFioV7fTay9Fx5bjy3njvVB8qS0Fpj7ekOucWPBWuDX3XrpKGyQIetlFoB5p7eR3qP3uum2eE1BGbEnFgytqaVETnm2bkmkbYARVlgroKLF1y8QC84j7SekPHgMkbKkjjCW/o6JQ9Ysk7JOiUPWLJOyTp7f5E9L7JnncovX3H5yi9fcfmKy1d++QqVRF8AN/uBPQ/seeDsB/Y8QPkH4rpr9Q==",

            //resources
            "bXNjaAF4nE1Oy07DMBCcOo+mpJSKAxyQ+AMf+B7EwXG2rUUaV2tHlL9nF1cCWfZ4ZmdnF3u0FerZnQkvy5V4tINLZGkawnK2F46eUrJv6EdKnsMlhzgDaCc30JRg3j822B9isuL8tJ6XdCJGHzKdbYoLeyrldHGe7MBhPBJ2f8q4+IxH5Sdy2R5pJnY5Mraq5TDbr8CEp98RVBItXTM7r65/2RyXLKMfVPGRSZ4pzt+y7KtcGGAl0DTAsxD9oxJYyatFoBYwxVoXLqZGoOrUWqtqRNAUOUXVukBb4lqNMwIGVSOgHqWSUWFdOte3zq7Q7kY3hW5u9E5pix5GN11JV19GbAvcK3TY6bo/ajhNMQ==",
            "bXNjaAF4nCXOzW6DMAwH8D8E6AcFSg/TpD3BDjnseaYd0uCuaECqBKbt7WfPkcgP29gGPS4GxeJmwvP2Q3GwV5fIpnH6pmjncSH7hnqg5OP4WMewAKgmd6UpIX//2OHpFpINkexAK/k1RJtmN02ox5Vmm8IWPeEsH8Xgv6yPW7pTxEUyd3Kr/aSFouNGtJJLD+d52OZXNBKv42KHOPLETkIvq3yYwvLLf/LKD07IM6YvgRfkyNAiN/zC+QKGo0zolLPSg68CDUzJdDAFw8kCpVCi4nYjtFKrtLZT9rJVyKS251X/GKVQSqVSdspeOShHpVZOSqO0SqeclV446PajUisnpVFaoeJmPn/KJUGC",
        };
        init();
    }

    @Override
    public void generate(Tiles tiles, Seq<Tile> cores, Tile spawn, Team team, Sector sector, float difficulty) {
        this.tiles = tiles;
        this.cores = cores;

        //don't generate bases when there are no loaded schematics
        if(coreParts.isEmpty()) return;

        Mathf.rand.setSeed(sector.id);

        float bracketRange = 0.17f;
        float baseChance = Mathf.lerp(0.7f, 2.1f, difficulty);
        int wallAngle = 70; //180 for full coverage
        double resourceChance = 0.5 * baseChance;
        double nonResourceChance = 0.0005 * baseChance;
        BaseRegistry.BasePart coreschem = coreParts.getFrac(difficulty);
        int passes = difficulty < 0.4 ? 1 : difficulty < 0.8 ? 2 : 3;

        Block wall = getDifficultyWall(1, difficulty), wallLarge = getDifficultyWall(2, difficulty);

        for(Tile tile : cores) {
            tile.clearOverlay();
            Schematics.placeLoadout(coreschem.schematic, tile.x, tile.y, team, true);

            //fill core with every type of item (even non-material)
            Building entity = tile.build;
            for(Item item : content.items()){
                entity.items.add(item, entity.block.itemCapacity);
            }
        }

        for(int i = 0; i < passes; i++) {
            //random schematics
            pass(800, tile -> {
                if (!tile.block().alwaysReplace || Mathf.within(tile.x, tile.y, 350, 350, 100)) return;

                if (((tile.overlay().asFloor().itemDrop != null || tile.overlay() instanceof UndergroundOreBlock || (tile.drop() != null && Mathf.chance(nonResourceChance)))
                    || (tile.floor().liquidDrop != null && Mathf.chance(nonResourceChance * 2))) && Mathf.chance(resourceChance)) {
                    Seq<BaseRegistry.BasePart> parts = forResource(
                        tile.overlay() instanceof UndergroundOreBlock uo ? uo.drop :
                            tile.drop() != null ? tile.drop() : tile.floor().liquidDrop
                    );
                    if (!parts.isEmpty()){
                        tryPlace(parts.getFrac(difficulty + Mathf.range(bracketRange)), tile.x, tile.y, team);
                    }
                } else if (Mathf.chance(nonResourceChance)) {
                    tryPlace(parts.getFrac(difficulty + Mathf.range(bracketRange)), tile.x, tile.y, team);
                }
            });
        }

        //small walls
        pass(tile -> {
            if(tile.block().alwaysReplace){
                boolean any = false;

                for(Point2 p : Geometry.d4){
                    Tile o = tiles.get(tile.x + p.x, tile.y + p.y);

                    //do not block payloads
                    if(o != null && (o.block() instanceof PayloadConveyor || o.block() instanceof PayloadBlock)){
                        return;
                    }
                }

                for(Point2 p : Geometry.d8){
                    if(Angles.angleDist(Angles.angle(p.x, p.y), spawn.angleTo(tile)) > wallAngle){
                        continue;
                    }

                    Tile o = tiles.get(tile.x + p.x, tile.y + p.y);
                    if(o != null && o.team() == team && !(o.block() instanceof Wall)){
                        any = true;
                        break;
                    }
                }

                if(any){
                    tile.setBlock(wall, team);
                }
            }
        });

        //large walls
        pass(curr -> {
            int walls = 0;
            for(int cx = 0; cx < 2; cx++){
                for(int cy = 0; cy < 2; cy++){
                    Tile tile = tiles.get(curr.x + cx, curr.y + cy);
                    if(tile == null || tile.block().size != 1 || (tile.block() != wall && !tile.block().alwaysReplace)) return;

                    if(tile.block() == wall){
                        walls ++;
                    }
                }
            }

            if(walls >= 3){
                curr.setBlock(wallLarge, team);
            }
        });

    }
}
