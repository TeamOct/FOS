package fos.type.blocks.special;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import fos.content.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.campaign.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static arc.Core.camera;
import static mindustry.Vars.*;

//this one actually works!
public class OrbitalAccelerator extends Accelerator {
    public OrbitalAccelerator(String name) {
        super(name);
        buildType = OrbitalAcceleratorBuild::new;
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(Stat.input);
        for (Consume c : consumers) {
            //don't duplicate power use
            if (c instanceof ConsumePower) continue;

            if (c instanceof ConsumeLiquid l) {
                stats.add(Stat.input, l.liquid, l.amount, false);
            } else {
                c.display(stats);
            }
        }
    }

    public class OrbitalAcceleratorBuild extends AcceleratorBuild {
        public CoreBuild build;
        public boolean isCoreUnlocked = FOSBlocks.coreFortress.unlocked();
        public boolean isLaunched = false;

        @Override
        public void draw() {
            super.draw();

            if (isCoreUnlocked) {
                Draw.alpha((float)items.total() / (float)itemCapacity);
                Draw.rect(FOSBlocks.coreFortress.fullIcon, x, y);
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            deselect();

            if (!isCoreUnlocked) return;

            if(!state.isCampaign()) {
                ui.showInfo("@accelerator.campaignonly");
                return;
            }

            if (items.total() == itemCapacity && liquids.currentAmount() == liquidCapacity) {
                ui.showConfirm("@accelerator.confirmtitle", "@accelerator.confirmtext", () -> {
                    isLaunched = true;

                    Reflect.set(renderer, "landTime", 160f);
                    Reflect.set(renderer, "launching", true);
                    Fx.coreLaunchConstruct.at(x, y, launching.size);

                    build = fakeCore(FOSBlocks.coreFortress::newBuilding);

                    Time.run(160f, () -> {
                        isLaunched = false;
                        build.remove();
                        ui.planet.showPlanetLaunch(state.getSector(), sector -> {
                            consume();

                            universe.clearLoadoutInfo();
                            universe.updateLoadout(sector.planet.generator.getDefaultLoadout().findCore(), sector.planet.generator.getDefaultLoadout());

                            Events.fire(EventType.Trigger.acceleratorUse);
                        });
                    });
                });
            }
        }

        public CoreBuild fakeCore(Prov<Building> entityprov) {
            Tile tile = world.tile(tileX(), tileY());
            return (CoreBuild) entityprov.get().init(tile, team, true, 0);
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if (isLaunched) {
                camera.position.set(x, y);
                Reflect.set(renderer, "landCore", build);
                Reflect.set(renderer, "launchCoreType", launching);
            }
        }

        @Override
        public boolean shouldConsume() {
            return false;
        }
    }
}
