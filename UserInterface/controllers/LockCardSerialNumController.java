package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import models.Lock;

import java.net.URL;
import java.util.ResourceBundle;

public class LockCardSerialNumController implements Initializable, Controller {
    @FXML
    private Label difficulty_label;
    @FXML
    private Label lockName_label;
    @FXML
    private Label lockSerialNum_label;
    @FXML
    private Label unlockDuration_label;
    @FXML
    private Label unlockTime_label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        MainController.addController(this);
    }

    public void setInfo(String unlockTime, int unlockDuration, Lock lock){
        unlockTime_label.setText("解锁时间"+unlockTime);
        unlockDuration_label.setText("解锁用时："+unlockDuration);
        lockSerialNum_label.setText("序列号："+lock.getSerialNumber());
        lockName_label.setText("名字："+lock.getLockName());
        difficulty_label.setText("难度："+lock.getDifficulty());
    }
}
