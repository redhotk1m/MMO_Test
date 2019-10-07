package org.openjfx.Packets;

import org.openjfx.Controllers.FXMLController;
import org.openjfx.Controllers.GameClient;
import org.openjfx.Controllers.GameServer;

public abstract class Packet {

    public static enum PacketTypes {
        INVALID(-1), LOGIN(00), DISCONNECT(01), MOVE(02),VALID_LOGIN(03), PLAYER_INFO(04);

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


    //Sends to all clients within server
    public abstract void writeData(GameServer server);

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
