package fos.type.units.comp;

import mindustry.Vars;
import mindustry.annotations.Annotations;
import mindustry.entities.EntityCollisions;
import mindustry.gen.Unitc;
import mindustry.gen.WaterMovec;
import mindustry.world.Tile;

@Annotations.Component
public abstract class SubmarineComp implements WaterMovec, Unitc {
    @Annotations.Import float elevation;
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
    @Annotations.Replace
    public boolean isGrounded() {
        return elevation <= 0.01f && elevation != 0f && !submerged;
    }
}
