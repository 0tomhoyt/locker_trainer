package controllers;

import javafx.scene.control.TextField;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import models.Machine;
import models.Worker;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import socketClient.SocketClient;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginWorkerController implements Initializable, Controller {
    private FXMLLoader outerLoader;
    protected Machine machine;
    protected Worker worker;
    @FXML
    private AnchorPane anchorPane;
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

    protected String choosePage(){//方便admin继承
        return "../fxml/worker_UI.fxml";
    }
    private void afterLogin(JSONObject jsonObject) {//本来想用这个来继承的，但好像上面那个少一点
        try {
            anchorPane.getChildren().clear();
            FXMLLoader innerLoader = new FXMLLoader(getClass().getResource("../fxml/worker_UI.fxml"));
            innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
            innerLoader.load();

            updateWorker(jsonObject);

            WorkerUIController workerUIController = innerLoader.getController();
            workerUIController.setWorker(worker);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        SocketClient client = new SocketClient("localhost", 5001);
        client.connect();

        client.send(worker.getLoginJson());
        String data = client.receive();
        System.out.println(data + "in LoginWorkerController Login method");
        // 反转义java字符串
        String tokenInfoEsca = StringEscapeUtils.unescapeJava(data);
        // 去除前后的双引号
        tokenInfoEsca = tokenInfoEsca.substring(1, tokenInfoEsca.length() - 1);
        // 转换为json对象
        JSONObject jsonObject = new JSONObject(tokenInfoEsca);
        boolean loginSuccess = jsonObject.getBoolean("loginSuccess");
        int machineID = jsonObject.getInt("machineId");
        int workerStationID = jsonObject.getInt("workstationId");
        if (loginSuccess && machineID == worker.getMachineID() && workerStationID == worker.getWorkStationID()) {
//            afterLogin(jsonObject);
            try {
                anchorPane.getChildren().clear();
                FXMLLoader innerLoader = new FXMLLoader(getClass().getResource(choosePage()));//把一个
                innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
                innerLoader.load();

                updateWorker(jsonObject);

                WorkerUIController workerUIController = innerLoader.getController();
                workerUIController.setWorker(worker);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        client.close();
        System.out.println("loginStatus:" + loginSuccess);
        return loginSuccess;
    }


    private void updateWorker(JSONObject object) throws JSONException {
        worker.setAuthToken(object.getString("authToken"));
        worker.setHeaderURL(object.getString("headerURL"));
        worker.setWorkLength(object.getInt("worklength"));
    }
}