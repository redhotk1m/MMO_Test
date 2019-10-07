package org.openjfx;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.openjfx.Controllers.FXMLController;
import org.openjfx.Controllers.Game;
import org.openjfx.Controllers.GameController;
import org.openjfx.Packets.Packet02Move;

import java.net.InetAddress;

public class PlayerMP {
    Rectangle rectangle;
    ImageView imageView;
    Label usernameLabel;
    public int port,x,y;
    public InetAddress ipAddress;
    private GameController gameController;
    public boolean up, down, right, left;
    VBox playerBox;

    public String username;

    public PlayerMP(String username, int x, int y,InetAddress ipAddress, int port){
        this.username = username;
        this.port = port;
        this.ipAddress = ipAddress;
        Image a = new Image("org/openjfx/smily-64x64-transparent.png");
        this.imageView = new ImageView(a);
        this.usernameLabel = new Label(username);
        playerBox = new VBox(imageView,usernameLabel);
        playerBox.setAlignment(Pos.CENTER);
        setX(x);
        setY(y);
        //playerBox.setLayoutX(x);
        //playerBox.setLayoutY(y);
        //this.rectangle = new Rectangle(x,y,30,30);
        //this.usernameLabel.setLayoutX(x + 6);
        //this.usernameLabel.setLayoutY(y - 15);
    }

    public void move(){

    }

    /*public void move(String button){
        switch (button){
            case "A":
                tick("x",-1);
                break;
            case "S":
                tick("y", 1);
                break;
            case "W":
                tick("y", -1);
                break;
            case "D":
                tick("x",1);
                break;
        }
        //this.x += 10;
        //rectangle.setX(x);
        //usernameLabel.setLayoutX(x);
        //Packet02Move packet = new Packet02Move(this.getUsername(), this.x,this.y);
        //Må sendes til socketClient
        //packet.writeData(FXMLController.controller.getSocketClient());
    }*/

    public void tick(){
        if (right){
            setX(x + 5);
        }
        if (left){
            setX(x - 5);
        }
        if (up){
            setY(y - 5);
        }
        if (down){
            setY(y + 5);
        }
        if (right || left || up || down){
            Packet02Move packet = new Packet02Move(this.getUsername(), this.x,this.y);
            //Må sendes til socketClient
            packet.writeData(FXMLController.controller.getSocketClient());
        }
        //updateVisuals();
        /*if (direction.equals("x")){
            this.x += value;
            rectangle.setX(x);
            usernameLabel.setLayoutX(x);
        }
        if (direction.equals("y")){
            this.y += value;
            rectangle.setY(y);
            usernameLabel.setLayoutY(y);
        }
        */
    }

    public String getUsername() {
        return username;
    }



    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void addNodes(GameController gameController){
        gameController.getAnchorPane().getChildren().addAll(playerBox);
    }

    public void updateVisuals() {
        playerBox.setLayoutX(x);
        playerBox.setLayoutY(y);
    }

    public VBox getVbox() {
        return playerBox;
    }

    public void setX(int x) {
        this.x = x;
        PlatformHelper.run(() -> playerBox.setLayoutX(x));
    }

    public void setY(int y) {
        this.y = y;
        PlatformHelper.run(() -> playerBox.setLayoutY(y));
    }
}
