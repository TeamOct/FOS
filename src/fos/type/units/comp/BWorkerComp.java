package fos.type.units.comp;

import arc.util.io.*;
import mindustry.gen.*;
import mindustry.io.TypeIO;

import static ent.anno.Annotations.EntityComponent;

@EntityComponent @SuppressWarnings("unused")
abstract class BWorkerComp implements Syncc {
    transient Building nest;

    @Override
    public void write(Writes write) {
        TypeIO.writeBuilding(write, nest);
    }

    @Override
    public void read(Reads read) {
        nest = TypeIO.readBuilding(read);
    }
}
