package org.openjfx.Packets;

import javafx.application.Platform;
import org.openjfx.Controllers.Game;
import org.openjfx.Controllers.GameClient;
import org.openjfx.PlayerMP;

import java.net.InetAddress;

public class PacketHandler {

    static GameClient socketClient;

    PacketHandler(GameClient gameClient){
        socketClient = gameClient;
    }


    public static void handleMove(Packet12Move packet){
        //kaller på movePlayers i game klassen, som oppdaterer alle posisjonene til hver spiller i et arraylist (av alle som er pålogget / vises på skjerm)
        System.out.println(packet.getUsername() + " Getting movePacket from this user");
        Game.game.movePlayer(packet.getUsername(),packet.getX(),packet.getY(), packet.getDirection(), packet.getIsIdle());
    }

    public static void handleLogin(Packet10Login packet, InetAddress address, int port){
        System.out.println("[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername()+ " has joined the game... loginpacket recieved");
        PlayerMP player = new PlayerMP(packet.getUsername(),packet.getX(),packet.getY(),address,port);
        Game.players.add(player);
        Platform.runLater(() -> player.addNodes(socketClient.gameController)); //Når det er mulig, vis nye spiller på skjerm
        //Bør kanskje endres til level.addEntity(player), hvor addEntity bruker player.addNodes(gameController)
        //Evenetuelt så blir level klassen gameController? Mulig å få dette til å fungere? 1 kontroller for alle level/maps (ikke 1 kontroller per level)
    }
    


}
