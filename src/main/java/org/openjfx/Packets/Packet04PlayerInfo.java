package org.openjfx.Packets;

import org.openjfx.Controllers.GameClient;
import org.openjfx.Controllers.GameServer;

public class Packet04PlayerInfo extends Packet{

    private String username;
    private int x,y;
    public Packet04PlayerInfo(byte[] data){
        super(00);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.x = Integer.parseInt(dataArray[1]);
        this.y = Integer.parseInt(dataArray[2]);
    }

    public Packet04PlayerInfo(String username, int x, int y){
        super(00);
        this.username = username;
        this.x = x;
        this.y = y;
    }

    public Packet04PlayerInfo(String username){
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
        return ("04" + this.username + "," + this.x + "," + this.y).getBytes();
    }

    public String getUsername(){
        return username;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }


}
