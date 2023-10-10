package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
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

    @FXML
    public boolean register_btn_click() throws IOException, JSONException {
        String username = field_username.getText();
        String password = field_password.getText();
        String passwordAgain = field_password_again.getText();
        String finalPassword = "1";
        int role = 1;
        if (passwordAgain.equals(password)){
            finalPassword = Tools.MD5hash(password);

            mainController.goToRegisterFinger();

            return true;
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

    private String registerJSON(String hashedPassword, int role){

        return String.format("{ \"event\": \"addUser\", \"data\": { \"authToken\":\"%s\", \"userName\":\"%s\", \"password\" : \"%s\" ,\"role\":\"%d\"}}",
                " ",
                field_username.getText(),
                hashedPassword,
                role

        );
    }
}
