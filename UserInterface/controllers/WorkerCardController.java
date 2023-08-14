package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import main.Main;
import models.TrainingHistory;
import models.Worker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WorkerCardController implements Initializable, Controller {
    private Worker worker;
    @FXML
    private Label username;
    @FXML
    private Label userid;
    @FXML
    private Label workLength;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        MainController.addController(this);
    }

    public void setWorker(Worker worker){
        this.worker = worker;
        this.username.setText("用户名："+worker.getUsername());
        userid.setText("ID："+worker.getId());
        workLength.setText("工位："+worker.getWorkStationID());
    }

    @FXML
    void check_training_history_btn_click() {
        ((AdminUIController) Main.controllers.get("AdminUIController")).setupWorkerTrainingHistory(worker);
    }
}
