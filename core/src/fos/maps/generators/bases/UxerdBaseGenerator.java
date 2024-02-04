package fos.maps.generators.bases;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.Seq;
import fos.type.blocks.environment.UndergroundOreBlock;
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
            "bXNjaAF4nGWT3XKbQAyFxY8xNgYDNjYmdqYvwEWfp9MLjDcxU2IyC0zyOH3USnu2k840yfB5paMjIW/omb555D+aN0XZ/Kn0rb42o6rbQav6O0U3Nba6e5+64UFEQd9cVT+S++NnROnLMNZ6aH/VrZ7Hu9KIjO9Nq+qr7m6vinKJ3FUz1a/qoXQzDZo2Epu6R/3RaUXJV81tbic6yrmdr0p381v90fR93TearbL/EnQwE6hxmDWXq89JN610+GcOPcwTT4ZieaeXQU9cMlIhob6b7mLXjKN6u/asJPpNXz8ekcO/LvGfKwGBJ3GP3BBchJLw5eEwXJyMyDdJfkqUlVYbQBJAG0AbmKRDS2iX5uhSiK4hRKGJerSy3VfmvGCGYrSiNRABG+Ri8VhjwDWaruG3tgNGaBqZY8BkLLk8cggjRpSIbgPdxuo2ZGGSMZKxnTGmwHJpGVquTFUsVYKtTBlTKh4JezgCD/CBBTkOIyDHY6zMohKe3gkZETQbclbslZAT8CMX74R2gi2ZxltT6DMjICVPkAE5eSK1FXupSG1FKhUhIxdpyhqDPVAAB+AIlMAJqIAn4AxcpGGG2TLTackN7MolFNvU1qayv99GJuP5jJJcwYXcgPOhfK87SozMRPbyKefRWJ2LWk4nBJ8QvCD4LL47DLOjneXesjAb34mDgGsW7G2u3l425jEKc1/30kyOpT2egCdELzZq2hW4rgWuayHXVRADCZACGZCLTWF6edypMP8eB+np8MO84AEveJCWErwg+CwVR1PhM83mjpL0+DN8SiyqhE8JnxKLKuFTwudkN3LCRk7wqewOKuygsjuosIMKO6jsDipxIr4LZvYzep7R84zZz+h5FuUfjfhz6Q==",
            //defences
            "bXNjaAF4nD2OTU7DMBCFX/6clEDTVixZsvWC8yAWjjOlEUlc2bHKdVhzN86AJ4OwZX1v3oz9jAaHAuViZsJj/CQ/6N4E0gOdabGkX9AOFKwfr+voFgBqMj1NAdXrz9f3m8L+7IIOV5Nmh2hXHLm2sSc/xlnfzDThwJZ39kNbH8OFPHbsGO/dDSeWFzKrfqeFvFmdRzuuNOvgoreUIp/TQYFtlSIqgRLUggZFhoxVhn+ROnmCEjRs5fJewYKrvaD7G5eQmkdKPIi59Up5sZSqkqrioDxBCRoxO/nePXLO3hKUmHXaGaMR7AR3glaQ7lUJXQp5wi9RbTcZ",
            //unit production
            "bXNjaAF4nD2Oy07EMAxFbx59SMOCQWI28w1Z8D2IRZoxNCJtUR6a+XuImwpb8U2Or61gxKigV7sQzuVB8WYmm8iU1WfzhtONkov+J/ttBdAHO1FIkO8fA86fWzKuTBR9WczdhoAXRjPZbL5opWjzFvHMLG7u27hY0kwRT0yyX83dR8Jl71PaSnRk6JGjdTz3yjz4PPN2mxItU6jDJ59pMc1df3StBwp76CZdk56L4J6oqdvrgAMg96xtiX5kKhtVbFU1G1VMj9W6rpYCDevDoJuha6VHJ35rCH4M/99Au7OIJrKJYsOAoY5f8AfFtUPx",
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
