package org.openjfx.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openjfx.Exceptions.CannotFindHostException;
import org.openjfx.Packets.*;
import org.openjfx.PlayerMP;

import java.io.IOException;
import java.net.*;

public class GameClient extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private FXMLController controller;
    private boolean login_accepted = false;
    public GameController gameController;
    PlayerMP mainPlayer;

    GameClient(FXMLController controller, String ipAddress){
        this.controller = controller;
        System.out.println("SOCKETCLIENT STARTED");
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("RUN METHOD STARTED");
        try {
            socket.setSoTimeout(0);
            while (true){
                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginAccepted() throws CannotFindHostException {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.setSoTimeout(4000);
                socket.receive(packet);
                this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
            } catch (Exception e) {
                throw new CannotFindHostException("Can't connect to server",e);
            }
    }

    public void sendData(byte[] data){
        DatagramPacket packet = new DatagramPacket(data,data.length,ipAddress,1337);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim();
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0,2));
        Packet packet;
        switch (type){
            default:
            case INVALID:
                break;
            case LOGIN:
                packet = new Packet00Login(data);
                handleLogin((Packet00Login) packet,address,port);
                break;
            case DISCONNECT:
                packet = new Packet01Disconnect(data);
                System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet01Disconnect)packet).getUsername()+ " has LEFT the world... received packet from client(s)");
                break;
            case MOVE:
                packet = new Packet02Move(data);
                handleMove((Packet02Move) packet);
                break;
            case VALID_LOGIN:
                packet = new Packet03ValidationLogin(data);
                handleValidLogin((Packet03ValidationLogin) packet,address,port);
                break;
            case PLAYER_INFO:
                //Spiller får info om andre spillere
                packet = new Packet04PlayerInfo(data);
                handlePlayerInfo((Packet04PlayerInfo) packet,address,port);
                //PlayerMP player = new PlayerMP(((Packet04PlayerInfo)packet).getUsername(),address,port,((Packet04PlayerInfo) packet).getX(),((Packet04PlayerInfo) packet).getY());
                //Game.players.add(player);
                break;
        }
    }

    private void handleMove(Packet02Move packet){
        //movePlayers inne i game klassen
        Game.game.movePlayer(packet.getUsername(),packet.getX(),packet.getY());
    }

    private void handleLogin(Packet00Login packet, InetAddress address, int port){
        System.out.println("[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername()+ " has joined the game... loginpacket recieved");
        PlayerMP player = new PlayerMP(packet.getUsername(),packet.getX(),packet.getY(),address,port);
        Game.players.add(player);
        Platform.runLater(() -> player.addNodes(gameController));
                //gameController.getAnchorPane().getChildren().add(
                //player.getImageView()));
        //Finn en måte å legge playeren til i anchorpanet...
    }

    public void handleValidLogin(Packet03ValidationLogin packet, InetAddress address, int port){
        login_accepted = packet.isValid();
            if (login_accepted) {
                Stage stage = (Stage) controller.passwordField.getParent().getScene().getWindow();
                this.gameController = gotoGame(stage);
                gameController.setSocketClient(this);
                gameController.setStage(stage);
                this.start(); //Ny tråd som venter på packets
            } else {
                new Popup("Wrong username / password");
            }
    }

    public GameController gotoGame(Stage stage){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../test.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("GAME");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loader.getController();
    }
    private void requestPlayerInfo(){

    }

    private void handlePlayerInfo(Packet04PlayerInfo packet, InetAddress address, int port){
        PlayerMP mainPlayer = new PlayerMP(packet.getUsername(),packet.getX(),packet.getY(),address,port);
        Game.game.setMainPlayer(mainPlayer);
        Platform.runLater(() -> {
            gameController.anchorPane.getChildren().addAll(mainPlayer.getVbox());
        });
    }
}
