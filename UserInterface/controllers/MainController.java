package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.Main;
import javafx.fxml.Initializable;
import models.Machine;
import org.apache.commons.text.StringEscapeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import socketClient.SocketClient;
import socketClient.SocketClient2;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MainController implements Initializable, Controller {
    private Machine machine;
    @FXML
    private Button joinMatchButton1;
    @FXML
    private Button joinMatchButton2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.controllers.put(this.getClass().getSimpleName(),this);
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

    private void start() throws JSONException {
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

        try {
            String receivedMessage = future.get(5, TimeUnit.SECONDS); // 设置超时时间为5秒
            // 反转义java字符串
            String tokenInfoEsca = StringEscapeUtils.unescapeJava(receivedMessage);
            // 去除前后的双引号
            tokenInfoEsca = tokenInfoEsca.substring(1, tokenInfoEsca.length() -1);
            // 转换为json对象
            JSONObject jsonObject = new JSONObject(tokenInfoEsca);
            if(jsonObject.has("code") && jsonObject.getInt("code") == 200){
                // 启动机器成功
            }
            else {
                // 启动机器失败
                popUpAlter("ERROR","启动机器失败",unicodeToChinese(jsonObject.getString("message")));
            }
        } catch (TimeoutException e) {
            // 超时处理
            System.out.println("Socket receive timeout: " + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            // 其他异常处理
            System.out.println("ERROR:"+e.getMessage());
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

    public static String unicodeToChinese(String unicodeString) {
        StringBuilder chineseText = new StringBuilder();
        int startIndex = 0;

        while (startIndex < unicodeString.length()) {
            int slashIndex = unicodeString.indexOf("\\u", startIndex);
            if (slashIndex == -1) {
                chineseText.append(unicodeString.substring(startIndex));
                break;
            }
            chineseText.append(unicodeString, startIndex, slashIndex);

            int codeStart = slashIndex + 2;
            int codeEnd = codeStart + 4;
            if (codeEnd <= unicodeString.length()) {
                String unicodeCode = unicodeString.substring(codeStart, codeEnd);
                char character = (char) Integer.parseInt(unicodeCode, 16);
                chineseText.append(character);
                startIndex = codeEnd;
            } else {
                chineseText.append(unicodeString.substring(slashIndex));
                break;
            }
        }

        return chineseText.toString();
    }
}