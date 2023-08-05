package models;

public class TrainingHistory {
    private int id;
    private int score;
    private int totalTime;
    private int unlocked;
    private int difficulty;
    private int trainingType;
    private boolean isMatch;
    private int MatchID;
    private Worker worker;
    private boolean isOn;

    public TrainingHistory(int score, int totalTime, int unlocked, int difficulty, int trainingType, boolean isMatch, Worker worker) {
        this.score = score;
        this.totalTime = totalTime;
        this.unlocked = unlocked;
        this.difficulty = difficulty;
        this.trainingType = trainingType;
        this.isMatch = isMatch;
        this.worker = worker;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTotalTime(int totalTime) {
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
        MatchID = matchID;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public int getId() {
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
        return MatchID;
    }

    public Worker getWorker() {
        return worker;
    }

    public boolean isOn() {
        return isOn;
    }
}
