package org.todss.client.test.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.todss.client.test.model.Client;
import org.todss.client.test.model.Preset;

import java.net.URL;
import java.util.ResourceBundle;

public class PresetEditController implements Initializable {
    @FXML
    private TextField nameTextField;

    @FXML
    private Button saveButton;

    private Client client = Client.getInstance();
    private Preset preset;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preset = MainController.mainController.getSelectedPreset();

        nameTextField.setText(preset.getName());

        saveButton.setOnAction((event -> {
            preset.setName(nameTextField.getText());

            Stage stage = (Stage) nameTextField.getScene().getWindow();
            stage.close();
        }));
    }
}
