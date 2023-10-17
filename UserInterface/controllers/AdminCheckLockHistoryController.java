package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import main.Main;
import models.Admin;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminCheckLockHistoryController implements Initializable, Controller {
    private Admin admin;
    @FXML
    private TextField serial_textField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        MainController.addController(this);
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

    public void setAdmin(Admin admin){
        this.admin = admin;
    }

    @FXML
    void check_lock_history_btn_click(ActionEvent event) {
        int serialNumber = Integer.parseInt(serial_textField.getText());
        ((AdminUIController) Main.controllers.get("AdminUIController")).setupLockHistory(serialNumber);
    }
}
