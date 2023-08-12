package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import models.Admin;
import models.Worker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminUIController extends WorkerUIController implements Initializable, Controller {
    @FXML
    private AnchorPane game_history_page;

    @FXML
    private AnchorPane machine_info_page;

    @FXML
    private AnchorPane machines_worker_page;

    @FXML
    private AnchorPane start_game_page;

    @FXML
    private AnchorPane worker_info_page;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController.addController(this);
    }

    @Override
    public void setWorker(Worker worker) {
        this.worker = worker;

        setupStartGamePage();
        setupWorkerInfoPage();
    }

    private void setupStartGamePage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/admin_start_game.fxml"));
            start_game_page.getChildren().add(loader.load());

            AdminStartGameController adminStartGameController = loader.getController();
            adminStartGameController.setAdmin((Admin) worker);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupWorkerInfoPage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/admin_worker_info.fxml"));
            worker_info_page.getChildren().add(loader.load());


        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private boolean getWorkerList() {
//        String s = ((Admin)worker).getWorkerList();
//        String data = Tools.socketConnect(s);
//        JSONObject jsonObject2 = Tools.transferToJSONObject(data);
//        System.out.println(jsonObject2);

        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(((Admin)worker).getWorkerList()));

        try {
            JSONObject jsonObject = Tools.transferToJSONObject(future.get(5, TimeUnit.SECONDS));
            if(jsonObject.has("code") && jsonObject.getInt("code") == 200){
                JSONArray jsonArray = jsonObject.getJSONArray("workers");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    Worker worker = new Worker(object.getInt("UserID"),1);
                    worker.setUsername(object.getString("Username"));
                    worker.setPassword(object.getString("password"));
                    worker.setAuthToken(object.getString("AuthToken"));
                    worker.setHeaderURL(object.getString("HeadUrl"));
                    worker.setEnrollDate(object.getString("EnrolledDate"));
                }
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

    private void setupWorkerInfo(){
        for(int i=0;i<workers.size();i++){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/worker_card.fxml"));
                Pane pane = loader.load();
                vBox_worker_info.getChildren().add(pane);
                WorkerCardController controller = loader.getController();
                Worker worker = workers.get(i);
                controller.setInfo(worker.getUsername(),worker.getId(),worker.getWorkStationID());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
