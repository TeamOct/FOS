package fos.type.units;

import arc.util.io.Reads;
import arc.util.io.Writes;
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
