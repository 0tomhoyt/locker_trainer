package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import models.Lock;
import models.LockStatus;
import models.TrainingHistory;
import models.Worker;
import socketClient.SocketClient;
import util.ConstantTimeStrategy;
import util.TimerStrategy;
import util.VariableTimeStrategy;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WorkerUIController implements Initializable, Controller {
    private List<Lock> locks = new ArrayList<>(50);
    private List<Label> labels = new ArrayList<>(50);
    private Timeline timer;
    private int seconds;
    private TimerStrategy timerStrategy;
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
    @FXML
    private Spinner time_spinner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);

        //设置ToggleGroup
        ToggleGroup toggleGroup_tab1 = new ToggleGroup();
        var_time_btn.setToggleGroup(toggleGroup_tab1);
        const_time_btn.setToggleGroup(toggleGroup_tab1);
        var_time_btn.setSelected(true);

        ToggleGroup toggleGroup_tab2 = new ToggleGroup();
        btn_all.setToggleGroup(toggleGroup_tab2);
        btn_train.setToggleGroup(toggleGroup_tab2);
        btn_match.setToggleGroup(toggleGroup_tab2);
        btn_all.setSelected(true);

        //设置time_spinner
        setupTimeSpinner();

        //设置定时器
        seconds = 0;
        timerStrategy = new VariableTimeStrategy(time_label);
        timer = new Timeline(new KeyFrame(Duration.seconds(1),actionEvent -> {
            timerStrategy.doWork();
            updateTraining();
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

    //“开始”Button的Listener
    @FXML
    void start_btn_click(ActionEvent event) throws IOException {
        timerStrategy.setSeconds(seconds);
        time_label.setText(timerStrategy.getSeconds()+"s");

        timer.play();

        const_time_btn.setDisable(true);
        var_time_btn.setDisable(true);
        time_spinner.setDisable(true);

        startTraining();
    }

    //“结束”Button的Listener
    @FXML
    void end_btn_click(ActionEvent event){
        notifyEndingTrain();
    }

    //“定时训练”Button的Listener
    @FXML
    void const_time_btn_click(){
        seconds = (Integer) time_spinner.getValue();
        time_spinner.setVisible(true);
        timerStrategy = new ConstantTimeStrategy(time_label, this);
    }

    //“计时训练”Button的Listener
    @FXML
    void var_time_btn_click(){
        seconds = 0;
        time_spinner.setVisible(false);
        timerStrategy = new VariableTimeStrategy(time_label);
    }

    //被ConstantTimerStrategy调用，进行结束训练后的操作
    public void notifyEndingTrain(){
        System.out.println("time up");

        timer.stop();

        timerStrategy.reset();

        const_time_btn.setDisable(false);
        var_time_btn.setDisable(false);
        time_spinner.setDisable(false);

        endTraining();
    }

    private void setupTimeSpinner(){
        time_spinner.setVisible(false);
        time_spinner.setEditable(true);
        //设置最大值和最小值
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 1);
        time_spinner.setValueFactory(valueFactory);

        //设置ChangeListener
        time_spinner.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            seconds = (Integer) newValue;
        }));
    }

    public void setWorker(Worker worker){
        this.worker = worker;

        //需要获得worker的workStationID之后才可以创建lock
        //模拟：通过URAT接收锁的状态
        for(int i=0;i<50;i++){
            Lock lock = new Lock(LockStatus.ON, worker.getWorkStationID());
            locks.add(lock);
            labels.add(new Label(lock.getStatus().toString()));
        }

        //添加Label到gridPane
        for(int i=0;i<5;i++){
            for(int j=0;j<10;j++){
                gridPane.add(labels.get(i * 10 + j) ,i ,j);
            }
        }

        getTrainingHistories();
    }

    public boolean getTrainingHistories(){
        //在这里获取trainingHistories
        //并且设置训练记录界面
        return true;
    }

    public boolean startTraining() throws IOException {
        //在这里初始化trainingHistory
//        trainingHistory = new TrainingHistory(1,1,1,1,1,false,worker);
//        SocketClient client = new SocketClient("localhost",12345);
//        client.connect();
//
//        client.send(worker.getStartTrainingJson(trainingHistory));
//        String data = client.receive();
//        System.out.println(data);
//        client.close();
        return true;
    }

    public boolean updateTraining(){
        return true;
    }

    public boolean endTraining(){
        return true;
    }
}