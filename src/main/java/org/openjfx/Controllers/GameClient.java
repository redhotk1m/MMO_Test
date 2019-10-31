package org.openjfx.Controllers;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.openjfx.*;
import org.openjfx.Exceptions.CannotFindHostException;
import org.openjfx.Packets.*;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class GameClient extends Thread {
    private InetAddress ipAddress;
    private DatagramSocket socket;
    private FXMLController controller;
    public GameController gameController;
    public boolean serverIsAlive = false;
    private static int secondsSinceLastServerAlivePacket = 0;
    final Object lock = new Object();

    GameClient(FXMLController controller, String ipAddress) throws UnknownHostException, SocketException {
        this.controller = controller;
        this.ipAddress = InetAddress.getByName(ipAddress);
        this.socket = new DatagramSocket();
        serverIsAlive = true;
        //this.start();
    }

    public void login(String usernameInput, String passwordInput){
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            sendLoginPacket(usernameInput, passwordInput);
            receiveResponseFromServer(0,packet);
        } catch (CannotFindHostException e) {
            new Popup(e.getMessage());
        }
    }

    private void sendLoginPacket(String usernameInput, String passwordInput) throws CannotFindHostException {
        Packet13ValidationLogin packet13ValidationLogin = new Packet13ValidationLogin(usernameInput,passwordInput);
        //new Thread(() ->packet13ValidationLogin.writeData(this,true)).start();
        packet13ValidationLogin.writeData(this);//Sender brukernavn / passord til server og spør om innlogging

        //test2();
        //Nå må vi motta godkjennelse fra serveren før vi fortsetter her
        /*byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        int attempts = 0;
        while (!serverIsAlive) {
            try {
                socket.setSoTimeout(1000); //Setter socket timeout til å kaste exception dersom den ikke får en packet innen 1sek
                socket.receive(packet); //Mottar en packet fra server som inneholder informasjon om login var vellykket eller ei
                serverIsAlive = true;
                this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
                new Thread(this::recieveAlivePacket).start();
            } catch (Exception e) {
                System.out.println(attempts + " forsøk på å logge inn");
                if (++attempts >= 10)
                    serverIsAlive = true;
            }
        }
        if (attempts >= 10)
            throw new CannotFindHostException("Can't connect to server");
        */
    }

    private void recieveAlivePacket(){
        boolean server_alive = true;
        while (server_alive){
            try {
                secondsSinceLastServerAlivePacket++;
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (secondsSinceLastServerAlivePacket >= 20){
                new Popup("Lost connection to server",true);
                server_alive = false;
            }
        }
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(0); //Setter socket timeout til evig
            while (serverIsAlive){
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
                serverIsAlive = true;
                this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
                new Thread(this::recieveAlivePacket).start();
            } catch (Exception e) {
                if (numberOfAttempts < 4){
                    return receiveResponseFromServer(++numberOfAttempts, packet); //Får ikke klient svar fra server innen 1 sek, prøver den igjen. Totalt 5 ganger før den gir opp og sier at server ikke er tilgjengelig
                }else{
                    throw new CannotFindHostException("Cannot connect to server");
                }
            }
            return 0;
    }

    public void sendData(byte[] data, boolean needsAck){
        DatagramPacket packet = new DatagramPacket(data,data.length,ipAddress,1337);
        DatagramSocket datagramSocket = null;

        boolean packetReceived = false;
        int test = 0;
        while (!packetReceived) {
            try {
                if (!needsAck) {
                    packetReceived = true;
                    socket.send(packet);
                }
                else {
                    /*try {
                        synchronized (lock) {
                            System.out.println("Locker objektet!" + System.currentTimeMillis());
                            socket.send(packet);
                            lock.wait();
                            System.out.println("Nå er objektet unlocka! " + System.currentTimeMillis());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    datagramSocket = new DatagramSocket();
                    datagramSocket.setSoTimeout(500);
                    sendDataWithAck(data, datagramSocket);
                    data = new byte[1024];
                    packet = new DatagramPacket(data, data.length);
                    datagramSocket.receive(packet);
                    parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
                    packetReceived = true;
                    datagramSocket.close();
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (datagramSocket != null) {
                    datagramSocket.close();
                    if (++test > 10) {
                        packetReceived = true;
                        //TODO Fikse sånn at det ikke heter packetReceived, men could not connect eller no
                        new Popup("Can't connect to server");
                    }
                    //TODO Legger til maks antall forsøk? Kan ikke prøve evig dersom serveren er nede
                    System.out.println("Fikk ikke mottat packet, antall forsøk: " + test + " med port" + datagramSocket.getLocalPort());
                }
                //new Popup(e.getMessage(),true);
            }
        }
    }


    public DatagramSocket sendDataWithAck(byte[] data,DatagramSocket datagramSocket){
        DatagramPacket packet = new DatagramPacket(data,data.length,ipAddress,1337);
        try {
            datagramSocket.send(packet);
            String a = new String(data);
            System.out.println("Packet er: " + a);
            return datagramSocket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized void parsePacket(byte[] data, InetAddress address, int port) {
        secondsSinceLastServerAlivePacket = 0;
        String message = new String(data).trim();
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0,2));
        Packet packet;
        switch (type){
            default:
            case INVALID:
                break;
            case LOGIN:
                packet = new Packet10Login(data);
                handleLogin((Packet10Login) packet,address,port);
                break;
            case DISCONNECT:
                packet = new Packet11Disconnect(data);
                handleDisconnect((Packet11Disconnect) packet,address,port);
                break;
            case MOVE:
                packet = new Packet12Move(data);
                handleMove((Packet12Move) packet);
                break;
            case VALID_LOGIN:
                /*synchronized (lock) {
                    System.out.println("LockObject blir notified");
                    lock.notify();
                    System.out.println("Lock object har blitt notified");
                }*/
                packet = new Packet13ValidationLogin(data);
                handleValidLogin((Packet13ValidationLogin) packet,address,port);
                break;
            case PLAYER_INFO:
                //Spiller får info om andre spillere
                packet = new Packet14PlayerInfo(data);
                handlePlayerInfo((Packet14PlayerInfo) packet,address,port);
                //PlayerMP player = new PlayerMP(((Packet04PlayerInfo)packet).getUsername(),address,port,((Packet04PlayerInfo) packet).getX(),((Packet04PlayerInfo) packet).getY());
                //Game.players.add(player);
                break;
            case SERVER_DISCONNECT:
                handleServerDisconnect();
                break;
            case SERVER_ALIVE:
                secondsSinceLastServerAlivePacket = 0;
                break;
            case CREATE_ACCOUNT:
                System.out.println("Mottar create account");
                packet = new Packet17CreateAccount(data);
                handleCreateAccount((Packet17CreateAccount) packet);
                break;
            case CONNECT_TO_SERVER:
                serverIsAlive = true;
                start();
        }
    }

    private void handleCreateAccount(Packet17CreateAccount packet) {
        System.out.println("popup skal skje");
        System.out.println(packet.getPassword());
        if (packet.getPassword().equals("0"))
            new Popup("Account creation:\nFailed!\n" + packet.getUsername() +" is taken.");
        else
            new Popup("Account creation:\nSuccess!\nYou may now log in");
    }

    private void handleServerDisconnect() {
        new Popup("Server has shutdown",true);
        //System.exit(0);
    }

    private void handleDisconnect(Packet11Disconnect packet, InetAddress address, int port) {
        System.out.println("[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername()+ " has LEFT the world... received packet from client(s)");
        Game.game.removePlayer(packet.getUsername());
    }

    private void handleMove(Packet12Move packet){
        //kaller på movePlayers i game klassen, som oppdaterer alle posisjonene til hver spiller i et arraylist (av alle som er pålogget / vises på skjerm)
        Game.game.movePlayer(packet.getUsername(),packet.getX(),packet.getY(),packet.getDirection(),packet.getIsIdle());
    }

    private void handleLogin(Packet10Login packet, InetAddress address, int port){
        System.out.println("[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername()+ " has joined the game... loginpacket recieved");
        PlayerMP player = new PlayerMP(packet.getUsername(),packet.getX(),packet.getY(),address,port);
        Game.players.add(player);
        Platform.runLater(() -> player.addNodes(gameController)); //Når det er mulig, vis nye spiller på skjerm
        //Bør kanskje endres til level.addEntity(player), hvor addEntity bruker player.addNodes(gameController)
        //Evenetuelt så blir level klassen gameController? Mulig å få dette til å fungere? 1 kontroller for alle level/maps (ikke 1 kontroller per level)
    }

    public void handleValidLogin(Packet13ValidationLogin packet, InetAddress address, int port){
            if (packet.loginAccepted()) {
                Stage stage = (Stage) controller.passwordField.getParent().getScene().getWindow();
                ChangeScene sceneChanger = new ChangeScene<GameController>(stage,"test.fxml");
                this.gameController = (GameController) sceneChanger.getController();
                gameController.setSocketClient(this);
                gameController.setStage(stage);
                gameController.startGame();
                this.start(); //Ny tråd som venter på packets
            } else {
                new Popup("Wrong username / password");
            }
    }

    private void handlePlayerInfo(Packet14PlayerInfo packet, InetAddress address, int port){
        PlayerMP mainPlayer = new PlayerMP(packet.getUsername(),packet.getX(),packet.getY(),address,port);
        Game.game.setMainPlayer(mainPlayer);
        PlatformHelper.run(() -> gameController.anchorPane.getChildren().addAll(mainPlayer.getVbox()));
        /*Platform.runLater(() -> {
            gameController.anchorPane.getChildren().addAll(mainPlayer.getVbox());
        });*/
    }

    public void createAccount(String username, String password) {
        Packet17CreateAccount packet = new Packet17CreateAccount(username,password);
        packet.writeData(this,true);
        //new Thread(this::test).start();
    }

    private void test(){
        try {
            socket.setSoTimeout(1000); //Setter socket timeout til evig
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
        } catch (IOException e) {
            new Popup("Cannot connect to server");
        }
    }
}
