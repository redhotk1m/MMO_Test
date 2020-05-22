package org.openjfx.Controllers;

import javafx.stage.Stage;
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
        Stage stage = (Stage) controller.passwordField.getParent().getScene().getWindow();
        stage.setOnCloseRequest(windowEvent -> sendDisconnectPacket());
        new Thread(this::sendAlivePacket).start();
    }

    private void sendDisconnectPacket() {
        Packet15ServerShutdown packet;
        for (PlayerMP playerMP : connectedPlayers){
            packet = new Packet15ServerShutdown();
            sendData(packet.getData(),playerMP.ipAddress,playerMP.port);
        }
        System.exit(0);
    }

    private void sendAlivePacket(){
        Packet16IsAlive packet;
        while (true) {
            for (PlayerMP playerMP : connectedPlayers) {
                packet = new Packet16IsAlive();
                sendData(packet.getData(), playerMP.ipAddress, playerMP.port);
                System.out.println("sender alive packet!");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (server_running){
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                System.out.println("Avventer pakke");
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
        System.out.println(message + " packetdata mottatt");
        SQLite sqLite;
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0,2));
        Packet packet;
        switch (type){
            default:
            case INVALID:
                break;
            case LOGIN:
                packet = new Packet10Login(data);
                System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet10Login)packet).getUsername()+ " has connected... received packet from client(s)");
                break;
            case DISCONNECT:
                packet = new Packet11Disconnect(data);
                System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet11Disconnect)packet).getUsername()+ " has LEFT... received packet from client(s)");
                this.removeConnection((Packet11Disconnect)packet);
                break;
            case MOVE:
                packet = new Packet12Move(data);
                handleMove((Packet12Move) packet);
                break;
            case PLAYER_INFO:
                packet = new Packet14PlayerInfo(data);
                //Server får info om en spiller, som skal sendes videre ut til alle andre spillere
                break;
            case VALID_LOGIN:
                sqLite = new SQLite();
                packet = new Packet13ValidationLogin(data);
                if (sqLite.getUser
                        (((Packet13ValidationLogin) packet).getUsername(),
                        ((Packet13ValidationLogin) packet).getPassword())){
                    System.out.println("Stemmer, var riktig brukernavn og passord");
                    System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet13ValidationLogin)packet).getUsername()+ " has connected... received packet from client(s)");
                    //Sender logindata til personen som logget inn (skal brukes til bekreftelse av innlogging
                    //X og Y her må hentes fra sql, og må lagres der i disconnect packeten
                    //player.setIpAddress(address);
                    //player.setPort(port);
                    //PlayerMP player = new PlayerMP(((Packet03ValidationLogin)packet).getUsername(), 50, 10, address, port);
                    ((Packet13ValidationLogin) packet).setValid_login(1);
                    sendData(packet.getData(),address,port);
                    PlayerMP player = sqLite.loadUser(((Packet13ValidationLogin) packet).getUsername(),address,port);
                    packet = new Packet14PlayerInfo(((Packet13ValidationLogin) packet).getUsername(), player.x,player.y);
                    //Under må også har riktige verdier for x / y ved bruk av databasen
                    sendData(packet.getData(),address,port);
                    packet = new Packet10Login(((Packet14PlayerInfo) packet).getUsername(), player.x,player.y);
                    this.addConnection(player, (Packet10Login) packet);
                }
                else {
                    packet = new Packet13ValidationLogin("","",0);
                    sendData(packet.getData(),address,port);
                }
                break;
            case CREATE_ACCOUNT:
                packet = new Packet17CreateAccount(data);
                handleCreateAccount((Packet17CreateAccount) packet, address, port);
                break;
        }
    }

    private void handleCreateAccount(Packet17CreateAccount packet, InetAddress address, int port) {
        SQLite sqLite = new SQLite();
        if (!sqLite.userExists(packet.getUsername())) {
            //Bør legge til sjekk for å sjekke at sqLite.userExists også sjekker at brukernavnet er likt? Kanskje ikke
            sqLite.addUser(packet.getUsername(), packet.getPassword());
            System.out.println("server created user:" + packet.getUsername() + " with password: " + packet.getPassword());
            packet = new Packet17CreateAccount(packet.getUsername(),"1");
            sendData(packet.getData(),address,port);
        }
        else {
            System.out.println("Server cannot create user, sends response to client");
            packet = new Packet17CreateAccount(packet.getUsername(),"0");
            //System.out.println(packet.getPassword() + "pass");
            //System.out.println(packet.getData());
            sendData(packet.getData(),address,port);
            //System.out.println(packet.getData() + "\n" +address + "\n" + port + "\n" + packet.getPassword());
        }

    }

    public void removeConnection(Packet11Disconnect packet) {
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

    private void handleValidLogin(){

    }

    private void sendPlayerInfo(){
        
    }

    public void addConnection(PlayerMP player, Packet10Login packet2) {
        boolean alreadyConnected = false;
        for (PlayerMP p : this.connectedPlayers){
            Packet10Login packet = packet2; //FIKS
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
                packet = new Packet10Login(p.getUsername(),p.x, p.y);
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

    private void handleMove(Packet12Move packet){
        if (getPlayerMP(packet.getUsername()) != null){
            int index = getPlayerMPIndex(packet.getUsername());
            PlayerMP player = this.connectedPlayers.get(index);
            if (!validMove(player.x,packet.getX())){
                packet.setX(player.x);
            }else{
                this.connectedPlayers.get(index).x = packet.getX();
            }
            if (!validMove(player.y, packet.getY())){
                System.out.println("NÅ BLIR DENNE KALT!" + player.y + " " + packet.getY());
                packet.setY(player.y);
            }else{
                this.connectedPlayers.get(index).y = packet.getY();
            }

            //this.connectedPlayers.get(index).x = packet.getX();
            //this.connectedPlayers.get(index).y = packet.getY();
            packet.writeData(this);
        }
    }
    private boolean validMove(int before, int after){
        return Math.abs(before - after) <= Math.abs(5) || before == after;
    }
}
