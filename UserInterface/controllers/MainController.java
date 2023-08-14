package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Main;
import javafx.fxml.Initializable;
import models.Lock;
import models.Machine;
import org.apache.commons.text.StringEscapeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import socketClient.SocketClient;
import socketClient.SocketClient2;
import util.Tools;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MainController implements Initializable, Controller {
    public List<Integer> locks;
    private Machine machine;
    @FXML
    private Button joinMatchButton1;
    @FXML
    private Button joinMatchButton2;
    public static Stage primaryStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.controllers.put(this.getClass().getSimpleName(),this);
        locks = new ArrayList<>(Collections.nCopies(120, -1));
        System.out.println(locks);
    }

    public void setJoinMatchButtonsVisible(int buttonNum, boolean visible) {
        if (buttonNum == 1)
            joinMatchButton1.setVisible(visible);
        else if (buttonNum == 2)
            joinMatchButton2.setVisible(visible);
    }

    public static void addController(Object controller){
        boolean flag = true;
        Set<String> names = Main.controllers.keySet();
        for(int i=0;i< names.size();i++){
            if(names.contains(controller.getClass().getSimpleName())){
                Main.controllers.put(controller.getClass().getSimpleName()+"2",controller);
                flag = false;
            }
        }
        if(flag){
            Main.controllers.put(controller.getClass().getSimpleName(),controller);
        }
    }

    public void setMachine(Machine machine) throws JSONException, IOException {
        this.machine = machine;
        start();
    }

    private boolean start() throws JSONException {
//        //监听match的socket
//        SocketClient2 socketClient2 = new SocketClient2(this, "localhost", 50001);
//        new Thread(() -> {
//            try {
//                socketClient2.listen();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();

        Future<String> future = Main.executorService.submit(() -> {
            SocketClient client = new SocketClient("localhost", 5001);
            client.connect();
            client.send(machine.getStartJson());
            String data = client.receive();
            client.close();
            return data;
        });

        Popup popup = showLoadingPopup("开启机器中");

        try {
            String data = future.get(5, TimeUnit.SECONDS);
            System.out.println(data);
            JSONObject jsonObject = Tools.transferToJSONObject(data);
            if(jsonObject.has("code") && jsonObject.getInt("code") == 200){
                // 启动机器成功
                popup.hide();
                return true;
            }
            else {
                popup.hide();
                // 启动机器失败
                popUpAlter("ERROR","", "启动机器失败");
                return false;
            }
        } catch (TimeoutException e) {
            popup.hide();
            // 超时处理
            popUpAlter("ERROR","","启动机器超时");
            return false;
        } catch (InterruptedException | ExecutionException e) {
            popup.hide();
            // 其他异常处理
            popUpAlter("ERROR","","启动机器失败");
            System.out.println("ERROR:"+e.getMessage());
            return false;
        }
    }

    public static void popUpAlter(String title, String headerText, String contentText){
        // 创建警告窗
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // 显示警告窗
        alert.showAndWait();
    }

    public static Popup showLoadingPopup(String message) {
        // 创建 Popup
        Popup loadingPopup = new Popup();

        // 创建等待页面内容
        VBox popupContent = new VBox();
        popupContent.getChildren().addAll(new Label(message)); // 加载提示信息

        // 设置等待页面样式
        popupContent.setStyle("-fx-background-color: white; -fx-padding: 10px;");

        // 将内容放置在 Popup 中
        loadingPopup.getContent().add(popupContent);

        // 设置 Popup 相对于主舞台的位置
        loadingPopup.setAutoHide(false);
        loadingPopup.show(primaryStage);

        return loadingPopup;
    }
}