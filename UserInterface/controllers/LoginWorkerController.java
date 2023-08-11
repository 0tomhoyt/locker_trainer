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

    //    protected String choosePage(){//本来想用这个来继承的，但好像afterLogin那个好一点
//        return "../fxml/worker_UI.fxml";
//    }
    protected void afterLogin(JSONObject jsonObject) {
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
    protected String socketConnect(String s) throws IOException{
        SocketClient client = new SocketClient("localhost", 5001);
        client.connect();

        client.send(s);
        String s2 = client.receive();
        client.close();
        return s2;
    }

    protected JSONObject transferToJSON(String s) throws JSONException{
        String tokenInfoEsca = StringEscapeUtils.unescapeJava(s);
        // 去除前后的双引号
        tokenInfoEsca = tokenInfoEsca.substring(1, tokenInfoEsca.length() - 1);
        // 转换为json对象
        JSONObject jsonObject = new JSONObject(tokenInfoEsca);


        return jsonObject;
    }
    protected boolean login(Worker worker) throws IOException, JSONException {
        //变成了函数socketConnect
//        SocketClient client = new SocketClient("localhost", 5001);
//        client.connect();
//
//        client.send(worker.getLoginJson());
//        String data = client.receive();

        String data = socketConnect(worker.getLoginJson());

        //变成函数transferToJSON
//        System.out.println(data + "in LoginWorkerController Login method");
//        // 反转义java字符串
//        String tokenInfoEsca = StringEscapeUtils.unescapeJava(data);
//        // 去除前后的双引号
//        tokenInfoEsca = tokenInfoEsca.substring(1, tokenInfoEsca.length() - 1);
//        // 转换为json对象
//        JSONObject jsonObject = new JSONObject(tokenInfoEsca);

        JSONObject jsonObject = transferToJSON(data);

        boolean loginSuccess = jsonObject.getBoolean("loginSuccess");
        int machineID = jsonObject.getInt("machineId");
        int workerStationID = 0;//设置了管理员的总是0
        if(!worker.isAdmin())//因为管理员的返回没有workstationId，所以加个判断
            workerStationID = jsonObject.getInt("workstationId");


        if (loginSuccess && machineID == worker.getMachineID() && workerStationID == worker.getWorkStationID()) {
            afterLogin(jsonObject);//把下面那一段写了一个函数，方便admin继承
//            try {
//                anchorPane.getChildren().clear();
//                FXMLLoader innerLoader = new FXMLLoader(getClass().getResource(choosePage()));//把一个
//                innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
//                innerLoader.load();
//
//                updateWorker(jsonObject);
//
//                WorkerUIController workerUIController = innerLoader.getController();
//                workerUIController.setWorker(worker);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
//        client.close();
        System.out.println("loginStatus:" + loginSuccess);
        return loginSuccess;
    }


    protected void updateWorker(JSONObject object) throws JSONException {
        worker.setAuthToken(object.getString("authToken"));
        worker.setHeaderURL(object.getString("headerURL"));
        worker.setWorkLength(object.getInt("worklength"));
    }
}