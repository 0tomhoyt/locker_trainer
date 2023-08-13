package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import main.Main;
import models.Admin;
import models.Machine;
import models.TrainingHistory;
import models.Worker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AdminStartGameController implements Initializable,Controller {
    private Admin admin;
    private List<TrainingHistory> trainingHistoryList = new ArrayList<>();
    private List<Worker> workers = new ArrayList<>();
    private List<Machine> machines = new ArrayList<>();
    private List<ConnectedMachineCardController> controllers = new ArrayList<>();
    private int seconds;
    private int minutes;
    private int difficulty;
    @FXML
    private Spinner min_spinner;
    @FXML
    private Spinner sec_spinner;
    @FXML
    private ChoiceBox<String> worker_choiceBox;
    @FXML
    private Button add_btn;
    @FXML
    private TextArea display_area;
    @FXML
    private Button start_game_btn;
    @FXML
    private VBox connected_machine_vbox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);

        setupSpinner();
    }

    private void setupSpinner(){
        //在start_game_btn_click的时候，需要考虑时长为0的情况
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory1 =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        sec_spinner.setValueFactory(valueFactory1);
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory2 =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        min_spinner.setValueFactory(valueFactory2);

        sec_spinner.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            seconds = (Integer) newValue;
        }));
        min_spinner.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            minutes = (Integer) newValue;
        }));
        difficulty = getDifficulty();
    }

    private int getDifficulty(){
        int time = minutes * 60 + seconds;
        if(time < 60){
            return 5;
        }
        else if(time < 2 * 60){
            return 4;
        }
        else if(time < 3 * 60){
            return 3;
        }
        else if(time < 4 * 60){
            return 2;
        }
        else {
            return 1;
        }
    }

    private void setupChoiceBox(){
        //使用之前获得的员工信息初始化ChoiceBox
        ObservableList<String> options = FXCollections.observableArrayList();
        for(Worker worker : workers){
            options.add(worker.getId()+":"+worker.getUsername());
        }

        worker_choiceBox.setItems(options);
        worker_choiceBox.setValue(workers.get(0).getId()+":"+workers.get(0).getUsername());
    }

    private void setupVbox() {
        for(int i=0;i<machines.size();i++){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/connected_machine_card.fxml"));
                Pane pane =  loader.load();
                connected_machine_vbox.getChildren().add(pane);

                ConnectedMachineCardController controller = loader.getController();
                controller.setWorkers(workers);
                controller.setInfo(machines.get(i).getId(), machines.get(i).getWorkers().get(0));
                controllers.add(controller);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public void setWorkers(List<Worker> workers){
        this.workers.addAll(workers);
        setupChoiceBox();

        getConnectedMachines();
        setupVbox();
    }

    @FXML
    void add_btn_click(){
        //根据员工信息来new Worker
        String user = worker_choiceBox.getValue();
        int colonIndex = user.indexOf(":");
        int userID = Integer.parseInt(user.substring(0, colonIndex));

        Worker worker = new Worker(-1,-1);
        for(int i=0;i<workers.size();i++){
            if(workers.get(i).getId() == userID){
                worker = workers.get(i);
            }
        }
        TrainingHistory trainingHistory = new TrainingHistory(worker);
        trainingHistoryList.add(trainingHistory);

        display_area.appendText(worker_choiceBox.getValue()+"\n");
    }

    @FXML
    void start_game_btn_click() {
        if(minutes == 0 && seconds == 0){
            MainController.popUpAlter("ERROR","","时长不能为0");
        }
        else {
            for(int i=0;i<controllers.size();i++){
                Worker worker = controllers.get(i).getWorker();
                if(worker.getId() == -1){
                    System.out.println("no worker returned");
                }
                else {
                    TrainingHistory trainingHistory = new TrainingHistory(worker);
                    trainingHistory.setTotalTime(minutes, seconds);
                    trainingHistory.setDifficulty(difficulty);
                    trainingHistoryList.add(trainingHistory);
                }
            }
//            for(int i=0;i<trainingHistoryList.size();i++){
//                trainingHistoryList.get(i).setTotalTime(minutes, seconds);
//                trainingHistoryList.get(i).setDifficulty(difficulty);
//            }

            //开始传输数据
            startGame();
        }
    }

    private boolean startGame(){
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(admin.getStartGameJson(trainingHistoryList)));

        Popup popup = MainController.showLoadingPopup("开始比赛中");

        try {
            JSONObject jsonObject = Tools.transferToJSONObject(future.get(10, TimeUnit.SECONDS));
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
            MainController.popUpAlter("ERROR","","开始比赛超时");
            return false;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            // 其他异常处理
            popup.hide();
            MainController.popUpAlter("ERROR","","开始比赛失败");
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean getConnectedMachines(){
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(admin.getConnectedMachinesJson()));

        Popup popup = MainController.showLoadingPopup("获取连接机器中");

        try {
            JSONObject jsonObject = Tools.transferToJSONObject(future.get(5, TimeUnit.SECONDS));
            if(jsonObject.has("code") && jsonObject.getInt("code") == 200){
                popup.hide();
                JSONArray jsonArray_machine = jsonObject.getJSONArray("machines");
                for(int i=0;i<jsonArray_machine.length();i++){
                    Machine machine = new Machine(jsonArray_machine.getJSONObject(i).getInt("serverId"));
                    machines.add(machine);
                    JSONArray jsonArray_workstation = jsonArray_machine.getJSONObject(i).getJSONArray("workstations");
                    for(int j=0;j<jsonArray_workstation.length();j++){
                        JSONObject object = jsonArray_workstation.getJSONObject(j);
                        int workStationID = object.getInt("workstationId");
                        int workerID = object.getInt("workerId");
                        String username = object.getString("username");
                        String headerURL = object.getString("headUrl");
                        Worker worker = new Worker(workerID,workStationID);
                        worker.setUsername(username);
                        worker.setHeaderURL(headerURL);
                        machine.addWorker(worker);
                    }
                }
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
            MainController.popUpAlter("ERROR","","获取连接机器超时");
            return false;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            // 其他异常处理
            popup.hide();
            MainController.popUpAlter("ERROR","","获取连接机器失败");
            System.out.println("ERROR:"+e.getMessage());
            return false;
        }
    }
}