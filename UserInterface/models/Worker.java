package models;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class Worker {
    private int id;
    protected String username;
    protected String password;
    private int workStationID;
    protected int machineID;
    protected String authToken;
    private String enrollDate;
    private String headerURL;

    public Worker(String username, String password, int workStation, int machineID) {
        this.id = -1;
        this.username = username;
        this.password = password;
        this.workStationID = workStation;
        this.machineID = machineID;
    }

    public Worker(int id, int workStationID){
        this.id = id;
        this.workStationID = workStationID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setWorkStationID(int workStationID) {
        this.workStationID = workStationID;
    }

    public void setMachineID(int machineID) {
        this.machineID = machineID;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public void setHeaderURL(String headerURL) {
        this.headerURL = headerURL;
    }

    public void setWorkLength(int workLength){
        LocalDate today = LocalDate.now();

        // 计算报名日期
        LocalDate enrollDate = today.minus(workLength, ChronoUnit.DAYS);

        // 将报名日期对象格式化为字符串并返回
        this.enrollDate = enrollDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getWorkStationID() {
        return workStationID;
    }

    public int getMachineID() {
        return machineID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public String getHeaderURL() {
        return headerURL;
    }

    public int getWorkLength() throws Exception {
        // 获取今天的日期
        LocalDate today = LocalDate.now();

        // 将报名日期字符串解析为LocalDate对象
        LocalDate enrollDateObj = LocalDate.parse(enrollDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 计算工作天数
        long daysBetween = ChronoUnit.DAYS.between(enrollDateObj, today);

        return (int)daysBetween;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", workStationID=" + workStationID +
                ", machineID=" + machineID +
                ", authToken='" + authToken + '\'' +
                ", enrollDate='" + enrollDate + '\'' +
                ", headerURL='" + headerURL + '\'' +
                '}';
    }

    public String getLoginJson(){
        return String.format("{ \"event\": \"workerLogin\", \"data\": { \"username\": \"%s\", \"password\": \"%s\", \"machineId\": %d, \"workstationId\": %d } }",
                username,
                password,
                machineID,
                workStationID
        );
    }

    public String getLogoutJson(){
        return String.format("{ \"event\": \"workerLogout\", \"data\": { \"machineId\": %d, \"workstationId\": %d } }",
                machineID,
                workStationID
        );
    }

    public String getStartTrainingJson(TrainingHistory trainingHistory){
        return String.format("{ \"event\": \"startNewTraining\", \"data\": { \"authToken\": \"%s\", \"workstationID\": %d, \"difficulty\": %d, \"totalTime\": %d} }",
                authToken,
                workStationID,
                trainingHistory.getDifficulty(),
                trainingHistory.getTotalTime()
        );
    }

    public boolean isAdmin(){
        return false;
    }

    public boolean isAdmifuckn(){
        return false;
    }

    public String getUpdateTrainingJson(TrainingHistory trainingHistory){
        return String.format("{ \"event\": \"updateTraining\", \"data\": { \"authToken\": \"%s\", \"trainingID\": %d, \"score\": %d, \"unlockedNum\": %d, \"isOn\": %d} }",
                authToken,
                trainingHistory.getId(),
                trainingHistory.getScore(),
                trainingHistory.getUnlocked(),
                trainingHistory.isOn() ? 1 : 0
        );
    }

    public String getEndTrainingJson(TrainingHistory trainingHistory){
        return String.format("{ \"event\": \"stopTraining\", \"data\": { \"authToken\": \"%s\", \"trainingID\": %d} }",
                authToken,
                trainingHistory.getId()
        );
    }

    public String getTrainingHistoriesJson(){
        return String.format("{ \"event\": \"workerGetSelfTrainingHistory\", \"data\": { \"authToken\":\"%s\"} }",
                authToken
        );
    }
}
