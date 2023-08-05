package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import models.TrainingHistory;
import models.Worker;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WorkerUIController implements Initializable {
    private List<Label> labels = new ArrayList<>(50);
    private Timeline timer;
    private int seconds;
    private Worker worker;
    private TrainingHistory trainingHistory;    //本次的记录
    private List<TrainingHistory> trainingHistories;    //所有的历史记录
    @FXML
    private GridPane gridPane;
    @FXML
    private VBox vBox;
    @FXML
    private RadioButton const_time_btn;
    @FXML
    private RadioButton var_time_btn;
    @FXML
    private Label time_label;
    @FXML
    private RadioButton btn_all;
    @FXML
    private RadioButton btn_train;
    @FXML
    private RadioButton btn_match;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);

        //测试添加Label到gridPane
        for(int i=0;i<5;i++){
            for(int j=0;j<10;j++){
                Label label = new Label("OFF");
                labels.add(label);
                gridPane.add(label,i,j);
            }
        }

        //设置ToggleGroup
        ToggleGroup toggleGroup_tab1 = new ToggleGroup();
        const_time_btn.setToggleGroup(toggleGroup_tab1);
        var_time_btn.setToggleGroup(toggleGroup_tab1);
        const_time_btn.setSelected(true);

        ToggleGroup toggleGroup_tab2 = new ToggleGroup();
        btn_all.setToggleGroup(toggleGroup_tab2);
        btn_train.setToggleGroup(toggleGroup_tab2);
        btn_match.setToggleGroup(toggleGroup_tab2);
        btn_all.setSelected(true);

        //设置定时器
        seconds = 0;
        timer = new Timeline(new KeyFrame(Duration.seconds(1),actionEvent -> {
            seconds++;
            time_label.setText(seconds+"s");
        }));
        timer.setCycleCount(Animation.INDEFINITE);

        //测试添加history_card到训练历史
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/history_card.fxml"));
            Pane pane = fxmlLoader.load();
            vBox.getChildren().add(pane);
            HistoryCardController historyCardController = fxmlLoader.getController();
            historyCardController.setInfo("12:00","49","5:00","1","2");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btn_start(ActionEvent event) {
        timer.play();
    }

    public void setWorker(Worker worker){
        this.worker = worker;
        getTrainingHistories();
    }

    public boolean getTrainingHistories(){
        //在这里获取trainingHistories
        //并且设置训练记录界面
        return true;
    }

    public boolean startTraining(){
        //在这里初始化trainingHistory
        return true;
    }

    public boolean updateTraining(){
        return true;
    }

    public boolean endTraining(){
        return true;
    }
}