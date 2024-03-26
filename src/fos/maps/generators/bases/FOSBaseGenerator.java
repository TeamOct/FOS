package fos.maps.generators.bases;

import arc.func.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.*;
import arc.util.*;
import fos.content.FOSBlocks;
import fos.type.blocks.environment.UndergroundOreBlock;
import mindustry.ai.BaseRegistry;
import mindustry.ctype.Content;
import mindustry.game.*;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BuildVisibility;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.deepwater;

/**
 * Enemy base generator class for modded planets.
 * @author Slotterleet
 */
public abstract class FOSBaseGenerator {
    /** Schematics encoded in Base64. */
    public String[] schematics;
    /** Base parts registered for use. */
    public Seq<BaseRegistry.BasePart> coreParts = new Seq<>(), parts = new Seq<>();
    /** Required base parts. */
    public ObjectMap<Content, Seq<BaseRegistry.BasePart>> reqParts = new ObjectMap<>();
    /** Modded underground ores for use in base generators. */
    public static ObjectMap<Item, Block> uores = new ObjectMap<>();

    protected Tiles tiles;
    protected Seq<Tile> cores;
    protected static final Vec2 axis = new Vec2(), rotator = new Vec2();

    /** Initializes this base generator. */
    protected void init() {
        for (Block b : content.blocks()) {
            if (b instanceof UndergroundOreBlock u) {
                uores.put(u.drop, u);
            }
        }

        for(String str : schematics) {
            Schematic schem = Schematics.readBase64(str);

            BaseRegistry.BasePart part = new BaseRegistry.BasePart(schem);
            Tmp.v1.setZero();
            int drills = 0;

            for(Schematic.Stile tile : schem.tiles) {
                //keep track of core type
                if (tile.block instanceof CoreBlock && tile.block != FOSBlocks.coreColony) {
                    part.core = tile.block;
                }

                //save the required resource based on item source - multiple sources are not allowed
                if (tile.block instanceof ItemSource) {
                    Item config = (Item)tile.config;
                    if(config != null) part.required = config;
                }

                //same for liquids - this is not used yet
                if (tile.block instanceof LiquidSource) {
                    Liquid config = (Liquid)tile.config;
                    if(config != null) part.required = config;
                }

                //calculate averages
                if (tile.block instanceof Drill || tile.block instanceof Pump) {
                    Tmp.v1.add(tile.x*tilesize + tile.block.offset, tile.y*tilesize + tile.block.offset);
                    drills ++;
                }
            }
            schem.tiles.removeAll(s -> s.block.buildVisibility == BuildVisibility.sandboxOnly);

            part.tier = schem.tiles.sumf(s -> Mathf.pow(s.block.buildCost / s.block.buildCostMultiplier, 1.4f));

            if(part.core != null) {
                coreParts.add(part);
            } else if (part.required == null) {
                parts.add(part);
            }

            if (drills > 0) {
                Tmp.v1.scl(1f / drills).scl(1f / tilesize);
                part.centerX = (int)Tmp.v1.x;
                part.centerY = (int)Tmp.v1.y;
            } else {
                part.centerX = part.schematic.width/2;
                part.centerY = part.schematic.height/2;
            }

            if (part.required != null && part.core == null) {
                reqParts.get(part.required, Seq::new).add(part);
            }

            coreParts.sort(b -> b.tier);
            parts.sort();
            reqParts.each((key, arr) -> arr.sort());
        }
    }

    /**
     * @param size Wall size
     * @param difficulty Sector difficulty
     * @return A wall block to cover structures
     */
    public static Block getDifficultyWall(int size, float difficulty) {
        Seq<Block> wallsSmall = content.blocks().select(b -> b instanceof Wall && b.size == size
            && !b.insulated && b.buildVisibility == BuildVisibility.shown
            && !(b instanceof Door)
            && !(Structs.contains(b.requirements, i -> state.rules.hiddenBuildItems.contains(i.item)))
            && b.minfo.mod != null && b.minfo.mod.name.equals("fos"));
        wallsSmall.sort(b -> b.buildCost);
        return wallsSmall.getFrac(difficulty * 0.91f);
    }

    /**
     * Processes every tile in a 160 world-unit radius surrounding a tile declared in the {@code cons} lambda.
     * @param cons A lambda with a {@code Tile} parameter
     */
    protected void pass(Cons<Tile> cons) {
        pass(160, cons);
    }

    /**
     * Processes every tile surrounding a tile declared in the {@code cons} lambda.
     * @param cons A lambda with a {@code Tile} parameter
     * @param radius Radius in world units (8 units = 1 tile)
     */
    protected void pass(int radius, Cons<Tile> cons) {
        Tile core = cores.first();
        core.circle(radius, (x, y) -> cons.get(tiles.getn(x, y)));
    }

    /**
     * Generates the base on the entire sector.
     * @param tiles Tilemap to generate on
     * @param cores Core tiles
     * @param spawn Enemy spawn tile
     * @param team A team to generate the base for
     * @param sector A sector to generate on
     * @param difficulty Base difficulty
     */
    public void generate(Tiles tiles, Seq<Tile> cores, Tile spawn, Team team, Sector sector, float difficulty) {

    }


    /**
     * Attempts to place a base part in given coordinates.
     * @param part A base part to be placed
     * @param x x-coordinate
     * @param y y-coordinate
     * @param team A team that the base part belongs to
     */
    public static void tryPlace(BaseRegistry.BasePart part, int x, int y, Team team) {
        tryPlace(part, x, y, team, null);
    }

    /**
     * Attempts to place a base part in given coordinates.
     * @param part A base part to be placed
     * @param x x-coordinate
     * @param y y-coordinate
     * @param team A team that the base part belongs to
     * @param posc A lambda that... does something???
     */
    public static void tryPlace(BaseRegistry.BasePart part, int x, int y, Team team, @Nullable Intc2 posc) {
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

            // java sucks
            final boolean[] invalidTiles = {false};

            tile.block.iterateTaken(realX, realY, (ex, ey) -> {
                var t = world.tile(ex, ey);
                if (t.dangerous() || !t.floor().hasSurface() || t.floor() == deepwater)
                    invalidTiles[0] = true;
            });

            //do not place a schematic if an invalid tile is found.
            if (invalidTiles[0]) return;
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

    protected static void set(Tile tile, Item item) {
        if (uores.containsKey(item)) {
            tile.setOverlay(uores.get(item));
        } else if (bases.ores.containsKey(item)){
            tile.setOverlay(bases.ores.get(item));
        } else if (bases.oreFloors.containsKey(item)) {
            tile.setFloor(bases.oreFloors.get(item));
        }
    }

    protected static boolean isTaken(Block block, int x, int y) {
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

    protected static boolean overlaps(int x, int y) {
        Tile tile = world.tiles.get(x, y);

        return tile == null || !tile.block().alwaysReplace || world.getDarkness(x, y) > 0;
    }

    public Seq<BaseRegistry.BasePart> forResource(Content item) {
        return reqParts.get(item, Seq::new);
    }
}
