package controllers;

import main.Main;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private FXMLLoader outerLoader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.controllers.put(this.getClass().getSimpleName(),this);
    }

    public void setOuterLoader(FXMLLoader outerLoader){
        this.outerLoader = outerLoader;
    }
}