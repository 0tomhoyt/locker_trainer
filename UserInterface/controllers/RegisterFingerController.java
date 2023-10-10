package controllers;

import javafx.fxml.Initializable;
import main.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterFingerController implements Initializable,Controller {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);
    }
}
