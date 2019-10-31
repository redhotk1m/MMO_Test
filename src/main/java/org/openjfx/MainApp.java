package org.openjfx;

import javafx.application.Application;
import static javafx.application.Application.launch;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {
    static boolean f;
    static boolean b;
    @Override
    public void start(Stage stage) throws Exception {

        /*SQLite a = new SQLite();
        ResultSet rs;

        rs = a.displayUsers();

        while (rs.next()){
            System.out.println(rs.getString("uname") + " " + rs.getString("passwd"));
        }


        boolean at = a.getUser("Kim", "Thorsen");
        boolean bt = a.getUser("Roger", "Thorsen");
        System.out.println(at + " " + bt);*/

        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
