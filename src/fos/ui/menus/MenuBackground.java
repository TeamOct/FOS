package fos.ui.menus;

import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.CachedTile;
import mindustry.world.Tile;
import mindustry.world.Tiles;

import static mindustry.Vars.*;

// Original code from Project Unity
// Author: @Goobrr
public class MenuBackground {
    protected int width, height, seed;

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

    protected void generate(Tiles tiles) {
        for (int x = 0; x < tiles.width; x++) {
            for (int y = 0; y < tiles.height; y++) {
                Block floor = Blocks.air;
                Block block = Blocks.empty;
                Block overlay = Blocks.air;
                setTile(x, y, floor, block, overlay, tiles);
            }
        }
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

    protected void cache() {

    }

    public void render() {

    }
}
