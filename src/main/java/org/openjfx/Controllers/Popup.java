package org.openjfx.Controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Popup {
    @FXML
    Label message;

    public Popup(String message) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("../wrongPassword.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(Event::consume);
            stage.setScene(scene);
            Label a = (Label)scene.lookup("#message");
            System.out.println(message);
            a.setText(message);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
