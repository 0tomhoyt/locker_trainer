package models;

import java.util.ArrayList;
import java.util.List;

public class Machine {
    private int id;
    private boolean isStart;
    private List<Worker> workers = new ArrayList<>(2);

    public Machine(boolean isStart) {
        this.isStart = isStart;
    }

    public Machine(int id){
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    public void addWorker(Worker worker){
        workers.add(worker);
    }

    public int getId() {
        return id;
    }

    public boolean isStart() {
        return isStart;
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public String getStartJson(){
        return String.format("{ \"event\": \"machineStart\", \"data\": { \"machineId\": %d } }",
                id
                );
    }

    public String getEndJson(){
        return String.format("{ \"event\": \"machineStop\", \"data\": { \"machineId\": %d } }",
                id
                );
    }
}
