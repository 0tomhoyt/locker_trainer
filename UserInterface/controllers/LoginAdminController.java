package controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import models.*;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.io.IOException;


public class LoginAdminController extends LoginWorkerController implements Initializable, Controller {
/*
    继承，重写了
    login_btn_click，
    afterLogin
*/

    @Override
    void login_btn_click(Event event) throws IOException, JSONException {
        String username = field_username.getText();
        String password = field_password.getText();
        int machineID = machine.getId();

        worker = new Admin(username,password,machineID);

        System.out.println(worker.getLoginJson());
        login(worker);

        System.out.println("admin button finish");
    }

    @Override
    protected void afterLogin() {



        try {
            anchorPane.getChildren().clear();
            FXMLLoader innerLoader = new FXMLLoader(getClass().getResource("../fxml/admin_UI.fxml"));
            innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
            innerLoader.load();

            AdminUIController adminUIController = innerLoader.getController();
            adminUIController.setWorker(worker);//这里还需要写写，UI相关
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
