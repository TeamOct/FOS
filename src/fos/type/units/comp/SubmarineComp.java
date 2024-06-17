package fos.type.units.comp;

import mindustry.Vars;
import mindustry.entities.EntityCollisions;
import mindustry.gen.*;
import mindustry.world.Tile;

import static ent.anno.Annotations.*;

@EntityComponent
abstract class SubmarineComp implements WaterMovec, Unitc {
    @Import float elevation;
    transient boolean submerged = false;

    public boolean subSolid(int x, int y) {
        Tile tile = Vars.world.tile(x, y);
        return tile == null || tile.solid() || !tile.floor().isDeep();
    }

    @Override
    public EntityCollisions.SolidPred solidity() {
        return submerged ? this::subSolid : EntityCollisions::waterSolid;
    }

    @Override
    @Replace
    public boolean isGrounded() {
        return elevation <= 0.01f && elevation != 0f && !submerged;
    }
}
