package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import main.Main;
import models.Admin;
import models.Worker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AdminWorkerInfoController implements Initializable, Controller {
    private Admin admin;
    private List<Worker> workers = new ArrayList<>();
    @FXML
    private VBox vBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        MainController.addController(this);
    }

    public void setAdmin(Admin admin){
        this.admin = admin;
        getWorkerList();
        setupPage();
    }

    private boolean getWorkerList() {
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(admin.getWorkerList()));

        try {
            String data = future.get(5, TimeUnit.SECONDS);
            System.out.println(data);
            JSONObject jsonObject = Tools.transferToJSONObject(data);
            if(jsonObject.has("code") && jsonObject.getInt("code") == 200){
                JSONArray jsonArray = jsonObject.getJSONArray("workers");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    Worker worker = new Worker(object.getInt("UserID"),1);
                    worker.setUsername(object.getString("UserName"));
                    worker.setPassword(object.getString("Password"));
                    worker.setAuthToken(object.getString("AuthToken"));
                    worker.setHeaderURL(object.getString("HeadUrl"));
                    worker.setEnrollDate(object.getString("EnrolledDate"));
                    workers.add(worker);
                }
                System.out.println(workers);
                return true;
            }
            else {
                MainController.popUpAlter("ERROR","",Tools.unicodeToChinese(jsonObject.getString("message")));
                return false;
            }
        } catch (TimeoutException e) {
            // 超时处理
            System.out.println("Socket receive timeout: " + e.getMessage());
            MainController.popUpAlter("ERROR","",e.getMessage());
            return false;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            // 其他异常处理
            System.out.println("ERROR:"+e.getMessage());
            MainController.popUpAlter("ERROR","",e.getMessage());
            return false;
        }
    }

    private void setupPage(){
        for (Worker worker : workers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/work_card.fxml"));
                Pane pane = loader.load();
                vBox.getChildren().add(pane);

                WorkerCardController controller = loader.getController();
                controller.setWorker(worker);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
