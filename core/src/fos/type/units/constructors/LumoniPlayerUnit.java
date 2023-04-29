package fos.type.units.constructors;

import arc.util.io.*;
import mindustry.gen.LegsUnit;
import mindustry.io.TypeIO;

public class LumoniPlayerUnit extends LegsUnit {

    @Override
    public void write(Writes write) {
        super.write(write);
        TypeIO.writeMounts(write, mounts);
    }

    @Override
    public void read(Reads read) {
        super.read(read);
        TypeIO.readMounts(read, mounts);
    }
}
