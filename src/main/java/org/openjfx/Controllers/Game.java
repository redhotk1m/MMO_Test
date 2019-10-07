package org.openjfx.Controllers;

import javafx.animation.AnimationTimer;
import org.openjfx.PlatformHelper;
import org.openjfx.PlayerMP;

import java.util.ArrayList;

public class Game {

    public static Game game;
    public PlayerMP mainPlayer;
    public void setMainPlayer(PlayerMP mainPlayer) {
        this.mainPlayer = mainPlayer;
        players.add(mainPlayer);
        gameLoop.start();
    }

    private final GameController gameController;

    Game(GameController gameController, GameClient socketClient){
        game = this;
        this.gameController = gameController;
    }


    public static ArrayList<PlayerMP> players = new ArrayList<>();

    AnimationTimer gameLoop = new AnimationTimer() {
        @Override
        public void handle(long l) {
            mainPlayer.tick();
        }
    };




    public void movePlayer(String username, int x, int y){
        int index = getPlayerMPIndex(username);
        players.get(index).setX(x);
        players.get(index).setY(y);
        //PlatformHelper.run(() -> players.get(index).updateVisuals());
        System.out.println("accepting move packet, moving to x: " + x + " y: " + y);
    }

    private int getPlayerMPIndex(String username) {
        int index = 0;
        for (PlayerMP playerMP: players){
            if (playerMP.getUsername().equals(username)){
                break;
            }
            index++;
        }
        return index;
    }

}
