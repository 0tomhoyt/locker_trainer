package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import main.Main;
import javafx.fxml.Initializable;
import models.Machine;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import socketClient.SocketClient;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class MainController implements Initializable, Controller {
    private Machine machine;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.controllers.put(this.getClass().getSimpleName(),this);
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
        SocketClient client = new SocketClient("localhost", 12345);
        client.connect();
        client.send(machine.getStartJson());
        String data = client.receive();
        // 反转义java字符串
        String tokenInfoEsca = StringEscapeUtils.unescapeJava(data);
        // 去除前后的双引号
        tokenInfoEsca = tokenInfoEsca.substring(1, tokenInfoEsca.length() -1);
        // 转换为json对象
        JSONObject jsonObject = new JSONObject(tokenInfoEsca);
        boolean machineStatus = jsonObject.getBoolean("machinestatus");
        client.close();
        System.out.println("MachineStatus:"+machineStatus);
        return machineStatus;
    }
}