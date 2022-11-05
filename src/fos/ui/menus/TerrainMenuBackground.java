package fos.ui.menus;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import mindustry.content.Blocks;
import mindustry.world.*;

import static mindustry.Vars.*;

public class TerrainMenuBackground extends MenuBackground {
    protected int width, height, seed;
    private FrameBuffer shadows;
    private CacheBatch batch;
    private int cacheFloor, cacheWall;
    private final Camera camera = new Camera();
    private final Mat mat = new Mat();

    @Override
    public void generateWorld(int width, int height) {
        this.width = width;
        this.height = height;
        seed = Mathf.rand.nextInt();

        world.beginMapLoad();

        world.tiles = new Tiles(width, height);

        generate(world.tiles);

        world.endMapLoad();
        cache();
    }

    private void cache() {
        //draw shadows
        shadows = new FrameBuffer(width, height);

        Draw.proj().setOrtho(0, 0, shadows.getWidth(), shadows.getHeight());
        shadows.begin(Color.clear);
        Draw.color(Color.black);

        for(Tile tile : world.tiles){
            if(tile.block() != Blocks.air){
                Fill.rect(tile.x + 0.5f, tile.y + 0.5f, 1, 1);
            }
        }

        Draw.color();
        shadows.end();

        Batch prev = Core.batch;

        Core.batch = batch = new CacheBatch(new SpriteCache(width * height * 6, false));
        batch.beginCache();

        for(Tile tile : world.tiles){
            tile.floor().drawBase(tile);
        }

        for(Tile tile : world.tiles){
            tile.overlay().drawBase(tile);
        }

        cacheFloor = batch.endCache();
        batch.beginCache();

        for(Tile tile : world.tiles){
            tile.block().drawBase(tile);
        }

        cacheWall = batch.endCache();

        Core.batch = prev;
    }

    protected void setTile(int x, int y, Block floor, Block block, Block overlay, Tiles tiles) {
        Tile tile;
        tiles.set(x, y, (tile = new CachedTile()));
        tile.x = (short) x;
        tile.y = (short) y;
        tile.setFloor(floor.asFloor());
        tile.setBlock(block);
        tile.setOverlay(overlay);
    }

    @Override
    public void render() {
        float scaling = Math.max(Scl.scl(4f), Math.max(Core.graphics.getWidth() / ((width - 1f) * tilesize), Core.graphics.getHeight() / ((height - 1f) * tilesize)));
        camera.position.set(width * tilesize / 2f, height * tilesize / 2f);
        camera.resize(Core.graphics.getWidth() / scaling,
            Core.graphics.getHeight() / scaling);

        mat.set(Draw.proj());
        Draw.flush();
        Draw.proj(camera);
        batch.setProjection(camera.mat);
        batch.beginDraw();
        batch.drawCache(cacheFloor);
        batch.endDraw();
        Draw.color();
        Draw.rect(Draw.wrap(shadows.getTexture()),
            width * tilesize / 2f - 4f, height * tilesize / 2f - 4f,
            width * tilesize, -height * tilesize);
        Draw.flush();
        batch.beginDraw();
        batch.drawCache(cacheWall);
        batch.endDraw();

        Draw.proj(mat);
        Draw.color(0f, 0f, 0f, 0.3f);
        Fill.crect(0, 0, Core.graphics.getWidth(), Core.graphics.getHeight());
        Draw.color();
    }
}
