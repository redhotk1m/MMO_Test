package org.openjfx.Packets;

import org.openjfx.Controllers.Game;
import org.openjfx.Controllers.GameClient;
import org.openjfx.Controllers.GameServer;


public class Packet12Move extends Packet{

    private String username;
    private int x,y, direction, isIdle;

    public Packet12Move(byte[] data){
        super(12);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.x = Integer.parseInt(dataArray[1]);
        this.y = Integer.parseInt(dataArray[2]);
        this.direction = Integer.parseInt(dataArray[3]);
        this.isIdle = Integer.parseInt(dataArray[4]);
    }

    public Packet12Move(String username, int x, int y, int direction, int isIdle){
        super(12);
        this.username = username;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.isIdle = isIdle;
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
            return ("12" + this.username + "," + this.x + "," + this.y + "," + this.direction + "," + this.isIdle).getBytes();
        }

        public String getUsername(){
            return username;
        }

        public int getX(){
            return this.x;
        }

        public int getY(){
            return this.y;
        }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getIsIdle() {
        return isIdle;
    }

    public void setIsIdle(int isIdle) {
        this.isIdle = isIdle;
    }
}
