package org.openjfx.Controllers;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.openjfx.ChangeScene;
import org.openjfx.Exceptions.CannotFindHostException;
import org.openjfx.Packets.*;
import org.openjfx.PlatformHelper;
import org.openjfx.PlayerMP;

import java.io.IOException;
import java.net.*;

public class GameClient extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private FXMLController controller;
    public GameController gameController;
    private String usernameInput, passwordInput;

    GameClient(FXMLController controller, String ipAddress, String usernameInput, String passwordInput){
        this.controller = controller;
        this.usernameInput = usernameInput;
        this.passwordInput = passwordInput;
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
            sendLoginPacket();
            receiveResponseFromServer(0,packet);
        } catch (SocketException | UnknownHostException | CannotFindHostException e) {
            new Popup(e.getMessage());
        }
    }

    private void sendLoginPacket() {
        Packet03ValidationLogin packet03ValidationLogin = new Packet03ValidationLogin(usernameInput,passwordInput);
        packet03ValidationLogin.writeData(this);//Sender brukernavn / passord til server og spør om innlogging
        //Nå må vi motta godkjennelse fra serveren før vi fortsetter her
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(0); //Setter socket timeout til evig
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

    public int receiveResponseFromServer(int numberOfAttempts, DatagramPacket packet) throws CannotFindHostException {
            try {
                socket.setSoTimeout(1000); //Setter socket timeout til å kaste exception dersom den ikke får en packet innen 1sek
                socket.receive(packet); //Mottar en packet fra server som inneholder informasjon om login var vellykket eller ei
                this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
            } catch (Exception e) {
                if (numberOfAttempts < 4){
                    return receiveResponseFromServer(++numberOfAttempts, packet); //Får ikke klient svar fra server innen 1 sek, prøver den igjen. Totalt 5 ganger før den gir opp og sier at server ikke er tilgjengelig
                }else{
                    throw new CannotFindHostException("Cannot connect to server", e);
                }
            }
            return 0;
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
                handleDisconnect((Packet01Disconnect) packet,address,port);
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

    private void handleDisconnect(Packet01Disconnect packet, InetAddress address, int port) {
        System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet01Disconnect)packet).getUsername()+ " has LEFT the world... received packet from client(s)");
    }

    private void handleMove(Packet02Move packet){
        //kaller på movePlayers i game klassen, som oppdaterer alle posisjonene til hver spiller i et arraylist (av alle som er pålogget / vises på skjerm)
        Game.game.movePlayer(packet.getUsername(),packet.getX(),packet.getY());
    }

    private void handleLogin(Packet00Login packet, InetAddress address, int port){
        System.out.println("[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername()+ " has joined the game... loginpacket recieved");
        PlayerMP player = new PlayerMP(packet.getUsername(),packet.getX(),packet.getY(),address,port);
        Game.players.add(player);
        Platform.runLater(() -> player.addNodes(gameController)); //Når det er mulig, vis nye spiller på skjerm
        //Bør kanskje endres til level.addEntity(player), hvor addEntity bruker player.addNodes(gameController)
        //Evenetuelt så blir level klassen gameController? Mulig å få dette til å fungere? 1 kontroller for alle level/maps (ikke 1 kontroller per level)
    }

    public void handleValidLogin(Packet03ValidationLogin packet, InetAddress address, int port){
            if (packet.loginAccepted()) {
                Stage stage = (Stage) controller.passwordField.getParent().getScene().getWindow();
                ChangeScene sceneChanger = new ChangeScene<GameController>(stage,"test.fxml");
                this.gameController = (GameController) sceneChanger.getController();
                gameController.setSocketClient(this);
                gameController.setStage(stage);
                this.start(); //Ny tråd som venter på packets
            } else {
                new Popup("Wrong username / password");
            }
    }

    private void handlePlayerInfo(Packet04PlayerInfo packet, InetAddress address, int port){
        PlayerMP mainPlayer = new PlayerMP(packet.getUsername(),packet.getX(),packet.getY(),address,port);
        Game.game.setMainPlayer(mainPlayer);
        PlatformHelper.run(() -> gameController.anchorPane.getChildren().addAll(mainPlayer.getVbox()));
        /*Platform.runLater(() -> {
            gameController.anchorPane.getChildren().addAll(mainPlayer.getVbox());
        });*/
    }
}
