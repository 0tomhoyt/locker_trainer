package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import models.Admin;
import models.Worker;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);
    }

    @Override
    public void setWorker(Worker worker) {
        this.worker = worker;

        setupStartGamePage();
        setupWorkerInfoPage();
    }

    private void setupStartGamePage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/admin_start_game.fxml"));
            start_game_page.getChildren().add(loader.load());

            AdminStartGameController adminStartGameController = loader.getController();
            adminStartGameController.setAdmin((Admin) worker);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupWorkerInfoPage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/admin_worker_info.fxml"));
            worker_info_page.getChildren().add(loader.load());


        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
