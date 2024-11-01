package fos.ai;

import arc.*;
import arc.func.Prov;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import fos.content.FOSTeams;
import fos.core.*;
import fos.mod.FOSEventTypes;
import mindustry.content.Blocks;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.*;
import static mindustry.ai.Pathfinder.PathCost;

public class FOSPathfinder implements Runnable{
    private static final long maxUpdate = Time.millisToNanos(8);
    private static final int updateFPS = 60;
    private static final int updateInterval = 1000 / updateFPS;

    /** cached world size */
    static int wwidth, wheight;

    static final int impassable = -1;

    /** tile data, see PathTileStruct - kept as a separate array for threading reasons */
    static int[] tiles = new int[0];

    public static final int
        fieldBug = 0,
        fieldBurrowing = 1;
    public static final Seq<Prov<FOSFlowfield>> fieldTypes = Seq.with(
        () -> new FlagTargetsField(BlockFlag.generator, BlockFlag.drill, BlockFlag.factory, BlockFlag.core, null),
        () -> new FlagTargetsField(BlockFlag.unitCargoUnloadPoint, BlockFlag.core, null)
    );

    public static final int
        costBugLegs = 0,
        costBurrowing = 1;
    public static final Seq<PathCost> costTypes = Seq.with(
        // ground bugs
        (team, tile) -> {
            int data = tiles[tile];
            if (PathTile.legSolid(data)) return -1;
            return 1 + (PathTile.deep(data) ? 6000 : 0) +
                (PathTile.solid(data) ? 5 : 0) +
                FOSVars.deathMapController.deathMap[tile]; //take into account recent unit deaths
        },

        // burrowing bugs, ignore death map values
        (team, tile) -> {
            int data = tiles[tile];
            if (PathTile.legSolid(data)) return -1;
            return 1 + (PathTile.deep(data) ? 6000 : 0) +
                (PathTile.solid(data) ? 5 : 0);
        }
    );

    /** maps team, cost, type to flow field*/
    FOSFlowfield[][][] cache;
    /** unordered array of path data for iteration only. DO NOT iterate or access this in the main thread. */
    Seq<FOSFlowfield> threadList = new Seq<>(), mainList = new Seq<>();
    /** handles task scheduling on the update thread. */
    TaskQueue queue = new TaskQueue();
    /** Current pathfinding thread */
    @Nullable Thread thread;
    IntSeq tmpArray = new IntSeq();

    public FOSPathfinder() {
        clearCache();

        Events.on(EventType.WorldLoadEvent.class, event -> {
            stop();

            //reset and update internal tile array
            tiles = new int[world.width() * world.height()];
            wwidth = world.width();
            wheight = world.height();
            threadList = new Seq<>();
            mainList = new Seq<>();
            clearCache();

            for(int i = 0; i < tiles.length; i++){
                Tile tile = world.tiles.geti(i);
                tiles[i] = packTile(tile);
            }

            //don't bother setting up paths unless necessary
            if(state.rules.waveTeam == FOSTeams.bessin && !net.client()){
                preloadPath(getField(state.rules.waveTeam, costBugLegs, fieldBug));
                preloadPath(getField(state.rules.waveTeam, costBurrowing, fieldBurrowing));
                Log.debug("[FOS] Preloading ground bug flowfield.");
            }

            start();
        });

        Events.on(EventType.ResetEvent.class, event -> stop());

        Events.on(EventType.TileChangeEvent.class, event -> updateTile(event.tile));
        Events.on(FOSEventTypes.InsectDeathEvent.class, event -> updateTile(event.tile));

        //remove nearSolid flag for tiles
        Events.on(EventType.TilePreChangeEvent.class, event -> {
            Tile tile = event.tile;

            if(tile.solid()){
                for(int i = 0; i < 4; i++){
                    Tile other = tile.nearby(i);
                    if(other != null){
                        //other tile needs to update its nearSolid to be false if it's not solid and this tile just got un-solidified
                        if(!other.solid()){
                            boolean otherNearSolid = false;
                            for(int j = 0; j < 4; j++){
                                Tile othernear = other.nearby(i);
                                if(othernear != null && othernear.solid()){
                                    otherNearSolid = true;
                                    break;
                                }
                            }
                            int arr = other.array();
                            //the other tile is no longer near solid, remove the solid bit
                            if(!otherNearSolid && tiles.length > arr){
                                tiles[arr] &= ~(PathTile.bitMaskNearSolid);
                            }
                        }
                    }
                }
            }
        });
    }

    private void clearCache(){
        cache = new FOSFlowfield[256][5][5];
    }

    /** Packs a tile into its internal representation. */
    public int packTile(Tile tile){
        boolean nearLiquid = false, nearSolid = false, nearLegSolid = false, nearGround = false, solid = tile.solid(), allDeep = tile.floor().isDeep();

        for(int i = 0; i < 4; i++){
            Tile other = tile.nearby(i);
            if(other != null){
                Floor floor = other.floor();
                boolean osolid = other.solid();
                boolean olsolid = other.legSolid();
                if(floor.isLiquid) nearLiquid = true;
                //TODO potentially strange behavior when teamPassable is false for other teams?
                if(osolid && !other.block().teamPassable) nearSolid = true;
                if(olsolid && !other.block().teamPassable) nearLegSolid = true;
                if(!floor.isLiquid) nearGround = true;
                if(!floor.isDeep()) allDeep = false;

                //other tile is now near solid
                if(solid && !tile.block().teamPassable){
                    tiles[other.array()] |= PathTile.bitMaskNearSolid;
                }
            }
        }

        int tid = tile.getTeamID();

        return PathTile.get(
            tile.build == null || !solid || tile.block() instanceof CoreBlock ? 0 : Math.min((int)(tile.build.health / 40), 80),
            tid == 0 && tile.build != null && state.rules.coreCapture ? 255 : tid, //use teamid = 255 when core capture is enabled to mark out derelict structures
            solid,
            tile.floor().isLiquid,
            tile.staticDarkness() >= 2 || (tile.floor().solid && tile.block() == Blocks.air),
            nearLiquid,
            nearGround,
            nearSolid,
            nearLegSolid,
            tile.floor().isDeep(),
            tile.floor().damageTaken > 0.00001f,
            allDeep,
            tile.block().teamPassable
        );
    }

    public int get(int x, int y){
        return x + y * wwidth;
    }

    /** Starts or restarts the pathfinding thread. */
    private void start(){
        stop();
        if(net.client()) return;

        thread = new Thread(this, "FOS Pathfinder");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
        thread.start();
    }

    /** Stops the pathfinding thread. */
    private void stop(){
        if(thread != null){
            thread.interrupt();
            thread = null;
        }
        queue.clear();
    }

    /** Update a tile in the internal pathfinding grid.
     * Causes a complete pathfinding recalculation. Main thread only. */
    public void updateTile(Tile tile){
        if(net.client()) return;

        tile.getLinkedTiles(t -> {
            int pos = t.array();
            if(pos < tiles.length){
                tiles[pos] = packTile(t);
            }
        });

        //can't iterate through array so use the map, which should not lead to problems
        for(FOSFlowfield path : mainList){
            if(path != null){
                synchronized(path.targets){
                    path.updateTargetPositions();
                }
            }
        }

        //mark every flow field as dirty, so it updates when it's done
        queue.post(() -> {
            for(FOSFlowfield data : threadList){
                data.dirty = true;
            }
        });
    }

    /** Thread implementation. */
    @Override
    public void run(){
        while(true){
            if(net.client()) return;
            try{

                if(state.isPlaying()){
                    queue.run();

                    //each update time (not total!) no longer than maxUpdate
                    for(FOSFlowfield data : threadList){

                        //if it's dirty and there is nothing to update, begin updating once more
                        if(data.dirty && data.frontier.size == 0){
                            updateTargets(data);
                            data.dirty = false;
                        }

                        updateFrontier(data, maxUpdate);
                    }
                }

                try{
                    Thread.sleep(updateInterval);
                }catch(InterruptedException e){
                    //stop looping when interrupted externally
                    return;
                }
            }catch(Throwable e){
                e.printStackTrace();
            }
        }
    }

    public FOSFlowfield getField(Team team, int costType, int fieldType){
        if(cache[team.id][costType][fieldType] == null){
            FOSFlowfield field = fieldTypes.get(fieldType).get();
            field.team = team;
            field.cost = costTypes.get(costType);
            field.targets.clear();
            field.getPositions(field.targets);

            cache[team.id][costType][fieldType] = field;
            queue.post(() -> registerPath(field));
        }
        return cache[team.id][costType][fieldType];
    }

    /** Gets next tile to travel to. Main thread only. */
    public @Nullable Tile getTargetTile(Tile tile, FOSFlowfield path){
        if(tile == null) return null;

        //uninitialized flowfields are not applicable
        if(!path.initialized){
            return tile;
        }

        //if refresh rate is positive, queue a refresh
        if(path.refreshRate > 0 && Time.timeSinceMillis(path.lastUpdateTime) > path.refreshRate){
            path.lastUpdateTime = Time.millis();

            tmpArray.clear();
            path.getPositions(tmpArray);

            synchronized(path.targets){
                //make sure the position actually changed
                if(!(path.targets.size == 1 && tmpArray.size == 1 && path.targets.first() == tmpArray.first())){
                    path.updateTargetPositions();

                    //queue an update
                    queue.post(() -> updateTargets(path));
                }
            }
        }

        //use complete weights if possible; these contain a complete flow field that is not being updated
        int[] values = path.hasComplete ? path.completeWeights : path.weights;
        int apos = tile.array();
        int value = values[apos];

        Tile current = null;
        int tl = 0;
        for(Point2 point : Geometry.d8){
            int dx = tile.x + point.x, dy = tile.y + point.y;

            Tile other = world.tile(dx, dy);
            if(other == null) continue;

            int packed = world.packArray(dx, dy);

            if(values[packed] < value && (current == null || values[packed] < tl) && path.passable(packed) &&
                !(point.x != 0 && point.y != 0 && (!path.passable(world.packArray(tile.x + point.x, tile.y)) || !path.passable(world.packArray(tile.x, tile.y + point.y))))){ //diagonal corner trap
                current = other;
                tl = values[packed];
            }
        }

        if(current == null || tl == impassable || (path.cost == costTypes.items[costBugLegs] && current.dangerous() && !tile.dangerous())) return tile;

        return current;
    }

    /** Increments the search and sets up flow sources. Does not change the frontier. */
    private void updateTargets(FOSFlowfield path){

        //increment search, but do not clear the frontier
        path.search++;

        synchronized(path.targets){
            //add targets
            for(int i = 0; i < path.targets.size; i++){
                int pos = path.targets.get(i);

                path.weights[pos] = 0;
                path.searches[pos] = path.search;
                path.frontier.addFirst(pos);
            }
        }
    }

    private void preloadPath(FOSFlowfield path){
        path.updateTargetPositions();
        registerPath(path);
        updateFrontier(path, -1);
    }

    /**
     * TODO wrong docs
     * Created a new flowfield that aims to get to a certain target for a certain team.
     * Pathfinding thread only.
     */
    private void registerPath(FOSFlowfield path){
        path.lastUpdateTime = Time.millis();
        path.setup(tiles.length);

        threadList.add(path);

        //add to main thread's list of paths
        Core.app.post(() -> mainList.add(path));

        //fill with impassables by default
        for(int i = 0; i < tiles.length; i++){
            path.weights[i] = impassable;
        }

        //add targets
        for(int i = 0; i < path.targets.size; i++){
            int pos = path.targets.get(i);
            path.weights[pos] = 0;
            path.frontier.addFirst(pos);
        }
    }

    /** Update the frontier for a path. Pathfinding thread only. */
    private void updateFrontier(FOSFlowfield path, long nsToRun){
        boolean hadAny = path.frontier.size > 0;
        long start = Time.nanos();

        int counter = 0;

        while(path.frontier.size > 0){
            int tile = path.frontier.removeLast();
            if(path.weights == null) return; //something went horribly wrong, bail
            int cost = path.weights[tile];

            //pathfinding overflowed for some reason, time to bail. the next block update will handle this, hopefully
            if(path.frontier.size >= world.width() * world.height()){
                path.frontier.clear();
                return;
            }

            if(cost != impassable){
                for(Point2 point : Geometry.d4){

                    int dx = (tile % wwidth) + point.x, dy = (tile / wwidth) + point.y;

                    if(dx < 0 || dy < 0 || dx >= wwidth || dy >= wheight) continue;

                    int newPos = tile + point.x + point.y * wwidth;
                    int otherCost = path.cost.getCost(path.team.id, newPos);

                    if((path.weights[newPos] > cost + otherCost || path.searches[newPos] < path.search) && otherCost != impassable){
                        path.frontier.addFirst(newPos);
                        path.weights[newPos] = cost + otherCost;
                        path.searches[newPos] = (short)path.search;
                    }
                }
            }

            //every N iterations, check the time spent - this prevents extra calls to nano time, which itself is slow
            if(nsToRun >= 0 && (counter++) >= 200){
                counter = 0;
                if(Time.timeSinceNanos(start) >= nsToRun){
                    return;
                }
            }
        }

        //there WERE some things in the frontier, but now they are gone, so the path is done; copy over latest data
        if(hadAny && path.frontier.size == 0){
            System.arraycopy(path.weights, 0, path.completeWeights, 0, path.weights.length);
            path.hasComplete = true;
        }
    }

    public static class FlagTargetsField extends FOSFlowfield {
        public final BlockFlag[] flags;

        public FlagTargetsField(BlockFlag... flags) {
            this.flags = flags;
        }

        @Override
        protected void getPositions(IntSeq out) {
            for (var f : flags) {
                Seq<Building> seq = f != null ? indexer.getEnemy(team, f) : Groups.build.copy();
                if (seq.isEmpty()) continue;

                for (var b : seq) {
                    out.add(b.tile.array());
                }
                break;
            }
        }
    }

    /**
     * Data for a flow field to some set of destinations.
     * Concrete subclasses must specify a way to fetch costs and destinations.
     */
    public static abstract class FOSFlowfield {
        /** Refresh rate in milliseconds. Return any number <= 0 to disable. */
        protected int refreshRate;
        /** Team this path is for. Set before using. */
        protected Team team = Team.derelict;
        /** Function for calculating path cost. Set before using. */
        protected PathCost cost = costTypes.get(costBugLegs);
        /** Whether there are valid weights in the complete array. */
        protected volatile boolean hasComplete;
        /** If true, this flow field needs updating. This flag is only set to false once the flow field finishes and the weights are copied over. */
        protected boolean dirty = false;

        /** costs of getting to a specific tile */
        public int[] weights;
        /** search IDs of each position - the highest, most recent search is prioritized and overwritten */
        public int[] searches;
        /** the last "complete" weights of this tilemap. */
        public int[] completeWeights;

        /** search frontier, these are Pos objects */
        IntQueue frontier = new IntQueue();
        /** all target positions; these positions have a cost of 0, and must be synchronized on! */
        final IntSeq targets = new IntSeq();
        /** current search ID */
        int search = 1;
        /** last updated time */
        long lastUpdateTime;
        /** whether this flow field is ready to be used */
        boolean initialized;

        void setup(int length){
            this.weights = new int[length];
            this.searches = new int[length];
            this.completeWeights = new int[length];
            this.frontier.ensureCapacity((length) / 4);
            this.initialized = true;
        }

        public boolean hasCompleteWeights(){
            return hasComplete && completeWeights != null;
        }

        public void updateTargetPositions(){
            targets.clear();
            getPositions(targets);
        }

        protected boolean passable(int pos){
            int amount = cost.getCost(team.id, pos);
            return amount != impassable;
        }

        /** Gets targets to pathfind towards. This must run on the main thread. */
        protected abstract void getPositions(IntSeq out);
    }
}
