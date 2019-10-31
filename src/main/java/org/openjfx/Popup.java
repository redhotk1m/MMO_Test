package org.openjfx;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openjfx.PlatformHelper;

import java.io.IOException;

public class Popup {
    @FXML
    Label messageLabel;
    @FXML
    Button exitButton;
    private String message;
    double x, y;
    public Popup(String message, double x, double y) {
        this.message = message;
        this.x = x;
        this.y = y;
        PlatformHelper.run(() -> createPopup(false));
    }

    public Popup(String message, boolean shutdownProgram, double x, double y){
        this.x = x;
        this.y = y;
        this.message = message;
        PlatformHelper.run(() -> createPopup(shutdownProgram));
    }

    public Popup(String message) {
        this.message = message;
        this.x = x;
        this.y = y;
        PlatformHelper.run(() -> createPopup(false));
    }

    public Popup(String message, boolean shutdownProgram){
        this.x = x;
        this.y = y;
        this.message = message;
        PlatformHelper.run(() -> createPopup(shutdownProgram));
    }

    private void createPopup(boolean shutdownProgram){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("wrongPassword.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            //stage.setX(x);
            //stage.setY(y);
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(Event::consume);
            stage.setScene(scene);
            Label a = (Label)scene.lookup("#message");
            if (shutdownProgram) {
                Button b = (Button) scene.lookup("#exitButton");
                b.setOnAction(ActionEvent -> System.exit(0));
            }
            a.setText(message);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
