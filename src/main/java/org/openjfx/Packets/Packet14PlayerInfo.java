package org.openjfx.Packets;

import org.openjfx.Controllers.GameClient;
import org.openjfx.Controllers.GameServer;

public class Packet14PlayerInfo extends Packet{

    private String username;
    private int x,y;
    public Packet14PlayerInfo(byte[] data){
        super(14);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.x = Integer.parseInt(dataArray[1]);
        this.y = Integer.parseInt(dataArray[2]);
    }

    public Packet14PlayerInfo(String username, int x, int y){
        super(14);
        this.username = username;
        this.x = x;
        this.y = y;
    }

    public Packet14PlayerInfo(String username){
        super(14);
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
        return ("14" + this.username + "," + this.x + "," + this.y).getBytes();
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
