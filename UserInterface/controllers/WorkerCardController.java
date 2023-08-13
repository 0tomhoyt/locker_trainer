package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class WorkerCardController implements Initializable, Controller {
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

    public void setInfo(String username, int userID, int workStationID){
        this.username.setText("用户名："+username);
        userid.setText("ID："+userID);
        workLength.setText("工位："+workStationID);
    }
}
