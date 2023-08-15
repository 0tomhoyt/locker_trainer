package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import main.Main;
import models.Lock;
import models.LockStatus;
import models.Machine;

import models.Worker;
import org.json.JSONException;
import org.json.JSONObject;
import socketClient.SocketClient;
import util.Tools;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

//1.更新每把锁的开锁时间，event在stoptraining的时候同时带上这个数据 如果锁的序列号是0，就不带 更新列表：侯宇腾
//2.每把锁需要可以设置型号名称和序列号和难度，每次需要从服务器更新并且显示。 管理员界面可以单独单人训练  //志勇
//3.管理员需要可以查看每个工人的训练历史和每个锁的开锁信息。//符讯
public class MainController implements Initializable, Controller {
    public List<Lock> locks;//120 6行20列
    private Machine machine;
    private FXMLLoader outerLoader;
    @FXML
    private Button joinMatchButton1;
    @FXML
    private Button joinMatchButton2;
    @FXML
    private AnchorPane adminTab;
    public static Stage primaryStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.controllers.put(this.getClass().getSimpleName(), this);
        locks = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            // You can replace the arguments with the appropriate values for your LockStatus and workStation
            Lock lock = new Lock(i, LockStatus.ON, 1);
            locks.add(lock);
        }
        for (int i = 0; i < 60; i++) {
            // You can replace the arguments with the appropriate values for your LockStatus and workStation
            Lock lock = new Lock(i + 60, LockStatus.ON, 2);
            locks.add(lock);
        }
//        locks = new ArrayList<>(Collections.nCopies(120, -1));
        // Create and start thread for sending COM interface messages
        Thread sendCOMThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // 等待1000毫秒，即1秒
                } catch (InterruptedException e) {e.printStackTrace();}
//                System.out.println("1");
            }
        });
        sendCOMThread.start();

        // Create and start thread for receiving COM interface messages
        Thread receiveCOMThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // 等待1000毫秒，即1秒
                } catch (InterruptedException e) {e.printStackTrace();}
//                System.out.println("2");
            }
        });
        receiveCOMThread.start();

        // Create and start thread for receiving socket messages
        Thread receiveSocketThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // 等待1000毫秒，即1秒
                } catch (InterruptedException e) {e.printStackTrace();}
//                System.out.println("3");
            }
        });
        receiveSocketThread.start();
    }

    public void setJoinMatchButtonsVisible(int buttonNum, boolean visible) {
        if (buttonNum == 1)
            joinMatchButton1.setVisible(visible);
        else if (buttonNum == 2)
            joinMatchButton2.setVisible(visible);
    }

    public static void addController(Object controller) {
        boolean flag = true;
        Set<String> names = Main.controllers.keySet();
        for (int i = 0; i < names.size(); i++) {
            if (names.contains(controller.getClass().getSimpleName())) {
                Main.controllers.put(controller.getClass().getSimpleName() + "2", controller);
                flag = false;
            }
        }
        if (flag) {
            Main.controllers.put(controller.getClass().getSimpleName(), controller);
        }
    }

    public static void deleteController(Object controller){
        Main.controllers = Main.controllers.entrySet().stream()
                .filter(entry -> !entry.getValue().equals(controller))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void setOuterLoader(FXMLLoader outerLoader){
        this.outerLoader = outerLoader;
    }

    public void setMachine(Machine machine) throws JSONException, IOException {
        this.machine = machine;
        start();
    }

    private boolean start() throws JSONException {
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
            if (jsonObject.has("code") && jsonObject.getInt("code") == 200) {
                // 启动机器成功
                popup.hide();
                return true;
            } else {
                popup.hide();
                // 启动机器失败
                popUpAlter("ERROR", "", "启动机器失败");
                return false;
            }
        } catch (TimeoutException e) {
            popup.hide();
            // 超时处理
            popUpAlter("ERROR", "", "启动机器超时");
            return false;
        } catch (InterruptedException | ExecutionException e) {
            popup.hide();
            // 其他异常处理
            popUpAlter("ERROR", "", "启动机器失败");
            System.out.println("ERROR:" + e.getMessage());
            return false;
        }
    }

    public static void popUpAlter(String title, String headerText, String contentText) {
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

    //点击锁的函数
    public static Popup lockDetail(Lock lock, Worker worker) {
        // 创建 Popup
        Popup popup = new Popup();

        String lockName = lock.getLockName();
        int serialNumber = lock.getSerialNumber();
        int difficulty = lock.getDifficulty();

        TextField lockNameField = new TextField(lockName);
        TextField serialNumField = new TextField("" + serialNumber);
        TextField difficultyField = new TextField("" + difficulty);
        Button button = new Button("更新信息");

        EventHandler<ActionEvent> lockUpdate =
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {
                        String lockNameNew = lockNameField.getText();
                        int serialNumNew = Integer.parseInt(serialNumField.getText());
                        int difficultyNew = Integer.parseInt(difficultyField.getText());

                        if (lockNameNew.equals(lockName) && serialNumNew == serialNumber && difficultyNew == difficulty)
                            System.out.println("全都相等，不更新");
                        else {
                            try {
                                lock.setLockName(lockNameNew);
                                lock.setSerialNumber(serialNumNew);
                                lock.setDifficulty(difficultyNew);
                                lockUpdate(lock, worker);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                };
        button.setOnAction(lockUpdate);
        // 创建等待页面内容
        VBox popupContent = new VBox();
        popupContent.getChildren().addAll(
                lockNameField,
                serialNumField,
                difficultyField,
                button

        ); // 加载提示信息

        // 设置等待页面样式
        popupContent.setStyle("-fx-background-color: white; -fx-padding: 10px;");

        // 将内容放置在 Popup 中
        popup.getContent().add(popupContent);

        // 设置 Popup 相对于主舞台的位置
        popup.setAutoHide(false);
        popup.show(primaryStage);

        return popup;
    }

    private static void lockUpdate(Lock lock, Worker worker) throws IOException {
        System.out.println("更新!");
        Tools.socketConnect(lock.updateJSON(worker));
    }

    public void logout(){
        adminTab.getChildren().clear();
        try{
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/login_admin.fxml"));
//            Pane pane = loader.load();
//            adminTab.getChildren().add(pane);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_admin.fxml"));
            loader.setRoot(outerLoader.getNamespace().get("adminTab"));
            loader.load();

            LoginAdminController controller = loader.getController();
            controller.setMachine(machine);
            controller.setOuterFXMLLoader(outerLoader);
            controller.setPanePosition("adminTab");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}