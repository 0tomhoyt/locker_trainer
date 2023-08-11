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

    private boolean start() throws IOException, JSONException {
//        //监听match的socket
//        SocketClient2 socketClient2 = new SocketClient2(this, "localhost", 50001);
//        new Thread(() -> {
//            try {
//                socketClient2.listen();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();


        SocketClient client = new SocketClient("localhost", 5001);
        client.connect();
        client.send(machine.getStartJson());
        String data = client.receive();
        System.out.println(data);

        // 反转义java字符串
        String tokenInfoEsca = StringEscapeUtils.unescapeJava(data);
        // 去除前后的双引号
        tokenInfoEsca = tokenInfoEsca.substring(1, tokenInfoEsca.length() -1);
        // 转换为json对象
        JSONObject jsonObject = new JSONObject(tokenInfoEsca);
        boolean machineStatus = jsonObject.getBoolean("machinestatus");
        System.out.println("MachineStatus:"+machineStatus);

        client.close();

        return machineStatus;
    }
}