package fos.ai;

import fos.type.blocks.campaign.ResearchCore;
import mindustry.content.*;
import mindustry.entities.units.AIController;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.blocks.distribution.Router;
import mindustry.world.blocks.payloads.PayloadVoid;

import static mindustry.Vars.indexer;

public class CarrierAI extends AIController {
    @Override
    public void updateMovement() {
        PayloadUnit u = (PayloadUnit) unit;

        //derelict block a unit could pick up
        Building a = indexer.findTile(Team.derelict, u.x, u.y, 320f, build -> build.block instanceof Router && build.block.size * 8 * build.block.size * 8 <= unit.type.payloadCapacity);
        //nearest research core
        Building b = indexer.findTile(u.team, u.x, u.y, 320f, build -> build.block instanceof ResearchCore);
        
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
