package org.openjfx.Packets;

import org.openjfx.Controllers.GameClient;
import org.openjfx.Controllers.GameServer;

public class Packet11Disconnect extends Packet {

    private String username;

    public Packet11Disconnect(byte[] data){
        super(11);
        this.username = readData(data);
    }

    public Packet11Disconnect(String username){
        super(11);
        this.username = username;
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
        return ("11" + this.username).getBytes();
    }

    public String getUsername(){
        return username;
    }
}
