package UserInterface.controllers;

import UserInterface.main.Main;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginWorkerController implements Initializable {
    private FXMLLoader outerLoader;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private String panePosition;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.controllers.put("LoginWorkerController",this);
    }

    public void setOuterFXMLLoader(FXMLLoader outerLoader) {
        this.outerLoader = outerLoader;
    }

    public void setPanePosition(String panePosition) {
        this.panePosition = panePosition;
    }

    @FXML
    void login(Event event){
        try{
            anchorPane.getChildren().clear();
            FXMLLoader innerLoader = new FXMLLoader(getClass().getResource("../fxml/worker_UI.fxml"));
            System.out.println(panePosition);
            innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
            innerLoader.load();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
