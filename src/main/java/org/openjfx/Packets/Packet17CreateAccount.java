package org.openjfx.Packets;

import org.openjfx.Controllers.GameClient;
import org.openjfx.Controllers.GameServer;

public class Packet17CreateAccount extends Packet {
    String username, password;
    public Packet17CreateAccount(byte[] data) {
        super(17);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.password = dataArray[1];
    }

    public Packet17CreateAccount(String username, String password){
        super(17);
        this.username = username;
        this.password = password;
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
        return ("17" + username + ","+ password).getBytes();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
