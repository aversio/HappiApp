package org.todss.client.test.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.todss.algorithm.Algorithm;
import org.todss.algorithm.AlgorithmContext;
import org.todss.algorithm.impl.BruteAlgorithm;
import org.todss.algorithm.impl.SmartAlgorithm;
import org.todss.algorithm.model.Alarm;
import org.todss.algorithm.model.Frequency;
import org.todss.algorithm.model.Intake;
import org.todss.algorithm.model.Travel;
import org.todss.client.test.model.Client;
import org.todss.client.test.model.FXTravel;
import org.todss.client.test.model.Preset;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private ComboBox<Preset> presetComboBox;

    @FXML
    private Button presetEditButton;

    @FXML
    private Button presetDeleteButton;

    @FXML
    private Button presetNewButton;

    @FXML
    private VBox presetContextVBox;

    @FXML
    private ComboBox<String> travelDepartureTimeZoneComboBox;

    @FXML
    private ComboBox<String> travelArrivalTimeZoneComboBox;

    @FXML
    private CheckBox alarmOneActiveCheckBox;

    @FXML
    private TextField alarmOneTimeTextField;

    @FXML
    private CheckBox alarmTwoActiveCheckBox;

    @FXML
    private TextField alarmTwoTimeTextField;

    @FXML
    private ChoiceBox<Frequency> alarmFrequencyChoiceBox;

    @FXML
    private TextField alarmMarginTextField;

    @FXML
    private Button travelNewButton;

    @FXML
    private ListView<FXTravel> travelsListView;

    @FXML
    private VBox travelContentVBox;

    @FXML
    private TextField travelDepartureTimeTextField;

    @FXML
    private TextField travelArrivalTimeTextField;

    @FXML
    private DatePicker travelDepartureDatePicker;

    @FXML
    private DatePicker travelArrivalDatePicker;

    @FXML
    private CheckBox travelActiveCheckBox;

    @FXML
    private Button travelDeleteButton;

    @FXML
    private TreeTableView<Intake> intakesTreeTableView;

    @FXML
    private TreeTableColumn<Intake, String> intakeDateTreeTableColumn;

    @FXML
    private TreeTableColumn<Intake, String> intakeTimeTreeTableColumn;

    @FXML
    private TreeTableColumn<Intake, String> intakeTimeZoneTreeTableColumn;

    @FXML
    private ChoiceBox<Algorithm> algorithmChoiceBox;

    @FXML
    private Button algorithmRawOutputButton;
    private List<Intake> intakes = new ArrayList<>();

    public static MainController mainController;
    private Client client = Client.getInstance();
    private List<Algorithm> algorithms = new ArrayList<>();
    private boolean noSave = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithms.add(new BruteAlgorithm());
        algorithms.add(new SmartAlgorithm());

        algorithmChoiceBox.getItems().addAll(algorithms);
        algorithmChoiceBox.getSelectionModel().selectFirst();

        algorithmChoiceBox.setConverter(new StringConverter<Algorithm>() {
            @Override
            public String toString(Algorithm object) {
                return object.name();
            }

            @Override
            public Algorithm fromString(String string) {
                return null;
            }
        });

        algorithmChoiceBox.setOnAction(event -> savePreset());

        intakesTreeTableView.setShowRoot(false);

        intakeDateTreeTableColumn.setCellFactory(new Callback<TreeTableColumn<Intake, String>, TreeTableCell<Intake, String>>() {
            @Override
            public TreeTableCell<Intake, String> call(TreeTableColumn<Intake, String> param) {
                return new TreeTableCell<Intake, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        Intake intake = getTreeTableRow().getItem();

                        if (empty || intake == null) {
                            setText(null);
                        } else {
                            setText(intake.getDate().format(DateTimeFormatter.ofPattern("d-M-yyyy")));
                        }
                    }
                };
            }
        });

        intakeTimeTreeTableColumn.setCellFactory(new Callback<TreeTableColumn<Intake, String>, TreeTableCell<Intake, String>>() {
            @Override
            public TreeTableCell<Intake, String> call(TreeTableColumn<Intake, String> param) {
                return new TreeTableCell<Intake, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        Intake intake = getTreeTableRow().getItem();

                        if (empty || intake == null) {
                            setText(null);
                        } else {
                            setText(intake.getDate().format(DateTimeFormatter.ofPattern("H:mm")));
                        }
                    }
                };
            }
        });

        intakeTimeZoneTreeTableColumn.setCellFactory(new Callback<TreeTableColumn<Intake, String>, TreeTableCell<Intake, String>>() {
            @Override
            public TreeTableCell<Intake, String> call(TreeTableColumn<Intake, String> param) {
                return new TreeTableCell<Intake, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        Intake intake = getTreeTableRow().getItem();

                        if (empty || intake == null) {
                            setText(null);
                        } else {
                            setText(intake.getDate().getZone().toString());
                        }
                    }
                };
            }
        });

        String[] timeZones = ZoneId.getAvailableZoneIds().toArray(new String[0]);
        Arrays.sort(timeZones);

        travelDepartureTimeZoneComboBox.setItems(
                FXCollections.observableArrayList(timeZones)
        );
        travelArrivalTimeZoneComboBox.setItems(
                FXCollections.observableArrayList(timeZones)
        );

        presetComboBox.setOnAction(event -> {
            Preset selectedPreset = presetComboBox.getSelectionModel().getSelectedItem();

            client.setSelectedPreset(selectedPreset);

            presetContextVBox.setDisable((selectedPreset == null));
            presetEditButton.setDisable((selectedPreset == null));
            presetDeleteButton.setDisable((selectedPreset == null));

            if (selectedPreset != null) {
                travelsListView.itemsProperty().bind(
                        Bindings.createObjectBinding(() -> getSelectedPreset().getTravelList(), getSelectedPreset().getTravels())
                );

                travelsListView.getSelectionModel().selectFirst();
            }
        });

        presetDeleteButton.setOnAction(event -> client.removePreset(getSelectedPreset()));

        presetNewButton.setOnAction((event) -> {
            try {
                Parent root = FXMLLoader.load(new URL("file:resources/scenes/preset-new.fxml"));
                Scene scene = new Scene(root);

                Stage stage = new Stage();
                stage.setTitle("New preset");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setMaximized(false);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        presetEditButton.setOnAction((event) -> {
            try {
                if (getSelectedPreset() == null)
                    return;

                Parent root = FXMLLoader.load(new URL("file:resources/scenes/preset-edit.fxml"));
                Scene scene = new Scene(root);

                Stage stage = new Stage();
                stage.setTitle("Edit preset");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setMaximized(false);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        presetComboBox.itemsProperty().bindBidirectional(
                new SimpleListProperty<>(client.getPresets())
        );

        alarmOneActiveCheckBox.setOnAction(event -> savePreset());
        alarmTwoActiveCheckBox.setOnAction(event -> savePreset());

        alarmOneTimeTextField.setOnKeyReleased(event -> timeChanged(alarmOneTimeTextField));
        alarmTwoTimeTextField.setOnKeyReleased(event -> timeChanged(alarmTwoTimeTextField));
        alarmMarginTextField.setOnKeyReleased(event -> marginChanged(alarmMarginTextField));

        alarmFrequencyChoiceBox.getItems().addAll(Frequency.values());
        alarmFrequencyChoiceBox.getSelectionModel().select(Frequency.DAY);

        travelNewButton.setOnAction(event -> {
            ZonedDateTime departure = ZonedDateTime.now(ZoneId.systemDefault());
            ZonedDateTime arrival = ZonedDateTime.now(ZoneId.systemDefault()).plusHours(18);
            FXTravel travel = new FXTravel(departure, arrival);

            client.getSelectedPreset().addTravel(travel, true);

            travelsListView.getSelectionModel().select(travel);
        });

        travelsListView.setCellFactory(param -> new ListCell<FXTravel>() {
            @Override
            protected void updateItem(FXTravel travel, boolean empty) {
                super.updateItem(travel, empty);

                if (travel != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy H:mm VV");

                    boolean active = getSelectedPreset().isTravelActive(travel);
                    String departure = travel.getDeparture().format(formatter);
                    String arrival = travel.getArrival().format(formatter);

                    Text text = new Text((!active ? "\t" : "") + departure + " --> " + arrival);
                    if (!active)
                        text.setFill(Color.gray(.6));

                    setGraphic(text);
                } else {
                    setGraphic(null);
                }
            }
        });

        travelsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        	noSave = true;

        	FXTravel travel = newValue;

            if (travel != null) {
                travelActiveCheckBox.setSelected(getSelectedPreset().isTravelActive(travel));

                if (travel.getDeparture() != null) {
                    travelDepartureTimeTextField.setText(travel.getDeparture().format(DateTimeFormatter.ofPattern("k:mm")));
                    travelDepartureDatePicker.setValue(travel.getDeparture().toLocalDate());
                    travelDepartureTimeZoneComboBox.getSelectionModel().select(travel.getDeparture().getZone().toString());
                }

                if (travel.getArrival() != null) {
                    travelArrivalTimeTextField.setText(travel.getArrival().format(DateTimeFormatter.ofPattern("k:mm")));
                    travelArrivalDatePicker.setValue(travel.getArrival().toLocalDate());
					travelArrivalTimeZoneComboBox.getSelectionModel().select(travel.getArrival().getZone().toString());
                }
            } else {
                travelDepartureTimeTextField.clear();
                travelDepartureDatePicker.setValue(null);
                travelDepartureTimeZoneComboBox.setValue(null);

                travelArrivalTimeTextField.clear();
                travelArrivalDatePicker.setValue(null);
                travelArrivalTimeZoneComboBox.setValue(null);

                travelActiveCheckBox.setSelected(false);
            }

            travelContentVBox.setDisable(travel == null);

			noSave = false;
			savePreset();
        });

        travelDepartureTimeTextField.setOnKeyReleased(event -> {
            timeChanged(travelDepartureTimeTextField);
            savePreset();
        });
        travelArrivalTimeTextField.setOnKeyReleased(event -> {
            timeChanged(travelArrivalTimeTextField);
            savePreset();
        });
        travelDepartureDatePicker.setOnAction(event -> savePreset());
        travelArrivalDatePicker.setOnAction(event -> savePreset());
        travelDepartureTimeZoneComboBox.setOnAction(event -> savePreset());
        travelArrivalTimeZoneComboBox.setOnAction(event -> savePreset() );

        travelActiveCheckBox.setOnAction(event -> savePreset());

        travelDeleteButton.setOnAction(event -> {
            FXTravel travel = travelsListView.getSelectionModel().getSelectedItem();

            if (travelsListView.getSelectionModel().getSelectedIndex() != 0)
                travelsListView.getSelectionModel().selectPrevious();
            else
                travelsListView.getSelectionModel().selectNext();

            getSelectedPreset().removeTravel(travel);
        });

        algorithmRawOutputButton.setOnAction(event -> {
            TextArea textArea = new TextArea();
            textArea.setText(rawIntakes(intakes));

            Stage dialog = new Stage();
            Scene scene = new Scene(textArea);

            dialog.setScene(scene);
            dialog.show();
        });
    }

    void selectPreset(Preset preset) {
        presetComboBox.getSelectionModel().select(preset);
    }

    Preset getSelectedPreset() {
        return presetComboBox.getSelectionModel().getSelectedItem();
    }

    private void timeChanged(TextField textField) {
        if (validateTime(textField.getText())) {
            textField.getStyleClass().remove("invalid");

            savePreset();
        } else if (!textField.getStyleClass().contains("invalid"))
            textField.getStyleClass().add("invalid");
    }

    private void marginChanged(TextField textField) {
        if (validateMargin(textField.getText())) {
            textField.getStyleClass().remove("invalid");

            savePreset();
        } else if (!textField.getStyleClass().contains("invalid"))
            textField.getStyleClass().add("invalid");
    }

    private boolean validateTime(String time) {
        try {
            String[] timeSplit = time.split(":", 2);
            int hours = Integer.parseInt(timeSplit[0]);
            int minutes = Integer.parseInt(timeSplit[1]);

            return !(hours > 24
                    || hours < 0
                    || minutes > 59
                    || minutes < 0
                    || (hours == 24 && minutes > 0));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateMargin(String text) {
        try {
            int margin = Integer.parseInt(text);

            return margin > 0 && margin < 5;
        } catch (Exception e) {
            return false;
        }
    }

    private void saveAlarms() {
        try {
            saveAlarm(
                    0,
                    alarmOneActiveCheckBox.isSelected(),
                    alarmOneTimeTextField.getText()
            );

            saveAlarm(
                    1,
                    alarmTwoActiveCheckBox.isSelected(),
                    alarmTwoTimeTextField.getText()
            );
        } catch (Exception e) {
            System.out.println("Exception while saving alarms");
        }
    }

    private LocalTime getTime(String text) {
        String[] timeSplit = text.split(":", 2);
        int hours = Integer.parseInt(timeSplit[0]);
        int minutes = Integer.parseInt(timeSplit[1]);

        return LocalTime.of(hours, minutes);
    }

    private void saveAlarm(int alarmNumber, boolean status, String time) throws Exception {
        String[] timeSplit = time.split(":", 2);
        int hours = Integer.parseInt(timeSplit[0]);
        int minutes = Integer.parseInt(timeSplit[1]);

        Frequency frequency = alarmFrequencyChoiceBox.getValue();
        ZonedDateTime date = ZonedDateTime.now().withHour(hours).withMinute(minutes).withSecond(0).withNano(0);
//        int margin = Integer.parseInt(alarmMarginTextField.getText()) * 60;

        Alarm alarm = new Alarm(frequency, date);

        client.getSelectedPreset().setAlarm(alarmNumber, alarm, status);
    }

    private void saveTravels() {
		FXTravel travel = travelsListView.getSelectionModel().getSelectedItem();

		if (travel != null
        		&& travelDepartureDatePicker.getValue() != null
                && !travelDepartureTimeTextField.getText().isEmpty()
                && validateTime(travelDepartureTimeTextField.getText())
                && travelDepartureTimeZoneComboBox.getValue() != null
                && !travelDepartureTimeZoneComboBox.getValue().isEmpty()
                && travelArrivalDatePicker.getValue() != null
                && !travelArrivalTimeTextField.getText().isEmpty()
                && validateTime(travelArrivalTimeTextField.getText())
                && travelArrivalTimeZoneComboBox.getValue() != null
                && !travelArrivalTimeZoneComboBox.getValue().isEmpty()
                ) {
            travel.setTravel(
                    ZonedDateTime.of(
                            travelDepartureDatePicker.getValue(),
                            getTime(travelDepartureTimeTextField.getText()),
                            ZoneId.of(travelDepartureTimeZoneComboBox.getValue())
                    ),
                    ZonedDateTime.of(
                            travelArrivalDatePicker.getValue(),
                            getTime(travelArrivalTimeTextField.getText()),
                            ZoneId.of(travelArrivalTimeZoneComboBox.getValue())
                    )
            );

            getSelectedPreset().setTravelActive(travel, travelActiveCheckBox.isSelected());
        } else {
			System.out.println("Invalid travel");
		}
	}

    private void savePreset() {
    	if (!noSave) {
			saveAlarms();
			saveTravels();
		}

		if (travelsListView.getSelectionModel().getSelectedItem() != null) {
			intakesTreeTableView.setPlaceholder(new Text("Loading..."));

			new Thread(this::showIntakes).start();
		}
    }

    private void showIntakes() {
        Alarm alarm = getSelectedPreset().getAlarms()[0];
        List<Travel> travels = new ArrayList<>();
		ObservableList<FXTravel> fxTravels = getSelectedPreset().getTravelList();
        for(FXTravel travel : fxTravels) {
            travels.add(travel.toTravel());
        }

        Algorithm algorithm = algorithmChoiceBox.getValue();

        if (algorithm instanceof BruteAlgorithm) {
        	travels.clear();
        	travels.add(travelsListView.getSelectionModel().getSelectedItem().toTravel());
		}

        try {
			this.intakes = algorithm.run(new AlgorithmContext(alarm, travels));
		} catch(Exception e) {
        	Platform.runLater(() -> {
				intakesTreeTableView.setPlaceholder(new Text("An unknown error has occurred!"));
			});
		}

        TreeItem<Intake> root = new TreeItem<>();
        if (intakes != null && !intakes.isEmpty()) {
            for (Intake intake : intakes) {
                root.getChildren().add(new TreeItem<>(intake));
            }
        } else {
			System.out.println("Empty intakes");
		}

        Platform.runLater(() -> {
            intakesTreeTableView.setRoot(root);

            if (intakes == null || !intakes.isEmpty())
                intakesTreeTableView.setPlaceholder(new Text("No solution :("));
        });
    }

    private String rawIntakes(List<Intake> intakes) {
        StringBuilder output = new StringBuilder();

        if (intakes == null || intakes.isEmpty())
        	return output.toString();

        Intake prevIntake = null;
        for (int i = 0; i < intakes.size(); i++) {
            Intake intake = intakes.get(i);

            if (prevIntake != null) {
                Duration difference = Duration.between(prevIntake.getDate(), intake.getDate());

                output.append(
                        String.format("\t+%s", difference.toHours()) + "\n"
                );
            }

            output.append(String.format("[%d] %s", i, intake.getDate()) + "\n");

            prevIntake = intake;
        }

        return output.toString();
    }
}
