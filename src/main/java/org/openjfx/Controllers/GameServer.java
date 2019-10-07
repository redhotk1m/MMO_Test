package org.openjfx.Controllers;

import org.openjfx.Packets.*;
import org.openjfx.PlayerMP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class GameServer extends Thread {
    public static boolean server_running = false;
    private DatagramSocket socket;
    private FXMLController controller;
    private List<PlayerMP> connectedPlayers = new ArrayList<>();

    GameServer(FXMLController controller){
        this.controller = controller;
        try {
            this.socket = new DatagramSocket(1337);
            server_running = true;
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (server_running){
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
            System.out.println(connectedPlayers.size() + " is the size, after parsing recieved packet");
        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim(); //Dette er hele strengen som blir mottat
        System.out.println("Parsing message: " + message);
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0,2));
        Packet packet;
        switch (type){
            default:
            case INVALID:
                break;
            case LOGIN:
                //packet = new Packet00Login(data);
                //System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet00Login)packet).getUsername()+ " has connected... received packet from client(s)");
                break;
            case DISCONNECT:
                packet = new Packet01Disconnect(data);
                System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet01Disconnect)packet).getUsername()+ " has LEFT... received packet from client(s)");
                this.removeConnection((Packet01Disconnect)packet);
                break;
            case MOVE:
                packet = new Packet02Move(data);
                handleMove((Packet02Move) packet);
                break;
            case PLAYER_INFO:
                packet = new Packet04PlayerInfo(data);
                //Server får info om en spiller, som skal sendes videre ut til alle andre spillere
                break;
            case VALID_LOGIN:
                SQLite sqLite = new SQLite();
                packet = new Packet03ValidationLogin(data);
                if (sqLite.getUser(
                        ((Packet03ValidationLogin) packet).getUsername(),
                        ((Packet03ValidationLogin) packet).getPassword())) {
                    System.out.println("Stemmer, var riktig brukernavn og passord");
                    System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet03ValidationLogin)packet).getUsername()+ " has connected... received packet from client(s)");
                    //Sender logindata til personen som logget inn (skal brukes til bekreftelse av innlogging
                    //X og Y her må hentes fra sql, og må lagres der i disconnect packeten
                    PlayerMP player = sqLite.loadUser(((Packet03ValidationLogin) packet).getUsername());
                    player.setIpAddress(address);
                    player.setPort(port);
                    //PlayerMP player = new PlayerMP(((Packet03ValidationLogin)packet).getUsername(), 50, 10, address, port);
                    ((Packet03ValidationLogin) packet).setValid_login(1);
                    sendData(packet.getData(),address,port);
                    packet = new Packet04PlayerInfo(((Packet03ValidationLogin) packet).getUsername(), player.x,player.y);
                    //Under må også har riktige verdier for x / y ved bruk av databasen
                    sendData(packet.getData(),address,port);
                    packet = new Packet00Login(((Packet04PlayerInfo) packet).getUsername(), player.x,player.y);
                    this.addConnection(player, (Packet00Login) packet);
                }
                else {
                    packet = new Packet03ValidationLogin(((Packet03ValidationLogin) packet).getUsername(), ((Packet03ValidationLogin) packet).getPassword(),0);
                    sendData(packet.getData(),address,port);
                }

        }
    }

    public void removeConnection(Packet01Disconnect packet) {
        this.connectedPlayers.remove(getPlayerMPIndex(packet.getUsername()));
        packet.writeData(this);
    }

    public PlayerMP getPlayerMP(String username){
        for (PlayerMP player : this.connectedPlayers){
            if(player.getUsername().equalsIgnoreCase(username)){
                return player;
            }
        }
        return null;
    }

    public int getPlayerMPIndex(String username){
        int index = 0;
        for (PlayerMP player : this.connectedPlayers){
            if(player.getUsername().equalsIgnoreCase(username)){
                break;
            }
            index++;
        }
        return index;
    }

    public void addConnection(PlayerMP player, Packet00Login packet2) {
        boolean alreadyConnected = false;
        for (PlayerMP p : this.connectedPlayers){
            Packet00Login packet = packet2; //FIKS
            if (player.getUsername().equalsIgnoreCase(p.getUsername())){
                if (p.ipAddress == null) {
                    p.ipAddress = player.ipAddress;
                }
                if (p.port == -1) {
                    p.port = player.port;
                }
                alreadyConnected = true;
            }else{
                //Sender denne clienten til alle clients som allerede er connecta
                sendData(packet.getData(), p.ipAddress, p.port);
                packet = new Packet00Login(p.getUsername(),p.x, p.y);
                //Sender alle clients som er connecta, til den clienten som connecta nå!
                sendData(packet.getData(), player.ipAddress, player.port);
            }
            packet = null;
        }
        if (!alreadyConnected){
            this.connectedPlayers.add(player);
            //packet.writeData(this);
        }
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port){
        DatagramPacket packet = new DatagramPacket(data,data.length,ipAddress,port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToAllClients(byte[] data) {
        for (PlayerMP p : connectedPlayers){
            sendData(data, p.ipAddress, p.port);
        }
    }

    private void handleMove(Packet02Move packet){
        if (getPlayerMP(packet.getUsername()) != null){
            int index = getPlayerMPIndex(packet.getUsername());
            this.connectedPlayers.get(index).x = packet.getX();
            this.connectedPlayers.get(index).y = packet.getY();
            packet.writeData(this);
        }
    }
}
