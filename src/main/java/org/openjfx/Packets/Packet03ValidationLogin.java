package org.openjfx.Packets;

import org.openjfx.Controllers.GameClient;
import org.openjfx.Controllers.GameServer;

public class Packet03ValidationLogin extends Packet{

        private String username, password;
        private int valid_login;
        public Packet03ValidationLogin(byte[] data){
            super(03);
            String[] dataArray = readData(data).split(",");
            this.username = dataArray[0];
            this.password = dataArray[1];
            this.valid_login = Integer.parseInt(dataArray[2]);
        }

        public Packet03ValidationLogin(String username, String password, int valid_login){
            super(03);
            this.username = username;
            this.password = password;
            this.valid_login = valid_login;
        }


    public Packet03ValidationLogin(String username, String password){
        super(03);
        this.username = username;
        this.password = password;
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
            return ("03" + this.username + "," + this.password + "," + this.valid_login).getBytes();
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
