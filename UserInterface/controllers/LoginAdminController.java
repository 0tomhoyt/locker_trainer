package controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Popup;
import main.Main;
import models.*;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class LoginAdminController extends LoginWorkerController implements Initializable, Controller {
/*
    继承，重写了
    login_btn_click，
    afterLogin
*/

    @Override
    void login_btn_click(Event event) throws IOException, JSONException {
        String username = field_username.getText();
        String password = Tools.MD5hash(field_password.getText());
        int machineID = machine.getId();

        worker = new Admin(username,password,machineID);

        System.out.println(worker.getLoginJson());
        login(worker);

//        System.out.println("admin button finish");
    }

    @Override
    protected void afterLogin() {
        try {
            anchorPane.getChildren().clear();
            FXMLLoader innerLoader = new FXMLLoader(getClass().getResource("/fxml/admin_UI.fxml"));
            innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
            innerLoader.load();

            AdminUIController adminUIController = innerLoader.getController();
            adminUIController.setWorker(worker);//这里还需要写写，UI相关
            adminUIController.setMainController(this.getMainController());

            MainController.deleteController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void figure_login_btn_click(ActionEvent event) throws JSONException, IOException {
        String username = field_username.getText();
        int machineID = machine.getId();

        worker = new Admin(username, "0", machineID);

        figureLogin(worker);
    }

    private boolean figureLogin(Worker worker) throws IOException, JSONException {
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(worker.getFigureLoginJson()));

        Popup popup = MainController.showLoadingPopup("登录中");

        try {
            String data = future.get(10, TimeUnit.SECONDS);
            System.out.println(data);
            JSONObject jsonObject = Tools.transferToJSONObject(data);
            if (jsonObject.has("loginSuccess") && jsonObject.getInt("code") == 200 && jsonObject.getBoolean("loginSuccess")){
                int machineID = jsonObject.getInt("machineId");
                int workerStationID = worker.isAdmin() ? 0 : jsonObject.getInt("workstationId");
                if (machineID == worker.getMachineID() && workerStationID == worker.getWorkStationID()) {
                    updateWorker(jsonObject);
                    popup.hide();
                    afterLogin();
                    return true;
                }
                else {
                    popup.hide();
                    MainController.popUpAlter("ERROR","","");
                    return false;
                }
            }
            else {
                popup.hide();
                MainController.popUpAlter("ERROR","",Tools.unicodeToChinese(jsonObject.getString("message")));
                return false;
            }
        } catch (TimeoutException e) {
            // 超时处理
            popup.hide();
            MainController.popUpAlter("ERROR","Time UP","登录超时");
            return false;
        } catch (InterruptedException | ExecutionException e) {
            // 其他异常处理
            popup.hide();
            MainController.popUpAlter("ERROR","ERROR","登录失败");
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(Tools.MD5hash("123456"));
    }
}