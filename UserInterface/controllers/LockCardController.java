package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import main.Main;
import models.Lock;
import models.Worker;

import java.net.URL;
import java.util.ResourceBundle;

public class LockCardController implements Initializable, Controller {
    private Lock lock;
    private Worker worker;
    @FXML
    private Label name_label;
    @FXML
    private Label serial_number_label;
    @FXML
    private Label difficulty_label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        MainController.addController(this);
    }

    public void setWorker(Worker worker){
        this.worker = worker;
    }

    public void setLock(Lock lock){
        this.lock = lock;

        name_label.setText("名字："+lock.getLockName());
        serial_number_label.setText("序列号："+lock.getSerialNumber());
        difficulty_label.setText("难度："+lock.getDifficulty());
    }

    @FXML
    void check_lock_btn_click(){
        ((AdminUIController) Main.controllers.get("AdminUIController")).setupLock(lock,worker);
    }
}
