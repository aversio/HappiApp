package org.todss;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.todss.model.Alarm;
import org.todss.model.Frequency;
import org.todss.model.Travel;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController implements Initializable {

	/**
	 * The stage of this application.
	 */
	private Stage stage;

	@FXML
	private BorderPane mainPane;

	@FXML
	private DatePicker departurePicker;

	@FXML
	private TextField departureTime;

	@FXML
	private TextField arrivalTime;

	@FXML
	private DatePicker arrivalPicker;

	@FXML
	private ListView<String> listView;

	@FXML
	private GridPane grid;

	public static Alarm ALARM = new Alarm(Frequency.DAY, ZonedDateTime.parse("2017-04-07T08:00+02:00", DateTimeFormatter.ISO_DATE_TIME));

	private static final int OFFSET = 8;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/*FlowPane pane = (FlowPane) grid.getChildren().get(0 * 8 + 2);
		System.out.println(pane.getStyleClass());*/
		for(int i = 0 ; i < OFFSET; i++) {
			//GridPane.setColumnIndex(grid.getChildren().get(i), i + 1);
			GridPane.setRowIndex(grid.getChildren().get(i), 0);
		}
		ObservableList<String> list = FXCollections.observableArrayList(ZoneOffset.getAvailableZoneIds());
		list.sort(null);
		listView.setItems(list);
		listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			checkFlight();
		});
		departurePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			checkFlight();
		});
		departureTime.textProperty().addListener(((observable, oldValue, newValue) -> {
			checkFlight();
		}));
		arrivalPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			checkFlight();
		});
		arrivalTime.textProperty().addListener(((observable, oldValue, newValue) -> {
			checkFlight();
		}));
		LocalDateTime departure = LocalDate.now().atTime(12, 10);
		LocalDateTime arrival = departure.plusHours(6);
		departurePicker.setValue(departure.toLocalDate());
		arrivalPicker.setValue(arrival.toLocalDate());

		departureTime.setText(departure.getHour() + ":" + departure.getMinute());
		arrivalTime.setText(arrival.getHour() + ":" + arrival.getMinute());
		generate(null, null);
		//listView.getSelectionModel().select("Australia/West");
		listView.getSelectionModel().select("America/Atka");
	}

	private void checkFlight() {
		if (departurePicker.getValue() == null || departureTime.getText().isEmpty() || arrivalPicker.getValue() == null
				|| arrivalTime.getText().isEmpty() || listView.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		ZoneId zone = ZoneId.of(listView.getSelectionModel().getSelectedItem());
		ZonedDateTime departure = ZonedDateTime.of(departurePicker.getValue().atTime(LocalTime.parse(departureTime.getText())), zone);
		ZonedDateTime arrival = ZonedDateTime.of(arrivalPicker.getValue().atTime(LocalTime.parse(arrivalTime.getText())), zone);
		generate(arrival, zone);
	}

	public void generate(ZonedDateTime from, ZoneId zoneId) {
		List<ZonedDateTime> dates = ALARM.getScheme(7, null, from, zoneId);
		for(int i = 1; i < OFFSET; i++) {
			for (int i2 = 1; i2 < OFFSET; i2++) {
				Node node = getNode(i, i2);
				if (node != null) {
					grid.getChildren().remove(node);
				}
			}
		}
		for(int i = 1; i < OFFSET; i++) {
			for(int i2 = 1; i2 < OFFSET; i2++) {
				FlowPane pane = new FlowPane();
				ZonedDateTime date = dates.get(i * 7 + i2 - OFFSET);
				Label label = new Label(date.format(DateTimeFormatter.ofPattern("H:mm")) + "\n" + date.format(DateTimeFormatter.ofPattern("dd-MM")));
				pane.getChildren().add(label);
				grid.add(pane, i2, i);
				pane.setUserData("Object[row=" + i + ", column=" + i2 + "]");
			}
		}
	}

	private FlowPane getNode(int row, int column) {
		for (Node node : grid.getChildren()) {
			if (node.getUserData() == null) {
				continue;
			}
			if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
				return (FlowPane) node;
			}
		}
		return null;
	}

	/**
	 * Set the main stage of this application.
	 * @param stage The stage to set.
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

}
