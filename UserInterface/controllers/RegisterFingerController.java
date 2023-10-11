package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import main.Main;
import models.Admin;
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

public class RegisterFingerController implements Initializable,Controller {
	@FXML
    private Label field_title;

    @FXML
    private Label field_remind;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);
    }

    private Admin admin;
    private Alert alert;


    private MainController mainController;

    @FXML
    public void start_fingerprint_input()throws IOException, JSONException{
        boolean success = false;
        int times = 0;
        while (!success && times<1){
            try {
                Thread.sleep(3000);  // 阻塞3000毫秒（即3秒）
            } catch (InterruptedException e) {
                e.printStackTrace();  // 如果在休眠期间线程被中断，这里会捕获到异常
            }
            success = add_fingerprint();
            times ++;
        }
        if (!success){
            alert = new Alert(Alert.AlertType.INFORMATION, "添加指纹失败");
        }
        else {
            alert = new Alert(Alert.AlertType.INFORMATION, "添加指纹成功!");
        }
        alert.setTitle("注意");
        alert.setHeaderText("提示");
        mainController.goToLogin();
    }



    public boolean add_fingerprint(){
        field_remind.setText("请按手指\n第一次采集指纹");
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(admin.addFingerLoginJson()));
        boolean check_result;
        try {
            String data = future.get(10, TimeUnit.SECONDS);
            System.out.println(data);
            JSONObject jsonObject = Tools.transferToJSONObject(data);
            if (jsonObject.getInt("code") == 200){
                try {
                    Thread.sleep(3000);  // 阻塞3000毫秒（即3秒）
                } catch (InterruptedException e) {
                    e.printStackTrace();  // 如果在休眠期间线程被中断，这里会捕获到异常
                }
                alert = new Alert(Alert.AlertType.INFORMATION, "第一次录入指纹成功，请再次按手指以确认!");
                alert.setTitle("成功");
                alert.setHeaderText("添加成功");
                alert.showAndWait();
				check_result = check_fingerprint(jsonObject.getString("fingerprint_id"));
                return check_result;
            }
            else {
                System.out.println("j");
                return false;
            }
        } catch (TimeoutException e) {
            // 超时处理
            System.out.println("f");
            return false;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            // 其他异常处理
            System.out.println("e");
            return false;
        }
    }

    public boolean check_fingerprint(String fingerprint_id){
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(admin.checkFingerPrintJson(fingerprint_id)));
        try {
            String data = future.get(10, TimeUnit.SECONDS);
            System.out.println(data);
            JSONObject jsonObject = Tools.transferToJSONObject(data);
            if (jsonObject.getInt("code") == 200){
                field_remind.setText("第二次指纹验证成功，成功添加指纹");
                alert = new Alert(Alert.AlertType.INFORMATION, "指纹添加成功!");
                alert.setTitle("成功");
                alert.setHeaderText("添加成功");
                alert.showAndWait();
                return true;
            }
            else {
				field_remind.setText("指纹验证失败");
                return false;
            }
        } catch (TimeoutException e) {
            // 超时处理
            System.out.println("d");
            return false;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            // 其他异常处理
            System.out.println("c");
            return false;
        }
    }

    public void setMainController(MainController mainController){
        this.mainController = mainController;
    }
	public void setAdmin(Admin admin){this.admin = admin;}

}
