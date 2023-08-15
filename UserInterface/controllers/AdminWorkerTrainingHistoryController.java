package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import main.Main;
import models.TrainingHistory;
import models.Worker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AdminWorkerTrainingHistoryController implements Initializable, Controller {
    private Worker worker;
    private LocalDate startDate = LocalDate.of(1900, 1, 1);
    private LocalDate endDate = LocalDate.now();
    private List<TrainingHistory> trainingHistories;
    private List<TrainingHistory> showingTrainingHistories;
    @FXML
    private DatePicker start_datePicker;
    @FXML
    private DatePicker end_datePicker;
    @FXML
    private VBox vBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        MainController.addController(this);

        setupDatePicker();
    }

    private void setupDatePicker(){
        start_datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            startDate = newValue;
            filter();
        });
        end_datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            endDate = newValue;
            filter();
        });
    }

    private void filter(){
        showingTrainingHistories.clear();
        for(TrainingHistory t : trainingHistories){
            LocalDate date = t.getDateTime().toLocalDate();
            System.out.println(startDate);
            System.out.println(date);
            System.out.println(endDate);
            if( !date.isBefore(startDate) && !date.isAfter(endDate)){
                System.out.println(t);
                showingTrainingHistories.add(t);
            }
        }
        setupPage();
    }

    public void setWorker(Worker worker){
        this.worker = worker;

        getWorkerTrainingHistories();

        setupPage();
    }

    @FXML
    void return_btn_click(ActionEvent event) {
        ((AdminUIController) Main.controllers.get("AdminUIController")).setupWorkerInfo();
    }

    private boolean getWorkerTrainingHistories(){
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(worker.getTrainingHistoriesJson()));

        Popup popup = MainController.showLoadingPopup("获得训练历史中");

        try {
            String data = future.get(5, TimeUnit.SECONDS);
            System.out.println(data);
            JSONObject jsonObject = Tools.transferToJSONObject(data);
            if(jsonObject.has("code") && jsonObject.getInt("code") == 200){
                JSONArray jsonArray = jsonObject.getJSONArray("trainingList");
                trainingHistories = new ArrayList<>(jsonArray.length());
                showingTrainingHistories = new ArrayList<>(jsonArray.length());
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject trainHistoryJson = jsonArray.getJSONObject(i);
                    TrainingHistory history = new TrainingHistory(trainHistoryJson);
                    trainingHistories.add(history);
                    showingTrainingHistories.add(history);
                }
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
            MainController.popUpAlter("ERROR","","获取训练记录超时");
            return false;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            // 其他异常处理
            popup.hide();
            MainController.popUpAlter("ERROR","","获取训练记录失败");
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void setupPage(){
        vBox.getChildren().clear();
        for(TrainingHistory t : showingTrainingHistories){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/history_card.fxml"));
                Pane pane = loader.load();
                vBox.getChildren().add(pane);

                HistoryCardController controller = loader.getController();
                controller.setWorker(worker);
                controller.setTrainingHistory(t);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
