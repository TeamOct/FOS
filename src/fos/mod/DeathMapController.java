package fos.mod;

import arc.Events;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.io.*;

import java.io.*;

import static mindustry.Vars.world;

public class DeathMapController implements SaveFileReader.CustomChunk {
    public volatile short[] deathMap;
    public boolean shouldSave;
    public float updateDelay = 600f;
    public Seq<Runnable> updateQueue = new Seq<>();

    public DeathMapController() {
        Events.on(EventType.ResetEvent.class, e -> reset());

        Events.on(EventType.WorldLoadEvent.class, e -> {
            deathMap = new short[world.width() * world.height()];
        });

        Events.on(FOSEventTypes.InsectDeathEvent.class, e -> {
            // delay update by 10 seconds
            Runnable r = () -> {
                if (!Vars.state.isGame()) return;

                int ti = e.tile.array();
                deathMap[ti] += 1;

                e.tile.circle(3, (tile) -> {
                    if (tile == null) return;
                    deathMap[tile.array()] += 1;

                    // overflow? leave it at max
                    if (deathMap[tile.array()] < 0) {
                        deathMap[tile.array()] = Short.MAX_VALUE;
                    }

                    shouldSave = true;
                });
            };

            updateQueue.add(r);
            Time.run(updateDelay, r);
            Time.run(updateDelay + 1f, () -> updateQueue.remove(r));
        });

        SaveVersion.addCustomChunk("fos-bug-death-map", this);
    }

    void reset() {
        deathMap = null;
    }

    @SuppressWarnings("resource")
    @Override
    public void write(DataOutput stream) throws IOException {
        try {
            Writes write = new Writes(stream);
            write.i(deathMap.length);

            // whatever had been delayed up to this point, execute immediately on save
            for (var r : updateQueue) {
                r.run();
                updateQueue.remove(r);
            }

            Time.clear();

            for (short s : deathMap) {
                write.s(s);
            }
        } catch (Exception e) {
            Log.err(e);
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
