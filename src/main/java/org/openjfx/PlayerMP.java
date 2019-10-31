package org.openjfx;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.openjfx.Animation.SpriteAnimation;
import org.openjfx.Controllers.FXMLController;
import org.openjfx.Controllers.GameController;
import org.openjfx.Packets.Packet12Move;

import java.net.InetAddress;

public class PlayerMP {
    Rectangle rectangle;
    ImageView imageView;
    Label usernameLabel;
    public int port,x,y;
    public int direction; //0 = right, 1 = left
    public InetAddress ipAddress;
    public boolean up, down, right, left;
    public int MOV_SPEED = 3;
    private int isIdle; // 1 == idle
    VBox playerBox;

    public String username;
    public SpriteAnimation animation;
    public PlayerMP(String username, int x, int y,InetAddress ipAddress, int port){
        this.username = username;
        this.port = port;
        this.ipAddress = ipAddress;
        initAnimation(x,y);
        //Image a = new Image("org/openjfx/smily-64x64-transparent.png");
        //playerBox.setLayoutX(x);
        //playerBox.setLayoutY(y);
        //this.rectangle = new Rectangle(x,y,30,30);
        //this.usernameLabel.setLayoutX(x + 6);
        //this.usernameLabel.setLayoutY(y - 15);
    }

    private void initAnimation(int x, int y){
        Image ani = new Image("org/openjfx/0x72_DungeonTilesetII_v1.3.png");
        this.imageView = new ImageView(ani);
        this.usernameLabel = new Label(username);
        this.usernameLabel.setCursor(Cursor.TEXT);
        playerBox = new VBox(imageView,usernameLabel);
        playerBox.setCursor(Cursor.HAND);
        playerBox.setAlignment(Pos.CENTER);
        animation = new SpriteAnimation(this.getImageView(), Duration.millis(400),
                4, 4, 128, 4, 16, 28);
        animation.getImageView().setRotationAxis(Rotate.Y_AXIS);
        animation.play();
        setX(x);
        setY(y);
        //playerBox.setLayoutX(x);
    }

    public void moveRight(){
        animation.setOffsetX(192);
        System.out.println("NÅ BØR ROTATE RIGHT SKJE");
        animation.getImageView().setRotate(0);
        //isIdle = 0;
        //direction = 0;
        //animation.setOffsetY();
    }

    public void moveLeft(){
        animation.setOffsetX(192);
        System.out.println("NÅ BØR ROTATE LEFT SKJE");
        animation.getImageView().setRotate(180);
        //isIdle = 0;
        //direction = 1;
    }

    public void animationIDLE(){
        animation.setOffsetX(128);
    }

    public void tick(){
        if (right){
            setX(x + MOV_SPEED);
            setDirection(0);
            moveRight();
        }
        if (left){
            setX(x - MOV_SPEED);
            setDirection(1);
            moveLeft();
        }
        if (up){
            setY(y - MOV_SPEED);
        }
        if (down){
            setY(y + MOV_SPEED);
        }
        if (right || left || up || down){
            setIdle(0);
            Packet12Move packet = new Packet12Move(this.getUsername(), this.x,this.y, this.direction, this.isIdle);
            //Må sendes til socketClient
            packet.writeData(FXMLController.controller.getSocketClient());
        }else {
            if (isIdle == 0) {
                isIdle = 1;
                animationIDLE();
                Packet12Move packet = new Packet12Move(this.getUsername(), this.x,this.y, this.direction, this.isIdle);
                //Må sendes til socketClient
                packet.writeData(FXMLController.controller.getSocketClient());
            }
        }
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
        //setDirectionAnimation();
        PlatformHelper.run(() -> playerBox.setLayoutX(x));
    }

    public void setY(int y) {
        this.y = y;
        //setDirectionAnimation();
        PlatformHelper.run(() -> playerBox.setLayoutY(y));
    }

    public void remove(GameController gameController) {
        PlatformHelper.run(() -> gameController.getAnchorPane().getChildren().removeAll(playerBox));
    }

    public void setDirectionAnimation(){
        if (this.direction == 1)
            this.moveLeft();
        if (this.direction == 0)
            this.moveRight();
        if (isIdle == 1){
            animationIDLE();
        }
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setSpeed(Number t1) {
        this.MOV_SPEED = (int)Math.floor((double)t1);
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setIdle(int idle) {
        this.isIdle = idle;
    }

    public int getIsIdle(){
        return isIdle;
    }

    public void setMainPlayerUnderline(boolean underline){
        this.usernameLabel.setUnderline(underline);
    }

}
