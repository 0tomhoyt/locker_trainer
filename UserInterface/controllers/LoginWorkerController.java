package controllers;

import javafx.application.Platform;
import main.Main;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class LoginWorkerController implements Initializable {
    private FXMLLoader outerLoader;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private String panePosition;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        boolean flag = true;
        Set<String> names = Main.controllers.keySet();
        for(int i=0;i< names.size();i++){
            if(names.contains(this.getClass().getSimpleName())){
                Main.controllers.put(this.getClass().getSimpleName()+"2",this);
                flag = false;
            }
        }
        if(flag){
            Main.controllers.put(this.getClass().getSimpleName(),this);
        }
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
            innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
            innerLoader.load();

            WorkerUIController workerUIController = innerLoader.getController();
            workerUIController.setOuterLoader(outerLoader);
            workerUIController.setSelfLoader(innerLoader);
            workerUIController.setPanePosition(panePosition);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}