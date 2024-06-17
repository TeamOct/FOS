package fos.net;

import arc.util.io.*;
import fos.type.blocks.special.UpgradeCenter;
import mindustry.gen.Player;
import mindustry.io.TypeIO;
import mindustry.net.*;
import mindustry.world.Tile;

import static mindustry.Vars.net;

public class UpgradePacket extends Packet {
    private byte[] DATA = NODATA;
    public Player player;
    public Tile tile;

    @Override
    public void write(Writes write) {
        if (net.server()) {
            TypeIO.writeEntity(write, player);
        }
        TypeIO.writeTile(write, tile);
    }

    @Override
    public void read(Reads read, int length) {
        DATA = read.b(length);
    }

    @Override
    public void handled() {
        BAIS.setBytes(DATA);
        if (net.client()) {
            player = TypeIO.readEntity(READ);
        }
        tile = TypeIO.readTile(READ);
    }

    @Override
    public void handleServer(NetConnection con) {
        if (con.player == null || con.kicked) return;

        Player player = con.player;
        UpgradeCenter.upgrade(player, tile);
        FOSCall.upgradeForward(con, player, tile);
    }

    @Override
    public void handleClient() {
        UpgradeCenter.upgrade(player, tile);
    }
}
