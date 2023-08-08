package controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import models.Worker;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginAdminController extends LoginWorkerController implements Initializable, Controller {

//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        MainController.addController(this);
//    }
    @Override
    void login_btn_click(Event event) throws IOException, JSONException {
        String username = field_username.getText();
        String password = field_password.getText();
        int machineID = machine.getId();
        int workStationID = 0;

        worker = new Worker(username,password,workStationID,machineID);
        System.out.println(worker.getLoginJson());
        login(worker);


        System.out.println("admin button finish");

    }

    @Override
    protected String choosePage() {
        System.out.println("page");
        return "../fxml/admin_UI.fxml";
    }

}
