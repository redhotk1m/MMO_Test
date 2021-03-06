package org.openjfx.Packets;

import org.openjfx.Controllers.FXMLController;
import org.openjfx.Controllers.GameClient;
import org.openjfx.Controllers.GameServer;

public abstract class Packet {

    public static enum PacketTypes {
        INVALID(-1),
        LOGIN(10),
        DISCONNECT(11),
        MOVE(12),
        VALID_LOGIN(13),
        PLAYER_INFO(14),
        SERVER_DISCONNECT(15),
        SERVER_ALIVE(16),
        CREATE_ACCOUNT(17),
        CONNECT_TO_SERVER(18);

        private int packetId;
        private PacketTypes(int packetId) {
            this.packetId = packetId;
        }

        public int getId() {
            return packetId;
        }
    }

    public byte packetId;

    public Packet(int packetId){
        this.packetId = (byte) packetId;
    }

    //Sends to server from client
    public abstract void writeData(GameClient client);

    //Sends to server from client, waits for ack
    public abstract void writeData(GameClient client, boolean needsAck);



    //Sends to all clients within server
    public abstract void writeData(GameServer server);

    //Sends to all clients within server, wait for ack

    //TODO Create method with ack for server

    public String readData(byte[] data){
        String message = new String(data).trim();
        return message.substring(2);
    }

    public static PacketTypes lookupPacket(String packetId){
        try {
            return lookupPacket(Integer.parseInt(packetId));
        }catch (NumberFormatException e){
            return PacketTypes.INVALID;
        }
    }

    public static PacketTypes lookupPacket(int id){
        for (PacketTypes p : PacketTypes.values()){
            if (p.getId() == id){
                return p;
            }
        }
        return PacketTypes.INVALID;
    }

    public abstract byte[] getData();

}
