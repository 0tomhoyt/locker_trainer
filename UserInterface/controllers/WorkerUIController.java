package UserInterface.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WorkerUIController implements Initializable {
    private List<Label> labels = new ArrayList<>(50);
    @FXML
    private GridPane gridPane;
    @FXML
    private RadioButton const_time_btn;
    @FXML RadioButton var_time_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for(int i=0;i<5;i++){
            for(int j=0;j<10;j++){
                Label label = new Label("OFF");
                labels.add(label);
                gridPane.add(label,i,j);
            }
        }

        ToggleGroup toggleGroup = new ToggleGroup();
        const_time_btn.setToggleGroup(toggleGroup);
        var_time_btn.setToggleGroup(toggleGroup);
        const_time_btn.setSelected(true);
    }
}
