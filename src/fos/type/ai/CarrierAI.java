package fos.type.ai;

import mindustry.content.*;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.world.blocks.distribution.Router;
import mindustry.world.blocks.payloads.PayloadVoid;

import static mindustry.Vars.indexer;

public class CarrierAI extends AIController {
    @Override
    public void updateMovement() {
        PayloadUnit u = (PayloadUnit) unit;
        
        Building a = indexer.findTile(u.team, u.x, u.y, 320f, build -> build.block instanceof Router);
        Building b = indexer.findTile(u.team, u.x, u.y, 320f, build -> build.block instanceof PayloadVoid);
        
        if (!u.hasPayload()) {
            if (a != null) {
                circle(a, 4f);
                if (u.tileOn() == a.tile) {
                    u.pickup(a);
                    Fx.unitPickup.at(a);
                    a.tile.remove();
                }
            } else {
                if (b != null) circle(b, 96f);
            }
        } else {
            if (b == null) return;

            circle(b, 4f);
            if (u.tileOn() == b.tile) {
                u.dropLastPayload();
            }
        }

        faceMovement();
    }
}
