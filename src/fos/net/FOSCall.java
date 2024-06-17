package fos.net;

import fos.type.blocks.special.UpgradeCenter;
import fos.type.bullets.StickyBulletType;
import mindustry.gen.Player;
import mindustry.net.NetConnection;
import mindustry.world.Tile;

import static mindustry.Vars.*;

public class FOSCall {
    public static void registerPackets() {

    }

    public static void upgrade(Player player, Tile tile) {
        UpgradeCenter.upgrade(player, tile);
        if (net.active()) {
            UpgradePacket packet = new UpgradePacket();
            if (net.server()) {
                packet.player = player;
            }
            packet.tile = tile;
            net.send(packet, false);
        }
    }

    public static void upgradeForward(NetConnection except, Player player, Tile tile) {
        UpgradeCenter.upgrade(player, tile);
        if (net.active()) {
            UpgradePacket packet = new UpgradePacket();
            if (net.server()) {
                packet.player = player;
            }
            packet.tile = tile;
            net.sendExcept(except, packet, false);
        }
    }

    public static void detonate() {
        StickyBulletType.detonate(player);
        if (net.client()) {
            DetonatePacket packet = new DetonatePacket();
            net.send(packet, false);
        }
    }
}
