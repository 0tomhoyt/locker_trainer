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



        FXMLLoader outerLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = outerLoader.load();

        FXMLLoader innerLoader1 = new FXMLLoader(getClass().getResource("/fxml/login_worker.fxml"));
        innerLoader1.setRoot(outerLoader.getNamespace().get("insertionPoint1"));
        innerLoader1.load();

        FXMLLoader innerLoader2 = new FXMLLoader(getClass().getResource("/fxml/login_worker.fxml"));
        innerLoader2.setRoot(outerLoader.getNamespace().get("insertionPoint2"));
        innerLoader2.load();

        //加载管理员标签的登录页面
        FXMLLoader tabAdminLoader = new FXMLLoader(getClass().getResource("/fxml/login_admin.fxml"));
        tabAdminLoader.setRoot(outerLoader.getNamespace().get("adminTab"));
        tabAdminLoader.load();

        //加载管理员标签的登录页面
        FXMLLoader tabRegisterLoader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
        tabRegisterLoader.setRoot(outerLoader.getNamespace().get("registerTab"));
        tabRegisterLoader.load();

        //fxml加载完成

        MainController mainController = outerLoader.getController();
        MainController.primaryStage = primaryStage;
        mainController.setOuterLoader(outerLoader);
        mainController.setMachine(machine);

        LoginWorkerController loginWorkerController1 = innerLoader1.getController();
        loginWorkerController1.setOuterFXMLLoader(outerLoader);
        loginWorkerController1.setPanePosition("insertionPoint1");
        loginWorkerController1.setMachine(machine);
        loginWorkerController1.setMainController(mainController);

        LoginWorkerController loginWorkerController2 = innerLoader2.getController();
        loginWorkerController2.setOuterFXMLLoader(outerLoader);
        loginWorkerController2.setPanePosition("insertionPoint2");
        loginWorkerController2.setMachine(machine);
        loginWorkerController2.setMainController(mainController);

        LoginAdminController loginAdminController = tabAdminLoader.getController();
        loginAdminController.setOuterFXMLLoader(outerLoader);//这里为什么是outerloader
        loginAdminController.setPanePosition("adminTab");
        loginAdminController.setMachine(machine);
        loginAdminController.setMainController(mainController);

        // 页面出现
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("lockerTrainer");
        primaryStage.setFullScreen(true);
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