package models;

public class Machine {
    private int id;
    private boolean isStart;

    public Machine(boolean isStart) {
        this.isStart = isStart;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public int getId() {
        return id;
    }

    public boolean isStart() {
        return isStart;
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
