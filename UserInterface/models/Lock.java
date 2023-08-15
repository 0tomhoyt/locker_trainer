package models;

public class Lock {
    private int id;
    private LockStatus status;
    private int workStation;
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
}
