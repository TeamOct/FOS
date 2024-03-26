package fos.type.abilities;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Unitc;

/** Use this interface for better data i/o interface. **/
public interface FOSAbility {
    void write(Writes writes, Unitc unit);

    void read(Reads reads, Unitc unit);
}
