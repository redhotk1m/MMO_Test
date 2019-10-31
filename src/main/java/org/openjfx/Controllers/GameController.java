package org.openjfx.Controllers;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.openjfx.Packets.Packet11Disconnect;
import org.openjfx.PlayerMP;

public class GameController {

    PlayerMP player;
    GameServer socketServer;
    GameClient socketClient;
    @FXML
    AnchorPane anchorPane;
    @FXML
    Slider speedHackSlider;
    Game game;
    Stage stage;
    Scene scene;
    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    //public static ArrayList<GameController> a = new ArrayList<>();
    @FXML
    Button right;
    public void initialize(){
        //gameLoop.start();
        //this.game = new Game(this,socketClient);
        //PlayerMP playerMP = new PlayerMP()
        //a.add(this);
        speedHackSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                game.mainPlayer.setSpeed(t1);
            }
        });
    }

    @FXML
    Circle ball2;
    AnimationTimer gameLoop = new AnimationTimer() {
        @Override
        public void handle(long l) {
            //ball2.setCenterX(player.x);
            //ball2.setCenterY(player.y);
        }
    };

    public void moveRight(){
        //Beveger kun den første spilleren som legges til i ditt array, er feil! Fiks bedre metode
        //Game.players.get(0).move();
        //ball2.setCenterX(ball2.getCenterX() + 10);
    }

    public void transferMessage(GameClient socketClient) {
        this.socketClient = socketClient;
        //ball2.setCenterX(player.x);
        //ball2.setCenterY(player.y);
        System.out.println("Nå burde socketclient ha gamecontroller");
        System.out.println(socketClient.gameController);
    }

    public void setListeners(){
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.A) {
                Game.players.get(0).left = true;
                //Game.players.get(0).move("A",true);
            }
            if (keyEvent.getCode() == KeyCode.S) {
                Game.players.get(0).down = true;
                //Game.players.get(0).move("S",true);
            }
            if (keyEvent.getCode() == KeyCode.D) {
                Game.players.get(0).right = true;
                //Game.players.get(0).move("D",true);
            }
            if (keyEvent.getCode() == KeyCode.W) {
                Game.players.get(0).up = true;
                //Game.players.get(0).move("W",true);
            }
        });
        scene.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.A) {
                Game.players.get(0).left = false;
                //Game.players.get(0).move("A",true);
            }
            if (keyEvent.getCode() == KeyCode.S) {
                Game.players.get(0).down = false;
                //Game.players.get(0).move("S",true);
            }
            if (keyEvent.getCode() == KeyCode.D) {
                Game.players.get(0).right = false;
                //Game.players.get(0).move("D",true);
            }
            if (keyEvent.getCode() == KeyCode.W) {
                Game.players.get(0).up = false;
                //Game.players.get(0).move("W",true);
            }
        });
    }

    public void setSocketClient(GameClient socketClient) {
        this.socketClient = socketClient;
    }

    public void setStage(Stage stage){
        this.stage = stage;
        this.scene = stage.getScene();
        stage.setOnCloseRequest(windowEvent -> sendDisconnectPacket());
        setListeners();
    }

    private void sendDisconnectPacket(){
        Packet11Disconnect packet = new Packet11Disconnect(Game.game.mainPlayer.getUsername());
        packet.writeData(Game.game.socketClient);
        System.exit(0);
    }

    public void startGame() {
        this.game = new Game(this,socketClient);
    }
}
