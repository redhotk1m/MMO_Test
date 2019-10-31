package org.openjfx.Packets;

import org.openjfx.Controllers.GameClient;
import org.openjfx.Controllers.GameServer;

public class Packet13ValidationLogin extends Packet{

    private String username, password;
    private int valid_login;
    public Packet13ValidationLogin(byte[] data){
        super(13);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.password = dataArray[1];
        this.valid_login = Integer.parseInt(dataArray[2]);
    }

    public Packet13ValidationLogin(String username, String password){
        super(13);
        this.username = username;
        this.password = password;
    }


    public Packet13ValidationLogin(String username, String password, int valid_login){
        super(13);
        this.username = username;
        this.password = password;
        this.valid_login = valid_login;
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
        return ("13" + this.username + "," + this.password + "," + this.valid_login).getBytes();
    }

    public boolean loginAccepted(){
        return (valid_login == 1);
    }

    public void setValid_login(int valid_login){
        this.valid_login = valid_login;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
