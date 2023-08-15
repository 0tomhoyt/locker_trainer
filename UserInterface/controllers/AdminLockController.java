package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import main.Main;
import models.*;
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

public class AdminLockController implements Initializable, Controller {
    private List<Lock> locks = new ArrayList<>();
    private Admin admin;
    private Worker worker;
    private Lock lock;
    private int returnWay; //1是从AdminWorkerTrainingHistoryController, 2是从AdminCheckLockHistoryController
    @FXML
    private Button return_btn;
    @FXML
    private VBox vBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        MainController.addController(this);
    }

    public void setReturnWay(int returnWay){
        this.returnWay = returnWay;
    }

    public void setAdmin(Admin admin){
        this.admin= admin;
    }

    public void setWorker(Worker worker){
        this.worker = worker;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
        getLockHistory();
    }

    @FXML
    void return_btn_click(ActionEvent event) {
        if(returnWay == 1){
            ((AdminUIController) Main.controllers.get("AdminUIController")).setupWorkerTrainingHistory(worker);
        }
        else if(returnWay == 2) {
            ((AdminUIController) Main.controllers.get("AdminUIController")).setupCheckLockHistory();
        }
    }

    private boolean getLockHistory(){
        Future<String> future = Main.executorService.submit(() -> Tools.socketConnect(lock.getHistoryJson(admin.getAuthToken())));

        Popup popup = MainController.showLoadingPopup("获得锁的记录中");

        try {
            String data = future.get(5, TimeUnit.SECONDS);
            System.out.println(data);
            JSONObject jsonObject = Tools.transferToJSONObject(data);
            popup.hide();
            if(jsonObject.has("code") && jsonObject.getInt("code") == 200){
                JSONArray array = jsonObject.getJSONArray("unlocks");
                if(array.length() == 0){
                    MainController.popUpAlter("ERROR","","没有找到记录");
                }
                else {
                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        int unlockId = object.getInt("unlockId");
                        String unlockTime = object.getString("unlockTime");
                        int unlockDuration = object.getInt("unlockDuration");
                        int trainingId = object.getInt("trainingId");
                        int lockId = object.getInt("lockId");
                        int lockSerialNum = object.getInt("lockSerialNum");
                        String lockName = Tools.unicodeToChinese(object.getString("lockName"));
                        int difficulty = object.getInt("difficulty");
                        Lock lock = new Lock(lockId, LockStatus.OFF, -1);
                        lock.setSerialNumber(lockSerialNum);
                        lock.setLockName(lockName);
                        lock.setDifficulty(difficulty);
                        locks.add(lock);
                        setupPage(unlockTime, unlockDuration, lock);
                    }
                }
                return true;
            }
            else {
                popup.hide();
                MainController.popUpAlter("ERROR","",Tools.unicodeToChinese(jsonObject.getString("message")));
                return false;
            }
        } catch (TimeoutException e) {
            // 超时处理
            popup.hide();
            MainController.popUpAlter("ERROR","","获取锁的记录超时");
            return false;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            // 其他异常处理
            popup.hide();
            MainController.popUpAlter("ERROR","","获取锁的记录失败");
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void setupPage(String unlockTime, int unlockDuration, Lock lock){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/lock_card_serialNum.fxml"));
            Pane pane = loader.load();
            vBox.getChildren().add(pane);

            LockCardSerialNumController controller = loader.getController();
            controller.setInfo(unlockTime, unlockDuration, lock);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
