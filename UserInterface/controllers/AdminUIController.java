package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import main.Main;
import models.Admin;
import models.Lock;
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

public class AdminUIController extends WorkerUIController implements Initializable, Controller {
    @FXML
    private AnchorPane game_history_page;

    @FXML
    private AnchorPane machine_info_page;

    @FXML
    private AnchorPane machines_worker_page;

    @FXML
    private AnchorPane start_game_page;

    @FXML
    private AnchorPane worker_info_page;
    @FXML
    private VBox vBox_worker_info;
    public List<Worker> workers = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);
    }

    @Override
    public void setWorker(Worker worker) {
        this.worker = worker;

        setupWorkerInfo();

//        setupStartGamePage();
    }

    public void setupWorkerInfo(){
        vBox_worker_info.getChildren().clear();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/admin_worker_info.fxml"));
            Pane pane = loader.load();
            vBox_worker_info.getChildren().add(pane);

            AdminWorkerInfoController controller = loader.getController();
            controller.setAdmin((Admin) worker);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setupWorkerTrainingHistory(Worker worker){
        vBox_worker_info.getChildren().clear();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/admin_worker_training_history.fxml"));
            Pane pane = loader.load();
            vBox_worker_info.getChildren().add(pane);

            AdminWorkerTrainingHistoryController controller = loader.getController();
            controller.setWorker(worker);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setupLock(Lock lock, Worker worker){
        vBox_worker_info.getChildren().clear();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/admin_lock.fxml"));
            Pane pane = loader.load();
            vBox_worker_info.getChildren().add(pane);

            AdminLockController controller = loader.getController();
            controller.setAdmin((Admin) this.worker);
            controller.setWorker(worker);
            controller.setLock(lock);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setupStartGamePage(){
        start_game_page.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/admin_start_game.fxml"));
            start_game_page.getChildren().add(loader.load());

            AdminStartGameController adminStartGameController = loader.getController();
            adminStartGameController.setAdmin((Admin) worker);
            System.out.println(workers);
            adminStartGameController.setWorkers(workers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
