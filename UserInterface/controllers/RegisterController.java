package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import models.Admin;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Controller, Initializable {
    @FXML
    private TextField field_username;
    @FXML
    private TextField field_password;
    @FXML
    private TextField field_password_again;
    private MainController mainController;
//    @FXML
//    private TextField role;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);
    }

    public void setMainController(MainController mainController){
        this.mainController = mainController;
    }

    public void onTextFieldClicked(MouseEvent event) {
        openVirtualKeyboard();
    }
    private void openVirtualKeyboard() {
        try {
            // 启动虚拟键盘的代码
            Runtime.getRuntime().exec("cmd /c C:\\Windows\\System32\\osk.exe");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public boolean register_btn_click() throws IOException, JSONException {
        String username = field_username.getText();
        String password = field_password.getText();
        String passwordAgain = field_password_again.getText();
        String finalPassword = "1";
        int role = 1;
        if (passwordAgain.equals(password)){
            finalPassword = Tools.MD5hash(password);
            String s = Tools.socketConnect(registerJSON(username,finalPassword,role));
            JSONObject jsonObject = Tools.transferToJSONObject(s);
            Admin admin = new Admin(username,finalPassword,0);
            admin.setAuthToken(jsonObject.getString("authToken"));
            mainController.admin = admin;
            if (jsonObject.getInt("code") == 200){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("注册成功");
                alert.setHeaderText(null);
                alert.setContentText("用户注册成功！请录入指纹");
                alert.showAndWait();
                mainController.goToRegisterFinger();
                return true;
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("注册失败");
                alert.setHeaderText(null);
                alert.setContentText("注册失败，请重试");
                alert.showAndWait();
                mainController.goToRegisterFinger();
                return true;
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("密码不一致");
            alert.setHeaderText(null);
            alert.setContentText("密码不一致");
            alert.showAndWait();
            return false;
        }

//        String s = Tools.socketConnect(registerJSON(finalPassword,role));
//        JSONObject jsonObject = Tools.transferToJSONObject(s);
//        if (jsonObject.getInt("code") == 200){
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("注册成功");
//            alert.setHeaderText(null);
//            alert.setContentText("用户注册成功！");
//            alert.showAndWait();
//            return true;
//        }
//        else {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("注册失败");
//            alert.setHeaderText(null);
//            alert.setContentText("注册失败");
//            alert.showAndWait();
//            return false;
//        }
    }

    @FXML
    void login_btn_click(ActionEvent event){
        mainController.goToLogin();
    }

    private String registerJSON(String username,String hashedPassword, int role){

        return String.format("{ \"event\": \"addUser\", \"data\": { \"authToken\":\"%s\", \"userName\":\"%s\", \"password\" : \"%s\" ,\"role\":\"%d\"}}",
                " ",
                username,
                hashedPassword,
                role

        );
    }
}
