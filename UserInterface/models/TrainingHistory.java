package models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TrainingHistory {
    private long id;
    private int score;
    private int totalTime;
    private int unlocked;
    private int difficulty;
    private int trainingType;
    private boolean isMatch;
    private int matchID;
    private Worker worker;
    private boolean isOn;
    private int seconds;
    private int minutes;
    private List<Lock> locks = new ArrayList<>();
    private LocalDateTime dateTime;

    public TrainingHistory(int score, int totalTime, int unlocked, int difficulty, int trainingType, boolean isMatch, Worker worker) {
        this.score = score;
        this.totalTime = totalTime;
        this.unlocked = unlocked;
        this.difficulty = difficulty;
        this.trainingType = trainingType;
        this.isMatch = isMatch;
        this.worker = worker;
    }

    public TrainingHistory(Worker worker) {
        this.worker = worker;
    }

    public TrainingHistory(JSONObject object) throws JSONException {
        score = object.getInt("Score");
        isMatch = object.getInt("IsMatch") == 1;
        id = object.getLong("TrainingID");
        worker = new Worker(object.getInt("UserID"), object.getInt("WorkstationID"));
        difficulty = object.getInt("Difficulty");
        matchID = object.getInt("MatchID");
        totalTime = object.getInt("TotalTime");
        trainingType = object.getInt("TrainingType");
        isOn = object.getInt("IsOn") == 1;
        unlocked = object.getInt("UnlockedNum");

        long givenTimestampSeconds = id;
        long givenTimestampMillis = givenTimestampSeconds * 10L;

        // 使用Calendar来获取日期和时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(givenTimestampMillis);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，所以我们需要+1
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24小时制
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String dateTimeString = String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dateTime = LocalDateTime.parse(dateTimeString, formatter);

        if(object.has("unlockList")){
            JSONArray array = object.getJSONArray("unlockList");
            for(int i=0;i<array.length();i++){
                JSONObject object_lock = array.getJSONObject(i);
                int lockId = object_lock.getInt("lockId");
                int lockSerialNumber = object_lock.getInt("lockSerialNumber");
                String lockName = object_lock.getString("lockName");
                int difficulty = object_lock.getInt("difficulty");
                Lock lock = new Lock(lockId,LockStatus.OFF,object.getInt("WorkstationID"));
                lock.setSerialNumber(lockSerialNumber);
                lock.setLockName(lockName);
                lock.setDifficulty(difficulty);
                locks.add(lock);
            }
        }
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTotalTime(int minutes, int seconds) {
        this.totalTime = minutes * 60 + seconds;
    }
    public void setTotalTime(int totalTime){
        this.totalTime = totalTime;
    }

    public void setUnlocked(int unlocked) {
        this.unlocked = unlocked;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setTrainingType(int trainingType) {
        this.trainingType = trainingType;
    }

    public void setMatch(boolean match) {
        isMatch = match;
    }

    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setLocks(List<Lock> locks) {
        this.locks = locks;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public long getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getUnlocked() {
        return unlocked;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getTrainingType() {
        return trainingType;
    }

    public boolean isMatch() {
        return isMatch;
    }

    public int getMatchID() {
        return matchID;
    }

    public Worker getWorker() {
        return worker;
    }

    public boolean isOn() {
        return isOn;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public List<Lock> getLocks() {
        return locks;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDateTimeString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public void setTime(int time){
        seconds = time % 60;
        minutes = time / 60;
    }

    public int getTime() {
        return minutes * 60 + seconds;
    }

    public void incSeconds(){
        seconds++;
    }

    public void decSeconds(){
        seconds--;
    }

    @Override
    public String toString() {
        return "TrainingHistory{" +
                "id=" + id +
                ", score=" + score +
                ", totalTime=" + totalTime +
                ", unlocked=" + unlocked +
                ", difficulty=" + difficulty +
                ", trainingType=" + trainingType +
                ", isMatch=" + isMatch +
                ", matchID=" + matchID +
                ", worker=" + worker +
                ", isOn=" + isOn +
                ", seconds=" + seconds +
                ", minutes=" + minutes +
                ", locks=" + locks +
                ", dateTime=" + dateTime +
                '}';
    }
}

