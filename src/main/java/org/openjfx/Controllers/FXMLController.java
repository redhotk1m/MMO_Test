package org.openjfx.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.openjfx.Packets.Packet11Disconnect;
import org.openjfx.Popup;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class FXMLController {
    private final String serverHost = "localhost"; //IP Of server host, change to public IP.  Remember to open up port
    @FXML
    private Label label;
    @FXML
    private Button clientButton, serverButton;
    @FXML
    TextField usernameTextField;
    @FXML
    PasswordField passwordField;
    @FXML
    AnchorPane mainAnchorpane;
    public static String usernameText, passwordText;
    public static FXMLController controller;
    private GameClient socketClient;
    private GameServer socketServer;
    double x, y;
    double finalX, finalY;
    public void initialize() {
        controller = this;
        launchClient();
    }
    @FXML
    private void logIn(ActionEvent event) {
        x = mainAnchorpane.localToScreen(mainAnchorpane.getLayoutBounds()).getCenterX();
        y = mainAnchorpane.localToScreen(mainAnchorpane.getLayoutBounds()).getCenterY();
        finalX = x - (mainAnchorpane.getWidth()/2);
        finalY = y - (mainAnchorpane.getHeight()/2);
        if (!checkValidUsernamePass()){
            new Popup("No valid username / password",finalX,finalY);
        }
        else {
            socketClient.login(usernameTextField.getText(), passwordField.getText());
        }
    }

    private boolean checkValidUsernamePass(){
        usernameText = usernameTextField.getText();
        passwordText = passwordField.getText();
        return ((usernameText != null && usernameText.length() > 0) && (passwordText != null && passwordText.length() > 0));
    }

    private void launchClient(){
        label.setText("Client");
        try {
            socketClient = new GameClient(this,serverHost);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void launchServer(ActionEvent event) {
        socketClient.serverIsAlive = false;
        socketClient = null;
        label.setText("Server");
        //clientButton.setDisable(true);
        serverButton.setDisable(true);
        socketServer = new GameServer(this);
        socketServer.start();
    }

    @FXML
    public void createAccount(ActionEvent event){
        if (!checkValidUsernamePass()){
            new Popup("No valid username / password");
        } else {
            if (socketClient != null)
                socketClient.createAccount(usernameTextField.getText(), passwordField.getText());
            else {
                SQLite sqLite = new SQLite();
                sqLite.addUser(usernameTextField.getText(), passwordField.getText());
            }
        }
    }

    @FXML
    public void displayUsers(ActionEvent event){
        System.out.println("DISPLAYING ALL USERS!");
        SQLite sqLite = new SQLite();
        sqLite.displayUsers();
    }

    public GameClient getSocketClient() {
        return socketClient;
    }

    public GameServer getSocketServer() {
        return socketServer;
    }

}
