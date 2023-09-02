package fos.net;

import arc.util.io.Reads;
import arc.util.io.Writes;
import fos.type.blocks.units.UpgradeCenter;
import mindustry.Vars;
import mindustry.gen.Player;
import mindustry.io.TypeIO;
import mindustry.net.Net;
import mindustry.net.NetConnection;
import mindustry.net.Packet;

public class FOSPackets {
    public static void register() {
        Net.registerPacket(UpgradeCenterUpgradePacket::new);
    }

    public static class UpgradeCenterUpgradePacket extends Packet {
        public Player player;
        public UpgradeCenter.UpgradeCenterBuild build;
        public int weapon;

        public UpgradeCenterUpgradePacket() {

        }

        public UpgradeCenterUpgradePacket(Player p, UpgradeCenter.UpgradeCenterBuild b, int w) {
            player = p;
            build = b;
            weapon = w;
        }

        public void write(Writes writes) {
            TypeIO.writeEntity(writes, player);
            writes.i(build.tile.pos());
            writes.i(weapon);
        }

        @Override
        public void read(Reads reads) {
            player = TypeIO.readEntity(reads);
            build = (UpgradeCenter.UpgradeCenterBuild) Vars.world.tile(reads.i()).build;
            weapon = reads.i();
        }

        @Override
        public void handleServer(NetConnection con) {
            build.upgrade(this);
            Vars.net.send(this, true);
        }

        @Override
        public void handleClient() {
            build.upgrade(this);
        }
    }
}
