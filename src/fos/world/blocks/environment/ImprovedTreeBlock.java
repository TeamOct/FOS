package fos.world.blocks.environment;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.TreeBlock;

public class ImprovedTreeBlock extends TreeBlock {
    public boolean secondLayer = true;

    public ImprovedTreeBlock(String name) {
        super(name);
    }

    @Override
    public void drawBase(Tile tile) {
        float
            x = tile.worldx(), y = tile.worldy(),
            rot = Mathf.randomSeed(tile.pos(), 0, 4) * 90 + Mathf.sin(Time.time + x, 50f, 0.5f) + Mathf.sin(Time.time - y, 65f, 0.9f) + Mathf.sin(Time.time + y - x, 85f, 0.9f),
            rot2 = Mathf.randomSeed(tile.pos() + 1, 0, 4) * 90 + Mathf.sin(Time.time + x, 50f, 0.5f) + Mathf.sin(Time.time - y, 65f, 0.9f) + Mathf.sin(Time.time + y - x, 85f, 0.9f),
            w = region.width * region.scl(), h = region.height * region.scl(),
            scl = 30f, mag = 0.2f,
            treeScl = Mathf.randomSeed(tile.pos(), 0.75f, 1.25f);

        TextureRegion shad = variants == 0 ? customShadowRegion : variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))];

        if (rot == rot2) rot2 += Mathf.randomSeed(tile.pos(), 20, 80);

        Draw.scl(treeScl);

        if (shad.found()) {
            Draw.z(Layer.power - 1);

            Draw.rect(shad, tile.worldx() + shadowOffset, tile.worldy() + shadowOffset, rot);
            if (secondLayer) {
                Draw.z(Layer.power + 1.01f);
                Draw.alpha(0.7f);
                Draw.rect(shad, tile.worldx() + shadowOffset * 2, tile.worldy() + shadowOffset * 2, rot2);
                Draw.alpha(1f);
            }
        }

        TextureRegion reg = variants == 0 ? region : variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))];

        Draw.z(Layer.power + 1);
        Draw.rectv(reg, x, y, w, h, rot, vec -> vec.add(
            Mathf.sin(vec.y*3 + Time.time, scl, mag) + Mathf.sin(vec.x*3 - Time.time, 70, 0.8f),
            Mathf.cos(vec.x*3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50, 0.2f)
        ));
        if (secondLayer) {
            Draw.color(Color.valueOf("00000060"));
            Draw.rectv(reg, x, y, w, h, rot, vec -> vec.add(
                Mathf.sin(vec.y*3 + Time.time, scl, mag) + Mathf.sin(vec.x*3 - Time.time, 70, 0.8f),
                Mathf.cos(vec.x*3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50, 0.2f)
            ));
        }

        Draw.reset();
        Draw.scl(treeScl);

        if (secondLayer) {
            Draw.z(Layer.power + 1.1f);

            Draw.rectv(reg, x, y, w, h, rot2, vec -> vec.add(
                Mathf.sin(vec.y*3 + Time.time*1.5f, scl, mag) + Mathf.sin(vec.x*3 - Time.time, 70, 0.8f),
                Mathf.cos(vec.x*3 + Time.time*1.5f + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50, 0.2f)
            ));
        }

        // This makes the tree impassable for legged units, effectively acting as a roadblock.
        tile.data = 2;
    }
}
