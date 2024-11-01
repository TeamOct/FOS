package fos.mod;

import arc.Events;
import arc.util.io.*;
import mindustry.game.EventType;
import mindustry.io.*;

import java.io.*;

import static mindustry.Vars.world;

public class DeathMapController implements SaveFileReader.CustomChunk {
    public volatile short[] deathMap;
    public boolean shouldSave;

    public DeathMapController() {
        Events.on(EventType.ResetEvent.class, e -> reset());

        Events.on(EventType.WorldLoadEvent.class, e -> {
            deathMap = new short[world.width() * world.height()];
        });

        Events.on(FOSEventTypes.InsectDeathEvent.class, e -> {
            int ti = e.tile.array();
            deathMap[ti] += 25;

            e.tile.circle(3, (tile) -> {
                if (tile == null) return;
                deathMap[tile.array()] += 25;

                // overflow? leave it at max
                if (deathMap[tile.array()] < 0) {
                    deathMap[tile.array()] = Short.MAX_VALUE;
                }

                shouldSave = true;
            });
        });

        SaveVersion.addCustomChunk("fos-bug-death-map", this);
    }

    void reset() {
        deathMap = null;
    }

    @SuppressWarnings("resource")
    @Override
    public void write(DataOutput stream) throws IOException {
        Writes write = new Writes(stream);
        write.i(deathMap.length);

        for (short s : deathMap) {
            write.s(s);
        }
    }

    @SuppressWarnings("resource")
    @Override
    public void read(DataInput stream) throws IOException {
        Reads read = new Reads(stream);
        deathMap = new short[read.i()];
        for (int i = 0; i < deathMap.length; i++) {
            deathMap[i] = read.s();
        }
    }

    @Override
    public boolean shouldWrite() {
        return deathMap != null && shouldSave;
    }
}
