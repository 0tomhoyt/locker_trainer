package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

import javafx.util.Duration;
import main.Main;
import models.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.ConstantTimeStrategy;
import util.TimerStrategy;
import util.Tools;
import util.VariableTimeStrategy;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AdminStartTrainingController extends WorkerUIController implements Initializable, Controller {
    private Admin admin;
    private TrainingHistory trainingHistory;
    private List<Lock> locks = new ArrayList<>(120);
    private List<Label> labels = new ArrayList<>(120);
    private Timeline timer;
    private TimerStrategy timerStrategy;
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
    private Spinner sec_spinner;
    @FXML
    private Spinner min_spinner;
    @FXML
    private Button start_btn;
    @FXML
    private Button end_btn;
    @FXML
    private GridPane gridPane;

//    @FXML
//    private Label lockStatus;
//    @FXML
//    private Button pressBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);

        setupToggleGroup();

        //设置time_spinner
        setupTimeSpinner();

        //设置Button
        end_btn.setDisable(true);
    }

    private void setupToggleGroup() {
        ToggleGroup toggleGroup_tab1 = new ToggleGroup();
        var_time_btn.setToggleGroup(toggleGroup_tab1);
        const_time_btn.setToggleGroup(toggleGroup_tab1);
        var_time_btn.setSelected(true);
    }

    private void setupTimeSpinner() {
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
            trainingHistory.setSeconds((Integer) newValue);
            trainingHistory.setTotalTime((Integer) min_spinner.getValue(), (Integer) newValue);
        }));

        min_spinner.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            trainingHistory.setMinutes((Integer) newValue);
            trainingHistory.setTotalTime((Integer) newValue, (Integer) sec_spinner.getValue());
        }));
    }

    private void setupTimer() {
        timerStrategy = new VariableTimeStrategy(trainingHistory);
        timer = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
            try {
                timerStrategy.doWork();
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
            if (trainingHistory.isOn()) {
                List<Lock> newLocks = ((MainController) Main.controllers.get("MainController")).locks;
                updateOldListByNewList(locks,newLocks);

                difficulty_label.setText("难度：" + trainingHistory.getDifficulty());
                score_label.setText("分值：" + trainingHistory.getScore());
                time_label.setText(trainingHistory.getTime() + "s");

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

    private void updateOldListByNewList(List<Lock> oldList, List<Lock> newList) {
        // 添加在newList中，但不在oldList中的元素
        for (int i=0;i<newList.size();i++) {
            Lock item = newList.get(i);
            if (!oldList.contains(item)) {
                oldList.add(item);
                updateLockUI(i);
            }
        }

        oldList.removeIf(item -> !newList.contains(item));
    }

    private void updateLockUI(int i){
        int row = i / 20;
        int column = i % 20;

        for(Node node : gridPane.getChildren()){
            if(GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == row){
                Label label = (Label) node;
                label.setText(locks.get(i).getTime());
                label.setTextFill(locks.get(i).getColorByStatus());
//                BackgroundFill backgroundFill = new BackgroundFill(locks.get(i).getColorByStatus(), CornerRadii.EMPTY, Insets.EMPTY);
//                label.setBackground(new Background(backgroundFill));
            }
        }
    }

    public void setAdmin(Admin admin) throws JSONException, IOException {
        this.admin = admin;
        //拿到锁的数据
        JSONArray jsonArray = getLocksStatus();
//        System.out.println(jsonArray);

        //需要获得worker的workStationID之后才可以创建lock
        //模拟：通过URAT接收锁的状态
        for(int i=0;i<120;i++){
            Lock lock = new Lock(i,LockStatus.ON, admin.getWorkStationID());
            locks.add(lock);


            labels.add(new Label(lock.getStatus().toString()));
            //添加点击事件，方便测试
            labels.get(i).setOnMouseClicked(e -> {
//                点击时弹出窗口
                Popup lockDetail = MainController.lockDetail(lock, admin);//有个问题就是，一直点的话内存占用会越来越高XD，因为一直在新建，哪怕是点同一个，但搞了半天暂时只能想到这个逻辑了
//                lockDetail.hide();//默认是弹出的
                lockDetail.setAutoHide(true);//点击其他地方会消失

//                if (lockDetail.isShowing())
//                    lockDetail.hide();
//                else
//                    lockDetail.show(primaryStage);

                System.out.println(lock.getLockName());
            });
        }
        //更新锁的状态
        updateLocks(jsonArray);

        //添加Label到gridPane
        for(int i=0;i<6;i++){
            for(int j=0;j<20;j++){
                gridPane.add(labels.get(i * 20 + j), j, i);
                updateLockUI(i * 20 + j);
            }
        }

        //初始化trainingHistory
        trainingHistory = new TrainingHistory(admin);
        trainingHistory.setSeconds(0);
        trainingHistory.setMinutes(0);

        //设置定时器
        setupTimer();
    }

    @FXML
    void const_time_btn_click() {
        //设置trainingHistory的时间
        trainingHistory.setSeconds((Integer) sec_spinner.getValue());
        trainingHistory.setMinutes((Integer) min_spinner.getValue());
        trainingHistory.setTotalTime((Integer) min_spinner.getValue(), (Integer) sec_spinner.getValue());
        sec_spinner.setVisible(true);
        min_spinner.setVisible(true);
        timerStrategy = new ConstantTimeStrategy(trainingHistory, this);
    }

    @FXML
    void var_time_btn_click() {
        trainingHistory.setSeconds(0);
        trainingHistory.setMinutes(0);
        sec_spinner.setVisible(false);
        min_spinner.setVisible(false);
        timerStrategy = new VariableTimeStrategy(trainingHistory);
    }

    @FXML
    void start_btn_click(ActionEvent event) throws JSONException {
        ((MainController) Main.controllers.get("MainController")).startHardware();

        updateTrainingHistory();

        if (!(trainingHistory.getTotalTime() == 0 && timerStrategy instanceof ConstantTimeStrategy)) {
            time_label.setText(trainingHistory.getTime() + "s");
            score_label.setText("分值：" + trainingHistory.getScore());
            difficulty_label.setText("难度：" + trainingHistory.getDifficulty());

            timer.play();
            updateTrainingHistory();

            const_time_btn.setDisable(true);
            var_time_btn.setDisable(true);
            sec_spinner.setDisable(true);
            start_btn.setDisable(true);
            end_btn.setDisable(false);

            startTraining();
        } else {
            System.out.println("训练时间不可以等于0");
        }
    }

    @FXML
    void end_btn_click(ActionEvent event) throws JSONException {
        notifyEndingTrain();
    }

    public void notifyEndingTrain() throws JSONException {
        ((MainController) Main.controllers.get("MainController")).stopHardware();
        locks = ((MainController) Main.controllers.get("MainController")).locks;

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

    private void updateTrainingHistory() {
        int score = (int) locks.stream()
                .filter(lock -> lock.getStatus() == LockStatus.ON)
                .count();
        int unlocked = (int) locks.stream()
                .filter(lock -> lock.getStatus() == LockStatus.OFF)
                .count();
        int trainingType = timerStrategy instanceof VariableTimeStrategy ? 1 : 2;
        boolean isOn = timer.statusProperty().isEqualTo(Animation.Status.RUNNING).get();
        int totalTime = timerStrategy instanceof VariableTimeStrategy ? trainingHistory.getTime() : trainingHistory.getTotalTime();

        trainingHistory.setScore(score);
        trainingHistory.setUnlocked(unlocked);
        trainingHistory.setTrainingType(trainingType);
        trainingHistory.setOn(isOn);
        trainingHistory.setTotalTime(totalTime);
        trainingHistory.setDifficulty(timerStrategy.getDifficulty());
        System.out.println(trainingHistory.getTotalTime());
    }

    //Socket调用，数据库部分
    private boolean startTraining() throws JSONException {
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(admin.getStartTrainingJson(trainingHistory)));

        Popup popup = MainController.showLoadingPopup("开始训练中");

        try {
            JSONObject jsonObject = Tools.transferToJSONObject(future.get(5, TimeUnit.SECONDS));
            if (jsonObject.has("code") && jsonObject.getInt("code") == 200) {
                long trainingID = jsonObject.getLong("TrainingID");
                trainingHistory.setId(trainingID);
                popup.hide();
                return true;
            } else {
                popup.hide();
                MainController.popUpAlter("ERROR", "", Tools.unicodeToChinese(jsonObject.getString("message")));
                return false;
            }
        } catch (TimeoutException e) {
            // 超时处理
            popup.hide();
            MainController.popUpAlter("ERROR", "", "开始训练超时");
            return false;
        } catch (InterruptedException | ExecutionException e) {
            // 其他异常处理
            popup.hide();
            MainController.popUpAlter("ERROR", "", "开始训练失败");
            return false;
        }
    }

    private boolean updateTraining() throws IOException, JSONException {
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(admin.getUpdateTrainingJson(trainingHistory)));

        try {
            JSONObject jsonObject = Tools.transferToJSONObject(future.get(5,TimeUnit.SECONDS));
            if(jsonObject.has("code") && jsonObject.getInt("code") == 200){
                return true;
            }
            else {
                MainController.popUpAlter("ERROR","",Tools.unicodeToChinese(jsonObject.getString("message")));
                return false;
            }
        } catch (TimeoutException e) {
            // 超时处理
//            MainController.popUpAlter("ERROR","","更新训练超时");
            return false;
        } catch (InterruptedException | ExecutionException e) {
            // 其他异常处理
//            MainController.popUpAlter("ERROR","","更新训练失败");
            return false;
        }
    }

    private boolean endTraining() throws JSONException {
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(admin.getEndTrainingJson(trainingHistory,locks)));

        Popup popup = MainController.showLoadingPopup("结束任务中");

        try {
            JSONObject jsonObject = Tools.transferToJSONObject(future.get(10,TimeUnit.SECONDS));
            if(jsonObject.has("code") && jsonObject.getInt("code") == 200){
                popup.hide();
                return true;
            }
            else {
                popup.hide();
                MainController.popUpAlter("ERROR","",Tools.unicodeToChinese(jsonObject.getString("message")));
                return false;
            }
        } catch (TimeoutException e) {
            // 超时处理
            popup.hide();
            MainController.popUpAlter("ERROR","","结束训练超时");
            return false;
        } catch (InterruptedException | ExecutionException e) {
            // 其他异常处理
            popup.hide();
            MainController.popUpAlter("ERROR","","结束训练失败");
            return false;
        }
    }

//    private void updateLocks(JSONArray jsonArray) throws JSONException {
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            int l = jsonObject.getInt("lockId");
//            int serialNum = jsonObject.getInt("lockSerialNumber");
//            String lockName = jsonObject.getString("lockName");
////            System.out.println(jsonArray.getJSONObject(i) +" "+ i);
////            labels.get(l).setOnMouseClicked(e ->{
////                System.out.println("special one");//可以拿到，但是是竖着来的，怪
////            });
//            locks.get(l).setSerialNumber(serialNum);
//            locks.get(l).setLockName(lockName);
//        }
//    }

    private String getLocksJSON() {
        return String.format("{ \"event\": \"getLocks\", \"data\": { \"authToken\":\"%s\", \"workstationId\": \"%d\"} }",
                admin.getAuthToken(),
                1
        );
    }

    private void updateLocks(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int l = jsonObject.getInt("lockId");
            int serialNum = jsonObject.getInt("lockSerialNumber");
            String lockName = jsonObject.getString("lockName");
            int difficulty = jsonObject.getInt("difficulty");
//            更新锁的数据 因为是地址类型的，可以影响外面
            Lock lock = locks.get(l);
            lock.setSerialNumber(serialNum);
            lock.setLockName(lockName);
            lock.setDifficulty(difficulty);
        }
    }

    //得到锁的数组
    private JSONArray getLocksStatus() throws IOException, JSONException {
        JSONObject jsonObject = Tools.transferToJSONObject(Tools.socketConnect(getLocksJSON()));
        JSONArray jsonArray = jsonObject.getJSONArray("Locks");

        return jsonArray;
    }

////    //    这个页面的入口函数↓
//    public void setAdmin(Admin admin) throws JSONException, IOException {
//        int lockNum = 120;
////        this.admin = admin;//好像没什么用
//
//        JSONArray jsonArray = getLocksStatus(admin);//拿到锁的数据
////        System.out.println(jsonArray);
//        ArrayList<Lock> locks = new ArrayList<>(lockNum);//不知道怎么和mainController联系起来
//        //需要获得worker的workStationID之后才可以创建lock，为什么呢？
//        //模拟：通过URAT接收锁的状态
//        ArrayList<Label> labels = new ArrayList<>(lockNum);
//        //定义锁的locks 和labels
//        for (int i = 0; i < lockNum; i++) {
//            Lock lock = new Lock(i, LockStatus.ON, admin.getWorkStationID());
//            locks.add(lock);
//
//            labels.add(new Label(lock.getStatus().toString()));//显示的label
//            //添加点击事件，方便测试
//            labels.get(i).setOnMouseClicked(e -> {
////                点击时弹出窗口
//                Popup lockDetail = MainController.lockDetail(lock, admin);//有个问题就是，一直点的话内存占用会越来越高XD，因为一直在新建，哪怕是点同一个，但搞了半天暂时只能想到这个逻辑了
//
//                lockDetail.setAutoHide(true);//点击其他地方会消失
//
////                System.out.println(lock.getLockName());
//            });
//        }
//        //更新锁的状态到客户端
//        updateLocks(jsonArray, locks);
//
//        //添加Label到gridPane
//        int column = 20;
//        int row = 6;
//
//        for (int i = 0; i < row; i++) {//
//            for (int j = 0; j < column; j++) {//
//                gridPane.add(labels.get(i * column + j), j, i);//j是列 i是行, 这里改好之后就是横着来的了
//            }
//        }

////定义好线程
////        由于硬件返回的是一个整数，单位为0.1s 这里只是做个模拟
//
//        Thread timeCounter = new Thread(() -> {
//            AtomicInteger receiveInt = new AtomicInteger();//在thread里好像就得这么用
//            while (true) {
//
//                receiveInt.getAndIncrement();
//                System.out.println(receiveInt);
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
//                showtime((receiveInt.intValue() / 10));
//            }
//        });
////        按下按钮
//        pressbutton(timeCounter);

//    }
//
//    private void showtime(int i) {
//        Platform.runLater(() -> {
//            // This code will be executed on the JavaFX application thread
//            lockStatus.setText("所用时间" + i + "s" + "锁的名称 ： " + "智能锁1" + " 难度 : " + "1");
//        });
//
//    }
//
//    private void pressbutton(Thread timeCounter) {
//        EventHandler<ActionEvent> count =
//                new EventHandler<ActionEvent>() {
//                    public void handle(ActionEvent e) {
//                        if (timeCounter.isAlive()) {
//                            timeCounter.interrupt();
//                        } else
//                            timeCounter.start();
//                    }
//                };
//
//        pressBtn.setOnAction(count);
//    }
//
//
    //获取锁的socket的格式
//    private String getLocksJSON1(Admin admin) {
//        return String.format("{ \"event\": \"getLocks\", \"data\": { \"authToken\":\"%s\", \"workstationId\": \"%d\"} }",
//                admin.getAuthToken(),
//                1
//        );
//    }
//
//    private String getLocksJSON2(Admin admin) {
//        return String.format("{ \"event\": \"getLocks\", \"data\": { \"authToken\":\"%s\", \"workstationId\": \"%d\"} }",
//                admin.getAuthToken(),
//                1
//        );
//    }
//
    //得到锁的数组
//    private JSONArray getLocksStatus(Admin admin) throws IOException, JSONException {
////        JSONObject jsonObject1 = Tools.transferToJSONObject(Tools.socketConnect(getLocksJSON1(admin)));
////        JSONObject jsonObject2 = Tools.transferToJSONObject(Tools.socketConnect(getLocksJSON2(admin)));//先保留，还不知道这个workstationId怎么搞
//        JSONObject jsonObject = Tools.transferToJSONObject(Tools.socketConnect(getLocksJSON1(admin)));
//        JSONArray jsonArray = jsonObject.getJSONArray("Locks");
////        System.out.println(jsonObject);
////        System.out.println(jsonArray + "锁的数据！！！！");
//        return jsonArray;
//    }
//
//    private void updateLocks(JSONArray jsonArray, List<Lock> locks) throws JSONException {
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            int l = jsonObject.getInt("lockId");
//            int serialNum = jsonObject.getInt("lockSerialNumber");
//            String lockName = jsonObject.getString("lockName");
//            int difficulty = jsonObject.getInt("difficulty");
////            更新锁的数据 因为是地址类型的，可以影响外面
//            Lock lock = locks.get(l);
//            lock.setSerialNumber(serialNum);
//            lock.setLockName(lockName);
//            lock.setDifficulty(difficulty);
//        }
//    }
//
//    public class ThreadCounterExample {
//        private static int counter = 0; // Shared counter
//
//        public static void main(String[] args) {
//            final int numThreads = 5;
//            Thread[] threads = new Thread[numThreads];
//
//            // Create and start multiple threads using lambda expressions
//            for (int i = 0; i < numThreads; i++) {
//                threads[i] = new Thread(() -> {
//                    for (int j = 0; j < 10000; j++) {
//                        synchronized (ThreadCounterExample.class) {
//                            counter++;
//                        }
//                    }
//                });
//                threads[i].start();
//            }
//
//            // Wait for all threads to complete
//            for (Thread thread : threads) {
//                try {
//                    thread.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            System.out.println("Final counter value: " + counter);
//        }
//    }

}
