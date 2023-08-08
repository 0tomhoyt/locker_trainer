package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class HistoryCardController implements Initializable, Controller {
    @FXML
    private Label time_label;
    @FXML
    private Label score_label;
    @FXML
    private Label total_time_label;
    @FXML
    private Label unlocked_label;
    @FXML
    private Label operator_label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);
    }

    public void setInfo(String time, String score, String total_time, String unlocked, String operator){
        time_label.setText(time);
        score_label.setText(score);
        total_time_label.setText(total_time);
        unlocked_label.setText(unlocked);
        operator_label.setText(operator);
    }
}
