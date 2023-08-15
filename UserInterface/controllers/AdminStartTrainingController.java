package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;

import models.Admin;
import models.Lock;
import models.LockStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminStartTrainingController {
    @FXML
    private Label lockStatus;
    @FXML
    private Button pressBtn;
    @FXML
    private GridPane gridPane;
    //    private Admin admin;
    private List<Label> labels = new ArrayList<>(120);

    //    这个页面的入口函数↓
    public void setAdmin(Admin admin) throws JSONException, IOException {
        int lockNum = 120;
//        this.admin = admin;//好像没什么用

        JSONArray jsonArray = getLocksStatus(admin);//拿到锁的数据
//        System.out.println(jsonArray);
        ArrayList<Lock> locks = new ArrayList<>(lockNum);//不知道怎么和mainController联系起来
        //需要获得worker的workStationID之后才可以创建lock，为什么呢？
        //模拟：通过URAT接收锁的状态
        ArrayList<Label> labels = new ArrayList<>(lockNum);
        //定义锁的locks 和labels
        for (int i = 0; i < lockNum; i++) {
            Lock lock = new Lock(i, LockStatus.ON, admin.getWorkStationID());
            locks.add(lock);

            labels.add(new Label(lock.getStatus().toString()));//显示的label
            //添加点击事件，方便测试
            labels.get(i).setOnMouseClicked(e -> {
//                点击时弹出窗口
                Popup lockDetail = MainController.lockDetail(lock, admin);//有个问题就是，一直点的话内存占用会越来越高XD，因为一直在新建，哪怕是点同一个，但搞了半天暂时只能想到这个逻辑了

                lockDetail.setAutoHide(true);//点击其他地方会消失

//                System.out.println(lock.getLockName());
            });
        }
        //更新锁的状态到客户端
        updateLocks(jsonArray, locks);

        //添加Label到gridPane
        int column = 20;
        int row = 6;

        for (int i = 0; i < row; i++) {//
            for (int j = 0; j < column; j++) {//
                gridPane.add(labels.get(i * column + j), j, i);//j是列 i是行, 这里改好之后就是横着来的了
            }
        }
//定义好线程
//        由于硬件返回的是一个整数，单位为0.1s 这里只是做个模拟

        Thread timeCounter = new Thread(() -> {
            AtomicInteger receiveInt = new AtomicInteger();//在thread里好像就得这么用
            while (true) {

                receiveInt.getAndIncrement();
                System.out.println(receiveInt);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                showtime((receiveInt.intValue() / 10));
            }
        });
//        按下按钮
        pressbutton(timeCounter);

    }

    private void showtime(int i) {
        Platform.runLater(() -> {
            // This code will be executed on the JavaFX application thread
            lockStatus.setText("所用时间" + i + "s" + "锁的名称 ： " + "智能锁1" + " 难度 : " + "1");
        });

    }

    private void pressbutton(Thread timeCounter) {
        EventHandler<ActionEvent> count =
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {
                        if (timeCounter.isAlive()) {
                            timeCounter.interrupt();
                        } else
                            timeCounter.start();
                    }
                };

        pressBtn.setOnAction(count);
    }


    //获取锁的socket的格式
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
//        System.out.println(jsonObject);
//        System.out.println(jsonArray + "锁的数据！！！！");
        return jsonArray;
    }

    private void updateLocks(JSONArray jsonArray, List<Lock> locks) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int l = jsonObject.getInt("lockId");
            int serialNum = jsonObject.getInt("lockSerialNumber");
            String lockName = jsonObject.getString("lockName");
            int difficulty = jsonObject.getInt("difficulty");
//            更新锁的数据 因为是地址类型的，可以影响外面
            Lock lock = locks.get(l);
            lock.setSerialNumber(serialNum);
            lock.setLockName(lockName);
            lock.setDifficulty(difficulty);
        }
    }

//    public class ThreadCounterExample {
//        private static int counter = 0; // Shared counter
//
//        public static void main(String[] args) {
//            final int numThreads = 5;
//            Thread[] threads = new Thread[numThreads];
//
//            // Create and start multiple threads using lambda expressions
//            for (int i = 0; i < numThreads; i++) {
//                threads[i] = new Thread(() -> {
//                    for (int j = 0; j < 10000; j++) {
//                        synchronized (ThreadCounterExample.class) {
//                            counter++;
//                        }
//                    }
//                });
//                threads[i].start();
//            }
//
//            // Wait for all threads to complete
//            for (Thread thread : threads) {
//                try {
//                    thread.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            System.out.println("Final counter value: " + counter);
//        }
//    }

}
