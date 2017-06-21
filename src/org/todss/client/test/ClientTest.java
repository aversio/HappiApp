package org.todss.client.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.todss.client.test.controller.MainController;

import java.io.IOException;
import java.net.URL;

public class ClientTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(new URL("file:resources/scenes/main.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        MainController.mainController = loader.getController();

        stage.setTitle("Happi test application");
        stage.setScene(scene);
        stage.show();
    }
}
