package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;

import models.Admin;
import models.Lock;
import models.LockStatus;
import models.TrainingHistory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminStartTrainingController {

    @FXML
    private GridPane gridPane;
    private Admin admin;
    private List<Label> labels = new ArrayList<>(120);

    public void setAdmin(Admin admin) throws JSONException, IOException {
        int lockNum = 120;
        this.admin = admin;
        //拿到锁的数据
        JSONArray jsonArray = getLocksStatus(admin);
//        System.out.println(jsonArray);
        ArrayList<Lock> locks = new ArrayList<>();
        //需要获得worker的workStationID之后才可以创建lock
        //模拟：通过URAT接收锁的状态
        List<Label> labels = new ArrayList<>(lockNum);
        for (int i = 0; i < lockNum; i++) {
            Lock lock = new Lock(i, LockStatus.ON, admin.getWorkStationID());
            locks.add(lock);


            labels.add(new Label(lock.getStatus().toString()));
            //添加点击事件，方便测试
            labels.get(i).setOnMouseClicked(e -> {
//                点击时弹出窗口
                Popup lockDetail = MainController.lockDetail(lock, admin);//有个问题就是，一直点的话内存占用会越来越高XD，因为一直在新建，哪怕是点同一个，但搞了半天暂时只能想到这个逻辑了
//                lockDetail.hide();//默认是弹出的
                lockDetail.setAutoHide(true);//点击其他地方会消失

//                if (lockDetail.isShowing())
//                    lockDetail.hide();
//                else
//                    lockDetail.show(primaryStage);

                System.out.println(lock.getLockName());
            });
        }
        //更新锁的状态到客户端
        updateLocks(jsonArray, locks);
        int column = 20;
        int row = 6;
        //添加Label到gridPane
        for (int i = 0; i < row; i++) {//
            for (int j = 0; j < column; j++) {//
                gridPane.add(labels.get(i * column + j), j, i);//j是列 i是行
            }
        }


    }

    private String getLocksJSON1(Admin admin) {
        return String.format("{ \"event\": \"getLocks\", \"data\": { \"authToken\":\"%s\", \"workstationId\": \"%d\"} }",
                admin.getAuthToken(),
                1
        );
    }

    private String getLocksJSON2(Admin admin) {
        return String.format("{ \"event\": \"getLocks\", \"data\": { \"authToken\":\"%s\", \"workstationId\": \"%d\"} }",
                admin.getAuthToken(),
                1
        );
    }

    //得到锁的数组
    private JSONArray getLocksStatus(Admin admin) throws IOException, JSONException {
//        JSONObject jsonObject1 = Tools.transferToJSONObject(Tools.socketConnect(getLocksJSON1(admin)));
//        JSONObject jsonObject2 = Tools.transferToJSONObject(Tools.socketConnect(getLocksJSON2(admin)));//先保留，还不知道这个workstationId怎么搞
        JSONObject jsonObject = Tools.transferToJSONObject(Tools.socketConnect(getLocksJSON1(admin)));
        JSONArray jsonArray = jsonObject.getJSONArray("Locks");
        System.out.println(jsonObject);
        System.out.println(jsonArray + "锁的数据！！！！");
        return jsonArray;
    }

    private void updateLocks(JSONArray jsonArray, List<Lock> locks) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int l = jsonObject.getInt("lockId");
            int serialNum = jsonObject.getInt("lockSerialNumber");
            String lockName = jsonObject.getString("lockName");
            int difficulty = jsonObject.getInt("difficulty");
//            System.out.println(jsonArray.getJSONObject(i) +" "+ i);
//            labels.get(l).setOnMouseClicked(e ->{
//                System.out.println("special one");//可以拿到，但是是竖着来的，怪
//            });
            Lock lock = locks.get(l);
            lock.setSerialNumber(serialNum);
            lock.setLockName(lockName);
            lock.setDifficulty(difficulty);
        }
    }
}
