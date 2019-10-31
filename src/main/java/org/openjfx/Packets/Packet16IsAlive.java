package org.openjfx.Packets;

import org.openjfx.Controllers.GameClient;
import org.openjfx.Controllers.GameServer;

public class Packet16IsAlive extends Packet {
    public Packet16IsAlive() {
        super(16);
    }

    @Override
    public void writeData(GameClient client) {
        client.sendData(getData(),false);
    }

    @Override
    public void writeData(GameClient client, boolean needsAck) {
        client.sendData(getData(),true);
    }

    @Override
    public void writeData(GameServer server) {
        server.sendDataToAllClients(getData());
    }

    @Override
    public byte[] getData() {
        return ("16").getBytes();
    }
}
