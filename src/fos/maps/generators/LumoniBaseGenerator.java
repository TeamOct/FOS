package fos.maps.generators;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import fos.type.blocks.environment.UndergroundOreBlock;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.ai.BaseRegistry.*;

public class LumoniBaseGenerator {
    public String[] schematics;
    public Seq<BasePart> coreParts = new Seq<>(), parts = new Seq<>();
    public ObjectMap<Content, Seq<BasePart>> reqParts = new ObjectMap<>();
    public static ObjectMap<Item, Block> uores = new ObjectMap<>();

    private Tiles tiles;
    private Seq<Tile> cores;
    private static final Vec2 axis = new Vec2(), rotator = new Vec2();

    public LumoniBaseGenerator() {
        schematics = new String[]{
            //cores
            "bXNjaAF4nF2QsW7DMAxET7YjVLCj1EPRr9CQsd9SdHBipTWg2IGUNL9fnrgVAvxoiiceiRFji26drhHv6XFd1imUnynPcQ7nLcdwDEf0cyznvNzuy7YCsGk6xVTQfH7t8HbZSihL+o05PKeUQpryd8ThXxqvTDyXdQ73Rz4ta4Rj5vZIJWJkWNtdtnzPsRRp8wEYOS3YUuEIA4/WoJHYEJ1iB9MILEA4NEz2Wjko9kQrgXw6ORUDahNPQcc7vmgVTpP1bsc7wjOwtMNuYoPoFD1NWGlkWHKA5F/kVOtV4FTgVOBU4FTgKDCSkgFa8dWhTmAVjnYHtdurpYGVRP3b849DVsGeAiarXc8Z6+K4LK8789wZ0SsGRfXia60Ysgp57A+okDUt",

            //defenses
            "bXNjaAF4nF1QzU7GIBDc/tAWqtGLifEdOHjxZYwHbPn8SGj7Bai+vrtMvJgWhl2W2dkhQ3NH/e42T8/x3MLubL66tPrVrv7i9+ztK82rz0sKtxKOnYiG6D59zNS+fwz0cDmyzSF++2R/XIz0KImbSyUsZ3TlSPT0r8RGl7483Um6hN0yWaH7v2hNgVnmUPxm83GmxXPLN17UUCfQAjpAD1CAATACJoAGGIGGv7apHG3LZ34n0cSLIwN+yfBv0IYj6aWoERg4ZjDo3QhLL3Tsohxkq5U9iyC5qyJ6PFAgU1IpMCJpIL0qE+mttFHCOchEkqwCB1SOaDti6BHJCaoniRSPzBu9dDJ8zWvYpmGbhm0aDBq2adimoVjDNi22/QIzGTiq",
            "bXNjaAF4nDWQXW6DMBCEFwNOHEzz26pSz+CXnqD3qPpAwFEtERMZo16/u4zywuddzw7roZbeSqpid/f0Pi73EDs3/3Zp8IMb/M3H2btPagY/9yk8cpgiEemxu/pxJvX9o+lwm2b36FIO/TJ2eUpkpZNDdCzK1D6rIYVxhHxKnt2z70W+dv5CHFxe0jVET03I/u7maUm95999ERWkqShIcbWiABRQAhXf8tlIVfJRCRRQoVkDGtiwD9+0VGJ4BdsVjErmaoHIt6KraSd3NR1loZpOqM7ABXgVFw0zDTMtLhWjRmWBFngB9sABOAJnWUKzJ2Oz7sabrHMblijFOAEXWWkrAQgUsL5hK8qCU1mjM4jOIDqD6AyUBp4GngaeO4w3T5yl2fBK/BorH/rgvsW1RSYWmVhkYiWTf2phOYk="
        };
        for (Block b : content.blocks()) {
            if (b instanceof UndergroundOreBlock u) {
                uores.put(u.drop, u);
            }
        }

        for(String str : schematics){
            Schematic schem = Schematics.readBase64(str);

            BasePart part = new BasePart(schem);
            Tmp.v1.setZero();
            int drills = 0;

            for(Schematic.Stile tile : schem.tiles){
                //keep track of core type
                if(tile.block instanceof CoreBlock){
                    part.core = tile.block;
                }

                //save the required resource based on item source - multiple sources are not allowed
                if(tile.block instanceof ItemSource){
                    Item config = (Item)tile.config;
                    if(config != null) part.required = config;
                }

                //same for liquids - this is not used yet
                if(tile.block instanceof LiquidSource){
                    Liquid config = (Liquid)tile.config;
                    if(config != null) part.required = config;
                }

                //calculate averages
                if(tile.block instanceof Drill || tile.block instanceof Pump){
                    Tmp.v1.add(tile.x*tilesize + tile.block.offset, tile.y*tilesize + tile.block.offset);
                    drills ++;
                }
            }
            schem.tiles.removeAll(s -> s.block.buildVisibility == BuildVisibility.sandboxOnly);

            part.tier = schem.tiles.sumf(s -> Mathf.pow(s.block.buildCost / s.block.buildCostMultiplier, 1.4f));

            if(part.core != null){
                coreParts.add(part);
            }else if(part.required == null){
                parts.add(part);
            }

            if(drills > 0){
                Tmp.v1.scl(1f / drills).scl(1f / tilesize);
                part.centerX = (int)Tmp.v1.x;
                part.centerY = (int)Tmp.v1.y;
            }else{
                part.centerX = part.schematic.width/2;
                part.centerY = part.schematic.height/2;
            }

            if(part.required != null && part.core == null){
                reqParts.get(part.required, Seq::new).add(part);
            }

            coreParts.sort(b -> b.tier);
            parts.sort();
            reqParts.each((key, arr) -> arr.sort());
        }
    }

    public static Block getDifficultyWall(int size, float difficulty){
        Seq<Block> wallsSmall = content.blocks().select(b -> b instanceof Wall && b.size == size
            && !b.insulated && b.buildVisibility == BuildVisibility.shown
            && !(b instanceof Door)
            && !(Structs.contains(b.requirements, i -> state.rules.hiddenBuildItems.contains(i.item)))
            && b.minfo.mod.name.equals("fos"));
        wallsSmall.sort(b -> b.buildCost);
        return wallsSmall.getFrac(difficulty * 0.91f);
    }

    public void generate(Tiles tiles, Seq<Tile> cores, Tile spawn, Team team, Sector sector, float difficulty){
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
        BasePart coreschem = coreParts.getFrac(difficulty);
        int passes = difficulty < 0.4 ? 1 : difficulty < 0.8 ? 2 : 3;

        Block wall = getDifficultyWall(1, difficulty), wallLarge = getDifficultyWall(2, difficulty);

        for(Tile tile : cores){
            tile.clearOverlay();
            Schematics.placeLoadout(coreschem.schematic, tile.x, tile.y, team, false);

            //fill core with every type of item (even non-material)
            Building entity = tile.build;
            for(Item item : content.items()){
                entity.items.add(item, entity.block.itemCapacity);
            }
        }

        for(int i = 0; i < passes; i++){
            //random schematics
            pass(tile -> {
                if(!tile.block().alwaysReplace) return;

                if(((tile.overlay().asFloor().itemDrop != null || tile.overlay() instanceof UndergroundOreBlock || (tile.drop() != null && Mathf.chance(nonResourceChance)))
                    || (tile.floor().liquidDrop != null && Mathf.chance(nonResourceChance * 2))) && Mathf.chance(resourceChance)){
                    Seq<BasePart> parts = forResource(
                        tile.overlay() instanceof UndergroundOreBlock uo ? uo.drop :
                        tile.drop() != null ? tile.drop() : tile.floor().liquidDrop
                    );
                    if(!parts.isEmpty()){
                        tryPlace(parts.getFrac(difficulty + Mathf.range(bracketRange)), tile.x, tile.y, team);
                    }
                }else if(Mathf.chance(nonResourceChance)){
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

        //clear path for ground units
        for(Tile tile : cores){
            Astar.pathfind(tile, spawn, t -> t.team() == state.rules.waveTeam && !t.within(tile, 25f * 8) ? 100000 : t.floor().hasSurface() ? 1 : 10, t -> !t.block().isStatic()).each(t -> {
                if(!t.within(tile, 25f * 8)){
                    if(t.team() == state.rules.waveTeam){
                        t.setBlock(Blocks.air);
                    }

                    for(Point2 p : Geometry.d8){
                        Tile other = t.nearby(p);
                        if(other != null && other.team() == state.rules.waveTeam){
                            other.setBlock(Blocks.air);
                        }
                    }
                }
            });
        }
    }

    void pass(Cons<Tile> cons){
        Tile core = cores.first();
        core.circle(160, (x, y) -> cons.get(tiles.getn(x, y)));
    }

    public static void tryPlace(BasePart part, int x, int y, Team team){
        tryPlace(part, x, y, team, null);
    }

    public static void tryPlace(BasePart part, int x, int y, Team team, @Nullable Intc2 posc){
        if (part == null) return;

        int rotation = Mathf.range(2);
        axis.set((int)(part.schematic.width / 2f), (int)(part.schematic.height / 2f));
        Schematic result = Schematics.rotate(part.schematic, rotation);
        int rotdeg = rotation*90;

        rotator.set(part.centerX, part.centerY).rotateAround(axis, rotdeg);
        //bottom left schematic corner
        int cx = x - (int)rotator.x;
        int cy = y - (int)rotator.y;

        for(Schematic.Stile tile : result.tiles){
            int realX = tile.x + cx, realY = tile.y + cy;
            if(isTaken(tile.block, realX, realY)){
                return;
            }

            if(posc != null){
                posc.get(realX, realY);
            }
        }

        if(part.required instanceof Item item){
            for(Schematic.Stile tile : result.tiles){
                if(tile.block instanceof Drill){

                    tile.block.iterateTaken(tile.x + cx, tile.y + cy, (ex, ey) -> {

                        if(world.tiles.getn(ex, ey).floor().hasSurface()){
                            set(world.tiles.getn(ex, ey), item);
                        }

                        Tile rand = world.tiles.getc(ex + Mathf.range(1), ey + Mathf.range(1));
                        if(rand.floor().hasSurface()){
                            //random ores nearby to make it look more natural
                            set(rand, item);
                        }
                    });
                }
            }
        }

        Schematics.place(result, cx + result.width/2, cy + result.height/2, team);

        //fill drills with items after placing
        if(part.required instanceof Item item){
            for(Schematic.Stile tile : result.tiles){
                if(tile.block instanceof Drill){

                    Building build = world.tile(tile.x + cx, tile.y + cy).build;

                    if(build != null){
                        build.items.add(item, build.block.itemCapacity);
                    }
                }
            }
        }

    }

    static void set(Tile tile, Item item){
        if (uores.containsKey(item)) {
            tile.setOverlay(uores.get(item));
        } else if (bases.ores.containsKey(item)){
            tile.setOverlay(bases.ores.get(item));
        } else if (bases.oreFloors.containsKey(item)) {
            tile.setFloor(bases.oreFloors.get(item));
        }
    }

    static boolean isTaken(Block block, int x, int y){
        int offsetx = -(block.size - 1) / 2;
        int offsety = -(block.size - 1) / 2;
        int pad = 1;

        for(int dx = -pad; dx < block.size + pad; dx++){
            for(int dy = -pad; dy < block.size + pad; dy++){
                if(overlaps(dx + offsetx + x, dy + offsety + y)){
                    return true;
                }
            }
        }

        return false;
    }

    static boolean overlaps(int x, int y){
        Tile tile = world.tiles.get(x, y);

        return tile == null || !tile.block().alwaysReplace || world.getDarkness(x, y) > 0;
    }

    public Seq<BasePart> forResource(Content item){
        return reqParts.get(item, Seq::new);
    }
}
