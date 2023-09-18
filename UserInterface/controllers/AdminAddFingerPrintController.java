package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Popup;
import main.Main;
import models.Admin;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AdminAddFingerPrintController implements Initializable, Controller {
    private Admin admin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);
    }

    public void setAdmin(Admin admin){
        this.admin = admin;
    }

    @FXML
    void add_finger_btn_click(ActionEvent event){
        addFingerPrint();
    }

    private boolean addFingerPrint(){
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(admin.addFingerLoginJson()));

        Popup popup = MainController.showLoadingPopup("登录中");

        try {
            String data = future.get(10, TimeUnit.SECONDS);
            System.out.println(data);
            JSONObject jsonObject = Tools.transferToJSONObject(data);
            if (jsonObject.getInt("code") == 200){
                popup.hide();

                return true;
            }
            else {
                popup.hide();
                MainController.popUpAlter("ERROR","",Tools.unicodeToChinese(jsonObject.getString("message")));
                return false;
            }
        } catch (TimeoutException e) {
            // 超时处理
            popup.hide();
            MainController.popUpAlter("ERROR","Time UP","添加指纹超时");
            return false;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            // 其他异常处理
            popup.hide();
            MainController.popUpAlter("ERROR","ERROR","添加指纹失败");
            return false;
        }
    }
}
