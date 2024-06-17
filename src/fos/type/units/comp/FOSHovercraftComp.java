package fos.type.units.comp;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.EntityCollisions;
import mindustry.gen.*;
import mindustry.world.Tile;

import static ent.anno.Annotations.*;

@EntityComponent
abstract class FOSHovercraftComp implements Velc, Posc, Flyingc, Hitboxc {
    // FIXME: pathfinding avoids cliff blocks and I have no idea how to fix this
    public boolean nonCliffSolid(int x, int y) {
        Tile tile = Vars.world.tile(x, y);
        return tile == null || (tile.solid() && tile.block() != Blocks.cliff);
    }

    @Override
    @Replace
    public EntityCollisions.SolidPred solidity() {
        return this::nonCliffSolid;
    }
}
