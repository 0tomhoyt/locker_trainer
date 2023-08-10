package controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import models.Admin;
import models.Worker;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginAdminController extends LoginWorkerController implements Initializable, Controller {
/*
    继承，重写了
    login_btn_click，
    afterLogin
*/

    //    public void initialize(URL url, ResourceBundle resourceBundle) {
//        MainController.addController(this);
//    }
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
    protected void afterLogin(JSONObject jsonObject) {
        getWorkerList();
        try {
            anchorPane.getChildren().clear();
            FXMLLoader innerLoader = new FXMLLoader(getClass().getResource("../fxml/admin_UI.fxml"));
            innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
            innerLoader.load();

            updateWorker(jsonObject);

            AdminUIController adminUIController = innerLoader.getController();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getWorkerList(){

    }
    //    @Override
//    protected String choosePage() {
//        System.out.println("page");
//        return "../fxml/admin_UI.fxml";
//    }

}
