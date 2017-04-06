package org.todss;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

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
	private DatePicker arrivalPicker;

	@FXML
	private ListView listView;

	@FXML
	private GridPane grid;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		FlowPane pane = (FlowPane) grid.getChildren().get(0 * 8 + 2);
		System.out.println(pane.getStyleClass());
	}

	public Node getNode(int row, int column) {
		Node result = null;
		ObservableList<Node> childrens = grid.getChildren();
		for (Node node : childrens) {
			if(grid.getRowIndex(node) == row && grid.getColumnIndex(node) == column) {
				result = node;
				break;
			}
		}

		return result;
	}

	/**
	 * Set the main stage of this application.
	 * @param stage The stage to set.
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

}
