package fos.net;

import fos.type.bullets.StickyBulletType;
import mindustry.gen.Player;
import mindustry.net.*;

public class DetonatePacket extends Packet {
    public Player player;

    @Override
    public void handleServer(NetConnection con) {
        if (con.player == null || con.kicked) return;

        StickyBulletType.detonate(player);
    }
}
