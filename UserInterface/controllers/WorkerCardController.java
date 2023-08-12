package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WorkerCardController {
    @FXML
    private Label username;
    @FXML
    private Label userid;
    @FXML
    private Label workLength;

    public void setInfo(String username, int userID, int workStationID){
        this.username.setText("用户名："+username);
        userid.setText("ID："+userID);
        workLength.setText("工位："+workStationID);
    }
}
