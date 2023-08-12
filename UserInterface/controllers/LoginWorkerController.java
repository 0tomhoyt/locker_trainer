package controllers;

import javafx.scene.control.TextField;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import main.Main;
import models.Machine;
import models.Worker;
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

public class LoginWorkerController implements Initializable, Controller {
    protected FXMLLoader outerLoader;
    protected Machine machine;
    protected Worker worker;
    @FXML
    protected AnchorPane anchorPane;
    @FXML
    protected String panePosition;
    @FXML
    protected TextField field_username;
    @FXML
    protected TextField field_password;

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

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    @FXML
    void login_btn_click(Event event) throws IOException, JSONException {
        String username = field_username.getText();
        String password = field_password.getText();
        int machineID = machine.getId();
        int workStationID = panePosition.equals("insertionPoint1") ? 2 * machineID - 1 : 2 * machineID;

        worker = new Worker(username, password, workStationID, machine.getId());
        System.out.println(worker.getLoginJson());
        login(worker);
    }

    protected boolean login(Worker worker) throws IOException, JSONException {
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(worker.getLoginJson()));

        try {
            JSONObject jsonObject = Tools.transferToJSONObject(future.get(5,TimeUnit.SECONDS));
            if (jsonObject.has("loginSuccess") && jsonObject.getInt("code") == 200 && jsonObject.getBoolean("loginSuccess")){
                int machineID = jsonObject.getInt("machineId");
                int workerStationID = worker.isAdmin() ? 0 : jsonObject.getInt("workstationId");
                if (machineID == worker.getMachineID() && workerStationID == worker.getWorkStationID()) {
                    updateWorker(jsonObject);
                    afterLogin();
                    return true;
                }
                else {
                    MainController.popUpAlter("问题","非本机ID","");
                    return false;
                }
            }
            else {
                MainController.popUpAlter("ERROR","login failed","login failed");
                return false;
            }
        } catch (TimeoutException e) {
            // 超时处理
            System.out.println("Socket receive timeout: " + e.getMessage());
            MainController.popUpAlter("ERROR","Time UP",e.getMessage());
            return false;
        } catch (InterruptedException | ExecutionException e) {
            // 其他异常处理
            System.out.println("ERROR:"+e.getMessage());
            MainController.popUpAlter("ERROR","ERROR",e.getMessage());
            return false;
        }
    }

    protected void afterLogin() throws IOException, JSONException {
        try {
            anchorPane.getChildren().clear();
            FXMLLoader innerLoader = new FXMLLoader(getClass().getResource("../fxml/worker_UI.fxml"));
            innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
            innerLoader.load();

            WorkerUIController workerUIController = innerLoader.getController();
            workerUIController.setWorker(worker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    protected void updateWorker(JSONObject object) throws JSONException {
        worker.setAuthToken(object.getString("authToken"));
        worker.setHeaderURL(object.getString("headerURL"));
        worker.setWorkLength(object.getInt("worklength"));
    }
}