package UserInterface.main;

import UserInterface.controllers.LoginWorkerController;
import UserInterface.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    public static Map<String,Object> controllers = new HashMap<String, Object>();

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader outerLoader = new FXMLLoader(getClass().getResource("../fxml/main.fxml"));
        Parent root = outerLoader.load();

        FXMLLoader innerLoader1 = new FXMLLoader(getClass().getResource("../fxml/login_worker.fxml"));
        innerLoader1.setRoot(outerLoader.getNamespace().get("insertionPoint1"));
        innerLoader1.load();

        FXMLLoader innerLoader2 = new FXMLLoader(getClass().getResource("../fxml/login_worker.fxml"));
        innerLoader2.setRoot(outerLoader.getNamespace().get("insertionPoint2"));
        innerLoader2.load();

        MainController testController = outerLoader.getController();
        testController.setOuterLoader(outerLoader);

        LoginWorkerController loginWorkerController1 = innerLoader1.getController();
        loginWorkerController1.setOuterFXMLLoader(outerLoader);
        loginWorkerController1.setPanePosition("insertionPoint1");

        LoginWorkerController loginWorkerController2 = innerLoader2.getController();
        loginWorkerController2.setOuterFXMLLoader(outerLoader);
        loginWorkerController2.setPanePosition("insertionPoint2");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}