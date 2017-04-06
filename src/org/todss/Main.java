package org.todss;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

	/**
	 * The controller of this application.
	 */
	private static MainController controller;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		final FXMLLoader loader = new FXMLLoader(new URL("file:data/design/main.fxml"));
		final BorderPane pane = loader.load();
		controller = loader.getController();
		controller.setStage(stage);
		pane.getStylesheets().add("file:data/css/style.css");
		stage.setScene(new Scene(pane, pane.getPrefWidth(), pane.getPrefHeight(), true));
		stage.show();
	}

}
