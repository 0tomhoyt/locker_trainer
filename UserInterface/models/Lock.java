package models;

public class Lock {
    private int id;
    private String status;
    private int workStation;

    public Lock(String status, int workStation) {
        this.status = status;
        this.workStation = workStation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setWorkStation(int workStation) {
        this.workStation = workStation;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public int getWorkStation() {
        return workStation;
    }
}
