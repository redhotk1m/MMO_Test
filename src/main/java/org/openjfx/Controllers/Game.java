package org.openjfx.Controllers;

import javafx.animation.AnimationTimer;
import javafx.util.Duration;
import org.openjfx.Animation.SpriteAnimation;
import org.openjfx.PlatformHelper;
import org.openjfx.PlayerMP;

import java.util.ArrayList;

public class Game {

    public static Game game;
    public PlayerMP mainPlayer;
    public GameClient socketClient;
    public void setMainPlayer(PlayerMP mainPlayer) {
        this.mainPlayer = mainPlayer;
        mainPlayer.setMainPlayerUnderline(true);
        getPlayers().add(mainPlayer);
        gameLoop.start();
    }

    private final GameController gameController;

    Game(GameController gameController, GameClient socketClient){
        game = this;
        this.gameController = gameController;
        this.socketClient = socketClient;
    }


    public static ArrayList<PlayerMP> players = new ArrayList<>();

    AnimationTimer gameLoop = new AnimationTimer() {
        @Override
        public void handle(long l) {
            mainPlayer.tick();
        }
    };

    public synchronized ArrayList<PlayerMP> getPlayers(){
        return players;
    }




    public void movePlayer(String username, int x, int y, int direction, int idle){
        int index = getPlayerMPIndex(username);
        PlayerMP player = getPlayers().get(index);
        player.setDirection(direction);
        player.setIdle(idle);
        player.setX(x);
        player.setY(y);
        PlatformHelper.run(player::setDirectionAnimation);
        //PlatformHelper.run(() -> players.get(index).updateVisuals());
        System.out.println("accepting move packet, moving to x: " + x + " y: " + y + " direction (1 = left) : " + direction + " idle (1 = idle)" + idle);
    }

    private int getPlayerMPIndex(String username) {
        int index = 0;
        for (PlayerMP playerMP: getPlayers()){
            if (playerMP.getUsername().equals(username)){
                break;
            }
            index++;
        }
        return index;
    }

    public void removePlayer(String username) {
        int index = getPlayerMPIndex(username);
        getPlayers().get(index).remove(gameController);
        getPlayers().remove(index);
    }
}
