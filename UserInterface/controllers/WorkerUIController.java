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
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    private TimerStrategy timerStrategy;
    private Worker worker;
    private TrainingHistory trainingHistory;    //本次的记录
    private List<TrainingHistory> trainingHistories;    //所有的历史记录
    private List<TrainingHistory> showHistories;
    @FXML
    private GridPane gridPane;
    @FXML
    private VBox vBox;
    @FXML
    private RadioButton const_time_btn;
    @FXML
    private RadioButton var_time_btn;
    @FXML
    private Label difficulty_label;
    @FXML
    private Label score_label;
    @FXML
    private Label time_label;
    @FXML
    private RadioButton all_btn;
    @FXML
    private RadioButton train_btn;
    @FXML
    private RadioButton match_btn;
    @FXML
    private Spinner sec_spinner;
    @FXML
    private Spinner min_spinner;
    @FXML
    private Button start_btn;
    @FXML
    private Button end_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);

        //设置ToggleGroup
        setupToggleGroup();

        //设置time_spinner
        setupTimeSpinner();

        //设置Button
        end_btn.setDisable(true);
    }

    //初始化
    private void setupToggleGroup() {
        ToggleGroup toggleGroup_tab1 = new ToggleGroup();
        var_time_btn.setToggleGroup(toggleGroup_tab1);
        const_time_btn.setToggleGroup(toggleGroup_tab1);
        var_time_btn.setSelected(true);

        ToggleGroup toggleGroup_tab2 = new ToggleGroup();
        all_btn.setToggleGroup(toggleGroup_tab2);
        train_btn.setToggleGroup(toggleGroup_tab2);
        match_btn.setToggleGroup(toggleGroup_tab2);
        all_btn.setSelected(true);
    }

    private void setupTimeSpinner(){
        sec_spinner.setVisible(false);
        sec_spinner.setEditable(true);
        min_spinner.setVisible(false);
        min_spinner.setEditable(true);
        //设置最大值和最小值
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory1 =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        sec_spinner.setValueFactory(valueFactory1);
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory2 =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        min_spinner.setValueFactory(valueFactory2);

        //设置ChangeListener
        sec_spinner.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            trainingHistory.setSeconds( (Integer) newValue );
            trainingHistory.setTotalTime( (Integer) min_spinner.getValue() , (Integer) newValue );
        }));

        min_spinner.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            trainingHistory.setMinutes( (Integer) newValue );
            trainingHistory.setTotalTime( (Integer) newValue , (Integer) sec_spinner.getValue() );
        }));
    }

    private void setupTimer() {
        timerStrategy = new VariableTimeStrategy(trainingHistory);
        timer = new Timeline(new KeyFrame(Duration.seconds(1),actionEvent -> {
            try {
                timerStrategy.doWork();
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
            if(trainingHistory.isOn()){
                difficulty_label.setText("难度："+trainingHistory.getDifficulty());
                score_label.setText("分值："+trainingHistory.getScore());
                time_label.setText(trainingHistory.getTime()+"s");

                try {
                    updateTraining();
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            updateTrainingHistory();

        }));
        timer.setCycleCount(Animation.INDEFINITE);
    }

    private void setupTrainingHistoryUI() {
        vBox.getChildren().clear();
        try {
            for (int i=0;i<showHistories.size();i++){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/history_card.fxml"));
                Pane pane = fxmlLoader.load();
                vBox.getChildren().add(pane);
                HistoryCardController historyCardController = fxmlLoader.getController();
                TrainingHistory t = showHistories.get(i);
                historyCardController.setInfo(t.getScore(),t.getTotalTime(),t.getUnlocked(),t.getDifficulty());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setWorker(Worker worker) throws JSONException, IOException {
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

        //初始化trainingHistory
        trainingHistory = new TrainingHistory(worker);
        trainingHistory.setSeconds(0);
        trainingHistory.setMinutes(0);

        //设置定时器
        setupTimer();

        //获得TrainingHistories
        getTrainingHistories();

        //添加history_card到训练历史
        setupTrainingHistoryUI();
    }

    //fxml控件的Listener
    @FXML
    void all_btn_click(){
        showHistories.clear();
        for(TrainingHistory history: trainingHistories){
            showHistories.add(history);
        }
        setupTrainingHistoryUI();
    }

    @FXML
    void train_btn_click(){
        showHistories.clear();
        for(TrainingHistory history: trainingHistories){
            if (!history.isMatch()){
                showHistories.add(history);
            }
        }
        setupTrainingHistoryUI();
    }

    @FXML
    void game_btn_click(){
        showHistories.clear();
        for(TrainingHistory history: trainingHistories){
            if (history.isMatch()){
                showHistories.add(history);
                System.out.println(showHistories);
            }
        }
        setupTrainingHistoryUI();
    }

    @FXML
    void const_time_btn_click(){
        //设置trainingHistory的时间
        trainingHistory.setSeconds( (Integer) sec_spinner.getValue() );
        trainingHistory.setMinutes( (Integer) min_spinner.getValue() );
        trainingHistory.setTotalTime( (Integer) min_spinner.getValue() , (Integer) sec_spinner.getValue() );
        sec_spinner.setVisible(true);
        min_spinner.setVisible(true);
        timerStrategy = new ConstantTimeStrategy(trainingHistory, this);
    }

    @FXML
    void var_time_btn_click(){
        trainingHistory.setSeconds(0);
        trainingHistory.setMinutes(0);
        sec_spinner.setVisible(false);
        min_spinner.setVisible(false);
        timerStrategy = new VariableTimeStrategy(trainingHistory);
    }

    @FXML
    void start_btn_click(ActionEvent event) throws IOException, JSONException {
        updateTrainingHistory();

        if( !(trainingHistory.getTotalTime() == 0 && timerStrategy instanceof ConstantTimeStrategy) ){
            time_label.setText(trainingHistory.getTime()+"s");
            score_label.setText("分值："+trainingHistory.getScore());
            difficulty_label.setText("难度："+trainingHistory.getDifficulty());

            timer.play();
            updateTrainingHistory();

            const_time_btn.setDisable(true);
            var_time_btn.setDisable(true);
            sec_spinner.setDisable(true);
            start_btn.setDisable(true);
            end_btn.setDisable(false);

            startTraining();
        }
        else {
            System.out.println("训练时间不可以等于0");
        }
    }

    @FXML
    void end_btn_click(ActionEvent event) throws JSONException, IOException {
        notifyEndingTrain();
    }

    //被ConstantTimerStrategy调用，进行结束训练后的操作
    public void notifyEndingTrain() throws JSONException, IOException {
        timer.stop();
        updateTrainingHistory();

        timerStrategy.reset();

        const_time_btn.setDisable(false);
        var_time_btn.setDisable(false);
        sec_spinner.setDisable(false);
        start_btn.setDisable(false);
        end_btn.setDisable(true);

        time_label.setText("时间");
        score_label.setText("分值");
        difficulty_label.setText("难度");

        endTraining();
    }

    private void updateTrainingHistory(){
        int score = (int) locks.stream()
                .filter(lock -> lock.getStatus() == LockStatus.ON)
                .count();
        int unlocked = (int) locks.stream()
                .filter(lock -> lock.getStatus() == LockStatus.OFF)
                .count();
        int trainingType = timerStrategy instanceof VariableTimeStrategy ? 1 : 2;
        boolean isOn = timer.statusProperty().isEqualTo(Animation.Status.RUNNING).get();

        trainingHistory.setScore(score);
        trainingHistory.setUnlocked(unlocked);
        trainingHistory.setTrainingType(trainingType);
        trainingHistory.setOn(isOn);
        trainingHistory.setDifficulty(timerStrategy.getDifficulty());
    }

    //Socket调用，数据库部分
    private boolean getTrainingHistories() throws IOException, JSONException {
        //Socket连接
        SocketClient client = new SocketClient("localhost",5001);
        client.connect();

        String jsonString = worker.getTrainingHistoriesJson();
        client.send(jsonString);
        String data = client.receive();
        client.close();

        // 反转义java字符串
        String tokenInfoEsca = StringEscapeUtils.unescapeJava(data);
        // 去除前后的双引号
        tokenInfoEsca = tokenInfoEsca.substring(1, tokenInfoEsca.length() - 1);
        // 转换为json对象
        JSONObject jsonObject = new JSONObject(tokenInfoEsca);

        if(jsonObject.getInt("code") == 200){
            JSONArray jsonArray = jsonObject.getJSONArray("trainingList");
            trainingHistories = new ArrayList<>(jsonArray.length());
            showHistories = new ArrayList<>(jsonArray.length());
            for(int i=0;i<jsonArray.length();i++){
                JSONObject trainHistoryJson = jsonArray.getJSONObject(i);
                TrainingHistory history = new TrainingHistory(trainHistoryJson);
                trainingHistories.add(history);
                showHistories.add(history);
            }
            return true;
        }
        else
            return false;
    }

    private boolean startTraining() throws IOException, JSONException {
        //Socket连接
        SocketClient client = new SocketClient("localhost",5001);
        client.connect();

        String jsonString = worker.getStartTrainingJson(trainingHistory);
        System.out.println(jsonString);
        client.send(jsonString);
        String data = client.receive();
        System.out.println(data);
        client.close();

        // 反转义java字符串
        String tokenInfoEsca = StringEscapeUtils.unescapeJava(data);
        // 去除前后的双引号
        tokenInfoEsca = tokenInfoEsca.substring(1, tokenInfoEsca.length() - 1);
        // 转换为json对象
        JSONObject jsonObject = new JSONObject(tokenInfoEsca);

        int code = jsonObject.getInt("code");
        long trainingID = jsonObject.getLong("TrainingID");

        if(code == 200){
            trainingHistory.setId(trainingID);
            return true;
        }
        else {
            return false;
        }
    }

    private boolean updateTraining() throws IOException, JSONException {
        //Socket连接
        SocketClient client = new SocketClient("localhost",5001);
        client.connect();

        String jsonString = worker.getUpdateTrainingJson(trainingHistory);
        System.out.println(jsonString);
        client.send(jsonString);
        String data = client.receive();
        System.out.println(data);
        client.close();

        // 反转义java字符串
        String tokenInfoEsca = StringEscapeUtils.unescapeJava(data);
        // 去除前后的双引号
        tokenInfoEsca = tokenInfoEsca.substring(1, tokenInfoEsca.length() - 1);
        // 转换为json对象
        JSONObject jsonObject = new JSONObject(tokenInfoEsca);

        int code = jsonObject.getInt("code");
        return code == 200;
    }

    private boolean endTraining() throws IOException, JSONException {
        //Socket连接
        SocketClient client = new SocketClient("localhost",5001);
        client.connect();

        String jsonString = worker.getEndTrainingJson(trainingHistory);
        System.out.println(jsonString);
        client.send(jsonString);
        String data = client.receive();
        System.out.println(data);
        client.close();

        // 反转义java字符串
        String tokenInfoEsca = StringEscapeUtils.unescapeJava(data);
        // 去除前后的双引号
        tokenInfoEsca = tokenInfoEsca.substring(1, tokenInfoEsca.length() - 1);
        // 转换为json对象
        JSONObject jsonObject = new JSONObject(tokenInfoEsca);

        int code = jsonObject.getInt("code");
        return code == 200;
    }
}