package controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.Popup;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import main.Main;
import models.*;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class LoginAdminController extends LoginWorkerController implements Initializable, Controller {
/*
    继承，重写了
    login_btn_click，
    afterLogin
*/
    private Alert alert;

    public void onTextFieldClicked(MouseEvent event) {
        openVirtualKeyboard();
    }

    // 打开虚拟键盘的方法
    private void openVirtualKeyboard() {
        try {
            // 启动虚拟键盘的代码
            Runtime.getRuntime().exec("cmd /c C:\\Windows\\System32\\osk.exe");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    void login_btn_click(Event event) throws IOException, JSONException {
        String username = field_username.getText();
        String password = Tools.MD5hash(field_password.getText());
        int machineID = machine.getId();

        worker = new Admin(username,password,machineID);

        System.out.println(worker.getLoginJson());
        login(worker);

//        System.out.println("admin button finish");
    }

    @Override
    protected void afterLogin() {
        try {
//            anchorPane.getChildren().clear();
//            FXMLLoader innerLoader = new FXMLLoader(getClass().getResource("/fxml/admin_UI.fxml"));
//            innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
//            innerLoader.load();
//
//            AdminUIController adminUIController = innerLoader.getController();
//            adminUIController.setWorker(worker);//这里还需要写写，UI相关
//            adminUIController.setMainController(this.getMainController());

            MainController.deleteController(this);
            mainController.goToAdminUI(worker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void figure_login_btn_click(ActionEvent event) throws JSONException {
        int machineID = machine.getId();

        worker = new Admin("", "0", machineID);


//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//        try {
//            final int[] elapsedTime = {0};
//            ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(() -> {
//                System.out.println("elapsedTime");
//                if (elapsedTime[0] >= 120) {  // 超过60秒则停止
//                    System.out.println("abc");
//                    scheduler.shutdown();
//                    return;
//                }
//                try {
//                    System.out.println("abc");
//                    if (figureLogin(worker)) {
//                        scheduler.shutdown(); // 登录成功则停止
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                elapsedTime[0] += 2;  // 每次增加2秒
//            }, 0, 2, TimeUnit.SECONDS);  // 初始延迟0秒，每2秒执行一次
//        } finally {
//            scheduler.shutdown();
//        }
        System.out.println("abc");
        boolean loggedin = false;
        for (int i = 0;i<50&& !loggedin;i++){
			loggedin = figureLogin(worker);
        }
    }

    @FXML
    private void Switch_to_register_click(ActionEvent event) throws IOException {
        // 加载目标FXML文件
        FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
        Parent root = loader.load();

        // 创建新的场景
        Scene scene = new Scene(root);

        // 获取舞台
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // 设置新的场景
        stage.setScene(scene);
    }

    private boolean figureLogin(Worker worker) throws JSONException {
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(worker.getFigureLoginJson()));

//        Popup popup = MainController.showLoadingPopup("登录中");

        try {
            String data = future.get(10, TimeUnit.SECONDS);
            System.out.println(data);
            JSONObject jsonObject = Tools.transferToJSONObject(data);
            if (jsonObject.has("loginSuccess") && jsonObject.getInt("code") == 200 && jsonObject.getBoolean("loginSuccess")) {
                int machineID = jsonObject.getInt("machineId");
                int workerStationID = worker.isAdmin() ? 0 : jsonObject.getInt("workstationId");
                worker.setUsername(jsonObject.getString("userName"));
                if (machineID == worker.getMachineID() && workerStationID == worker.getWorkStationID()) {
                    updateWorker(jsonObject);
//                    popup.hide();
                    afterLogin();
                    return true;
                }
            }
        } catch (TimeoutException e) {
            // 超时处理
//            alert.close();
//            MainController.popUpAlter("ERROR","Time UP","登录超时");
            return false;
        } catch (InterruptedException | ExecutionException e) {
            // 其他异常处理
//            alert.close();
//            MainController.popUpAlter("ERROR","ERROR","登录失败");
            return false;
        }
        return false;
    }

    @FXML
    void register_btn_click(ActionEvent event){
        mainController.goToRegister();
    }

    public static void main(String[] args) {
        System.out.println(Tools.MD5hash("123456"));
    }
}