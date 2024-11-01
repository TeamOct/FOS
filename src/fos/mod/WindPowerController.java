package fos.mod;

import arc.Events;
import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import arc.util.io.*;
import fos.content.FOSWeathers;
import fos.world.blocks.power.WindTurbine.WindTurbineBuild;
import mindustry.gen.Groups;
import mindustry.io.*;

import java.io.*;

import static mindustry.game.EventType.*;

public class WindPowerController implements SaveFileReader.CustomChunk {
    public float totalEfficiency, windPower;

    public ObjectFloatMap<WindTurbineBuild> turbineEfficiencies = new ObjectFloatMap<>();

    public WindPowerController() {
        Events.on(ResetEvent.class, e -> reset());

        Events.on(WorldLoadEvent.class, e -> init());

        Events.run(Trigger.update, this::updateTick);

        SaveVersion.addCustomChunk("fos-wind-power-control", this);
    }

    public void init() {
        Groups.build.each(b -> {
            if (b instanceof WindTurbineBuild wt)
                updateTurbine(wt);
        });

        if (FOSWeathers.wind.instance() != null)
            windPower = FOSWeathers.wind.instance().opacity;
    }

    public void reset() {
        totalEfficiency = 0f;
        windPower = 1f;
        turbineEfficiencies.clear();
    }

    public void updateTick() {
        if (FOSWeathers.wind.instance() == null) return;

        if (totalEfficiency < 20) {
            // restore wind power if there's <20 turbines at 100% efficiency (2,000%)
            windPower = Mathf.clamp(windPower + 0.001f * (20-totalEfficiency));
        } else if (totalEfficiency > 25) {
            // ...or lower it if there's >25 turbines at 100% efficiency (2,500%)
            windPower = Mathf.clamp(windPower - 0.001f * (totalEfficiency-25));
        }

        // I'm forced to update opacity every tick cuz otherwise it'll return back to 1
        var opacity = FOSWeathers.wind.instance().opacity;
        FOSWeathers.wind.instance().opacity(Mathf.lerpDelta(opacity, windPower, 0.05f));

        updateEfficiencyGlobal();
        turbineEfficiencies.each(wt -> updateEfficiency(wt.key));
    }

    public void updateTurbine(WindTurbineBuild build) {
        updateEfficiency(build);
    }

    private void updateEfficiencyGlobal() {
        float total = 0f;
        for (var t : turbineEfficiencies) {
            total += t.value;
        }
        totalEfficiency = total;
    }

    private void updateEfficiency(WindTurbineBuild build) {
        if (!build.isValid()) {
            turbineEfficiencies.remove(build, 0);
            return;
        }
        turbineEfficiencies.put(build, build.productionEfficiency);
    }

    @SuppressWarnings("resource")
    @Override
    public void write(DataOutput stream) throws IOException {
        Writes write = new Writes(stream);
        write.f(windPower);
    }

    @SuppressWarnings("resource")
    @Override
    public void read(DataInput stream) throws IOException {
        Reads read = new Reads(stream);
        windPower = read.f();
    }

    @Override
    public boolean shouldWrite() {
        return FOSWeathers.wind.instance() != null && windPower != 1f;
    }
}
