package org.openjfx.Packets;

import org.openjfx.Controllers.GameClient;
import org.openjfx.Controllers.GameServer;

public class Packet00Login extends Packet {
    private String username, password;
    private int x, y;
    public Packet00Login(byte[] data){
        super(00);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.x = Integer.parseInt(dataArray[1]);
        this.y = Integer.parseInt(dataArray[2]);
    }

    public Packet00Login(String username, int x, int y){
        super(00);
        this.username = username;
        this.x = x;
        this.y = y;
    }

    public Packet00Login(String username){
        super(00);
        this.username = username;
    }

    @Override
    public void writeData(GameClient client) {
        client.sendData(getData());
    }

    @Override
    public void writeData(GameServer server) {
        server.sendDataToAllClients(getData());
    }

    @Override
    public byte[] getData() {
        return ("00" + this.username + "," + getX() + "," +getY()).getBytes();
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
