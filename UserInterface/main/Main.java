package main;

import controllers.LoginAdminController;
import controllers.LoginWorkerController;
import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Machine;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {//继承抽象类，重写抽象函数
    public static Map<String, Object> controllers = new HashMap<String, Object>();
    public static ExecutorService executorService = Executors.newFixedThreadPool(5);
    private Machine machine;

    @Override
    public void start(Stage primaryStage) throws IOException, JSONException {
        machine = new Machine(true);
        machine.setId(1);

        FXMLLoader outerLoader = new FXMLLoader(getClass().getResource("../fxml/main.fxml"));
        Parent root = outerLoader.load();

        FXMLLoader innerLoader1 = new FXMLLoader(getClass().getResource("../fxml/login_worker.fxml"));
        innerLoader1.setRoot(outerLoader.getNamespace().get("insertionPoint1"));
        innerLoader1.load();

        FXMLLoader innerLoader2 = new FXMLLoader(getClass().getResource("../fxml/login_worker.fxml"));
        innerLoader2.setRoot(outerLoader.getNamespace().get("insertionPoint2"));
        innerLoader2.load();

        //加载管理员标签的登录页面
        FXMLLoader tab2Loader = new FXMLLoader(getClass().getResource("../fxml/login_admin.fxml"));
        tab2Loader.setRoot(outerLoader.getNamespace().get("adminTab"));
        tab2Loader.load();

        //fxml加载完成

        MainController mainController = outerLoader.getController();
        MainController.primaryStage = primaryStage;
        mainController.setMachine(machine);

        LoginWorkerController loginWorkerController1 = innerLoader1.getController();
        loginWorkerController1.setOuterFXMLLoader(outerLoader);
        loginWorkerController1.setPanePosition("insertionPoint1");
        loginWorkerController1.setMachine(machine);

        LoginWorkerController loginWorkerController2 = innerLoader2.getController();
        loginWorkerController2.setOuterFXMLLoader(outerLoader);
        loginWorkerController2.setPanePosition("insertionPoint2");
        loginWorkerController2.setMachine(machine);

        LoginAdminController loginAdminController = tab2Loader.getController();
        loginAdminController.setOuterFXMLLoader(outerLoader);//这里为什么是outerloader
        loginAdminController.setPanePosition("adminTab");
        loginAdminController.setMachine(machine);

        // 页面出现
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("lockerTrainer");
        primaryStage.show();
    }

    @Override
    public void stop(){
        executorService.shutdown();
    }

    public static void main(String[] args) {
        launch(args);//先调用 init 然后 start() 然后 stop 可以考虑在init中添加线程
    }
}