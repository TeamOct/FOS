package fos.type.units.constructors;

import arc.util.io.*;
import fos.core.FOSVars;
import mindustry.entities.EntityCollisions;
import mindustry.game.Team;
import mindustry.gen.UnitWaterMove;
import mindustry.world.Tile;

import static mindustry.Vars.world;

/**
 * A unit constructor with a certainly cursed code.
 * Used for submarines.
 * @author Slotterleet
 */
public class SubmarineUnit extends UnitWaterMove {
    /** Submerged state of a sub. */
    public boolean submerged = false;

    @Override
    public int classId() {
        return FOSVars.subEntity;
    }

    /**
     * A check for water depth.
     * @return Whether a tile is solid or shallow
     */
    public boolean subSolid(int x, int y) {
        Tile tile = world.tile(x, y);
        return tile == null || tile.solid() || !tile.floor().isDeep();
    }

    @Override
    public EntityCollisions.SolidPred solidity() {
        return submerged ? this::subSolid : EntityCollisions::waterSolid;
    }

    @Override
    public boolean isGrounded() {
        return elevation <= 0.01f && elevation != 0f && !submerged;
    }

    //TODO: how am I supposed to check by a freaking team????
    @Override
    public boolean targetable(Team targeter) {
        return super.targetable(targeter);
    }


    @Override
    public void write(Writes write) {
        super.write(write);
        write.bool(submerged);
    }

    @Override
    public void read(Reads read) {
        super.read(read);
        submerged = read.bool();
    }
}
