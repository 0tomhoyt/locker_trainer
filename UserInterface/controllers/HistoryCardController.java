package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class HistoryCardController implements Initializable, Controller {
    @FXML
    private Label score_label;
    @FXML
    private Label total_time_label;
    @FXML
    private Label unlocked_label;
    @FXML
    private Label difficulty_label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);
    }

    public void setInfo(int score, int total_time, int unlocked, int difficulty){
        score_label.setText("分数："+score);
        total_time_label.setText("总用时："+total_time);
        unlocked_label.setText("未解锁数："+unlocked);
        difficulty_label.setText("难度："+difficulty);
    }
}
