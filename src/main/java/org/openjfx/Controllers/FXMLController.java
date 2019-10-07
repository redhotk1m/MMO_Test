package org.openjfx.Controllers;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.*;
import org.openjfx.Exceptions.CannotFindHostException;
import org.openjfx.Packets.Packet01Disconnect;
import org.openjfx.Packets.Packet03ValidationLogin;

import java.io.IOException;

public class FXMLController {

    @FXML
    private Label label;
    @FXML
    private Button clientButton, serverButton;
    public static String usernameText, passwordText;
    public static FXMLController controller;
    private GameClient socketClient;
    private GameServer socketServer;

    String navn = "Client 2";
    @FXML
    private void logIn(ActionEvent event) {
        if (!checkValidUsernamePass()){
            new Popup("No valid username / password");
        }
        else {
            launchClient();
            //PlayerMP player = new PlayerMP(usernameTextField.getText(), null, -1, 500, 10);
            Packet03ValidationLogin packet03ValidationLogin = new Packet03ValidationLogin(usernameTextField.getText(), passwordField.getText());
            //Nå må vi motta godkjennelse fra serveren før vi fortsetter her
            packet03ValidationLogin.writeData(socketClient);
            try {
                socketClient.loginAccepted();
            } catch (CannotFindHostException e) {
                new Popup("Cannot connect to server");
            }
            /*try {
                if (socketClient.loginAccepted()) {
                    socketClient.start();
                    //Ny tråd som venter på packets
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    GameController gameController = gotoGame(stage);
                    gameController.transferMessage(socketClient);
                    //gameController.transferMessage(player, socketServer, socketClient);
                }else {
                    new Popup("Wrong username / password");
                }
            } catch (CannotFindHostException e) {
                new Popup("Cannot connect to server");
            }*/
        }
    }

    private GameController gotoGame(Stage stage){
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

    private boolean checkValidUsernamePass(){
        usernameText = usernameTextField.getText();
        passwordText = passwordField.getText();
        return ((usernameText != null && usernameText.length() > 0) && (passwordText != null && passwordText.length() > 0));
    }

    @FXML
    private void exit(ActionEvent event) { //Blir tilkalt ved exit knappen, navn her fungerer ikke. Bør være finne ID til personen innlogget, og sende den packeten istedenfor.
        Packet01Disconnect packet = new Packet01Disconnect(navn);
        packet.writeData(this.socketClient);
    }

    private void launchClient(){
        label.setText("Client");
        socketClient = new GameClient(this,"localhost");
    }

    @FXML
    private void launchServer(ActionEvent event) {
        label.setText("Server");
        socketServer = new GameServer(this);
        socketServer.start();
    }

    @FXML
    TextField usernameTextField;
    @FXML
    PasswordField passwordField;
    //public static FXMLController controller;
    public void initialize() {
        System.out.println("init method in controller");
        controller = this;
    }

    AnimationTimer a = new AnimationTimer() {
        @Override
        public void handle(long l) {
            usernameText = usernameTextField.getText();
            System.out.println(usernameTextField.getText());
            //System.out.println(ball2.getCenterX());
        }
    };

    public GameClient getSocketClient() {
        return socketClient;
    }

    public GameServer getSocketServer() {
        return socketServer;
    }

    @FXML
    public void createAccount(ActionEvent event){
        System.out.println("Lager ny bruker nå med brukernavn: " + usernameTextField.getText() + " og passord: " + passwordField.getText());
        SQLite sqLite = new SQLite();
        sqLite.addUser(usernameTextField.getText(), passwordField.getText());
    }
    @FXML
    public void displayUsers(ActionEvent event){
        System.out.println("DISPLAYING ALL USERS!");
        SQLite sqLite = new SQLite();
        sqLite.displayUsers();
    }

}
