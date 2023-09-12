package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import main.Main;
import models.*;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminUIController extends WorkerUIController implements Initializable, Controller {
    @FXML
    private AnchorPane start_training_page;
    @FXML
    private VBox vBox_worker_info;
    @FXML
    private AnchorPane check_lock_history_page;
    @FXML
    private AnchorPane start_game_page;
    public List<Worker> workers = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);

    }

    private void setStart_training_page(){
        start_training_page.getChildren().clear();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_start_training.fxml"));
            Pane pane = loader.load();
            start_training_page.getChildren().add(pane);

            AdminStartTrainingController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setAdmin((Admin) worker);
        }
        catch (IOException | JSONException e){
            e.printStackTrace();
        }
    }
    @Override
    public void setWorker(Worker worker) {
        this.worker = worker;

        setupWorkerInfo();

        setupCheckLockHistory();

        setStart_training_page();

//        setupStartGamePage();
    }

    public void setupWorkerInfo(){
        vBox_worker_info.getChildren().clear();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_worker_info.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_worker_training_history.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_lock.fxml"));
            Pane pane = loader.load();
            vBox_worker_info.getChildren().add(pane);

            AdminLockController controller = loader.getController();
            controller.setReturnWay(1);
            controller.setAdmin((Admin) this.worker);
            controller.setWorker(worker);
            controller.setLock(lock);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setupCheckLockHistory(){
        check_lock_history_page.getChildren().clear();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_check_lock_history.fxml"));
            Pane pane = loader.load();
            check_lock_history_page.getChildren().add(pane);

            AdminCheckLockHistoryController controller = loader.getController();
            controller.setAdmin((Admin) worker);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setupLockHistory(int serialNumber){
        check_lock_history_page.getChildren().clear();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_lock.fxml"));
            Pane pane = loader.load();
            check_lock_history_page.getChildren().add(pane);

            Lock lock = new Lock(-1, LockStatus.OFF,-1);
            lock.setSerialNumber(serialNumber);

            AdminLockController controller = loader.getController();
            controller.setReturnWay(2);
            controller.setAdmin((Admin) this.worker);
            controller.setLock(lock);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setupStartGamePage(){
        start_game_page.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_start_game.fxml"));
            start_game_page.getChildren().add(loader.load());

            AdminStartGameController adminStartGameController = loader.getController();
            adminStartGameController.setAdmin((Admin) worker);
            System.out.println(workers);
            adminStartGameController.setWorkers(workers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void logout_btn_click(ActionEvent event){
        MainController.deleteController(this);
        ((MainController) Main.controllers.get("MainController")).logout();
    }
}
