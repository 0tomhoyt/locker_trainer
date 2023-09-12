package models;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Lock {
    private int id;
    private LockStatus status;
    private int workStation;
    private String time = "";//0.1秒为单位
    private String lockName = "未知"; // Default value
    private int serialNumber = 0; // Default value
    private int difficulty = 0; // Default value[

    public Lock(int id, LockStatus status, int workStation) {
        this.id = id;
        this.status = status;
        this.workStation = workStation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LockStatus getStatus() {
        return status;
    }

    public void setStatus(LockStatus status) {
        this.status = status;
    }

    public int getWorkStation() {
        return workStation;
    }

    public void setWorkStation(int workStation) {
        this.workStation = workStation;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getTime() {
        if(time.equals("")){
            return "0";
        }
        else {
            return time;
        }
    }

    public void setTime(String Time) {
        this.time = Time;
    }

    public Color getColorByStatus(){
        if(status == LockStatus.ON){
            return Color.YELLOWGREEN;
        }
        else if(status == LockStatus.OFF){
            return Color.GRAY;
        }
        else if(status == LockStatus.UNCONNECTED){
            return Color.RED;
        }
        else if(status == LockStatus.FINISHED){
            return Color.GREEN;
        }
        else{
            return Color.RED;
        }
    }

    //"lockId": 5, "lockName": "2号锁", "lockSerialNumber": 8988,"difficulty":2}
    public String updateJSON(Worker worker) {
        return String.format("{ \"event\": \"updateLockInfo\", \"data\": { \"authToken\":\"%s\", \"lockId\" : \"%d\",\"lockName\":\"%s\",\"lockSerialNumber\":\"%d\",\"difficulty\":\"%d\"} }",
                worker.getAuthToken(),
                id,
                lockName,
                serialNumber,
                difficulty
        );
    }

    public String getHistoryJson(String authToken){
        return String.format("{ \"event\": \"getUnlockInfoBySerialNum\", \"data\": { \"authToken\": \"%s\", \"serialNum\": %d} }",
                authToken,
                serialNumber
        );
    }

    public static void main(String[] args) {
//        byte a = (byte)0b00000001;
//        System.out.println(a);

        List<String> lookStatusCommand = new ArrayList<>();
        String lookStatusCommandString = "AA 0X 00 01 00 02 55 00 00";
        String command;
        for(int i = 1;i<=9;i++){
            command = lookStatusCommandString.replace("X",String.valueOf(i));
            lookStatusCommand.add(command);
        }
        lookStatusCommand.add("AA 0A 00 01 00 02 55 00 00");
        lookStatusCommand.add("AA 0B 00 01 00 02 55 00 00");
        lookStatusCommand.add("AA 0C 00 01 00 02 55 00 00");
        for(int i = 0; i<12 ;i++){
//            this.serialPortConnection.sendByHexString(this.lookStatusCommand.get(i));
            System.out.println(lookStatusCommand.get(i));
        }
    }
}
