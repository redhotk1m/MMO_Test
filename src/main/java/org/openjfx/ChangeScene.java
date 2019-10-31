package org.openjfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openjfx.Controllers.GameController;

import java.io.IOException;

public class ChangeScene<T> {

    public T t;
    String path;

    public ChangeScene(Stage stage, String path) {
        this.path = path;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("GAME");
            stage.show();
            //PlatformHelper.run(() -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.t = loader.getController();
    }

    public T getController(){
        return this.t;
    }
}
