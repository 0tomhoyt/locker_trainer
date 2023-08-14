package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import main.Main;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RegisterController implements Controller, Initializable {
    @FXML
    private TextField field_username;
    @FXML
    private TextField field_password;
    @FXML
    private TextField field_password_again;
//    @FXML
//    private TextField role;
    @FXML
    private TextField authToken;
    @FXML
    private RadioButton adminButton;
    @FXML
    public boolean register_btn_click() throws IOException, JSONException {
//        if(field_username == null)
//            System.out.println("username is empty");
//        if(field_password == null)
//            System.out.println("password is empty");
//        if(field_password_again == null)
//            System.out.println("please enter password again");


        String username = field_username.getText();
        String password = field_password.getText();
        String passwordAgain = field_password_again.getText();
        String finalPassword = "1";
        int role = 2;
        if (passwordAgain.equals(password))

            finalPassword = Tools.MD5hash(password);
        if(adminButton.isSelected())
            role = 1;


        String finalPassword1 = finalPassword;
        int finalRole = role;
//        Future<String> future = Main.executorService.submit(() -> {
//
//            return Tools.socketConnect(registerJSON(finalPassword1, finalRole));
//        });
//
//        Popup popup = MainController.showLoadingPopup("运行中");
//
//        try {
//            JSONObject jsonObject = Tools.transferToJSONObject(future.get(5, TimeUnit.SECONDS));
//            if (jsonObject.has("loginSuccess") && jsonObject.getInt("code") == 200 && jsonObject.getBoolean("loginSuccess")){
//                int machineID = jsonObject.getInt("machineId");
//                int workerStationID = worker.isAdmin() ? 0 : jsonObject.getInt("workstationId");
//                if (machineID == worker.getMachineID() && workerStationID == worker.getWorkStationID()) {
//                    updateWorker(jsonObject);
//                    popup.hide();
//                    afterLogin();
//                    return true;
//                }
//                else {
//                    popup.hide();
//                    MainController.popUpAlter("ERROR","","");
//                    return false;
//                }
//            }
//            else {
//                popup.hide();
//                MainController.popUpAlter("ERROR","",Tools.unicodeToChinese(jsonObject.getString("message")));
//                return false;
//            }
//        } catch (TimeoutException e) {
//            // 超时处理
//            popup.hide();
//            MainController.popUpAlter("ERROR","Time UP","登录超时");
//            return false;
//        } catch (InterruptedException | ExecutionException e) {
//            // 其他异常处理
//            popup.hide();
//            MainController.popUpAlter("ERROR","ERROR","登录失败");
//            return false;
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }


        String s = Tools.socketConnect(registerJSON(finalPassword,role));
        JSONObject jsonObject = Tools.transferToJSONObject(s);
        System.out.println(jsonObject);
        return false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);
    }

    private String registerJSON(String hashedPassword, int role){

        return String.format("{ \"event\": \"addUser\", \"data\": { \"authToken\":\"%s\", \"userName\":\"%s\", \"password\" : \"%s\" ,\"role\":\"%d\"}}",
                authToken.getText(),
                field_username.getText(),
                hashedPassword,
                role

        );
    }

}
