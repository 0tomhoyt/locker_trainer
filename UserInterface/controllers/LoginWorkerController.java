package controllers;

import javafx.scene.control.TextField;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import models.Machine;
import models.Worker;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginWorkerController implements Initializable {
    private FXMLLoader outerLoader;
    private Machine machine;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private String panePosition;
    @FXML
    private TextField field_username;
    @FXML
    private TextField field_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);
    }

    public void setOuterFXMLLoader(FXMLLoader outerLoader) {
        this.outerLoader = outerLoader;
    }

    public void setPanePosition(String panePosition) {
        this.panePosition = panePosition;
    }

    public void setMachine(Machine machine){
        this.machine = machine;
    }

    @FXML
    void btn_login(Event event) throws IOException {
        String username = field_username.getText();
        String password = field_password.getText();
        int workStationID = panePosition.equals("insertionPoint1") ? 1:2;

        Worker worker = new Worker(username,password,machine.getId(),workStationID);

//        SocketClient client = new SocketClient("localhost", 12345);
//        client.connect();
//
//        client.send(worker.getLoginJson());
//        client.receive();

        if(true){ //等SocketClient做好了，再更改if条件
            try{
                anchorPane.getChildren().clear();
                FXMLLoader innerLoader = new FXMLLoader(getClass().getResource("../fxml/worker_UI.fxml"));
                innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
                innerLoader.load();

                WorkerUIController workerUIController = innerLoader.getController();
                workerUIController.setWorker(worker);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
//        client.close();
    }
}