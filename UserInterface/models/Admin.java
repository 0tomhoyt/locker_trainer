package models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Admin extends Worker{
    /*继承，重写了
    * getLoginJson
    * isAdmin
    * */

    public Admin(String username, String password, int machineID) {
        super(username,password,0,machineID);

    }

    @Override
    public String getLoginJson() {//小小修改
        return String.format("{ \"event\": \"adminLogin\", \"data\": { \"username\": \"%s\", \"password\": \"%s\", \"machineId\": %d } }",
                username,
                password,
                machineID
        );
    }

    @Override
    public boolean isAdmin() {//增加了这个函数，判断是否是管理员
        return true;
    }

    public String getStartTrainingJson(TrainingHistory trainingHistory){
        return String.format("{ \"event\": \"startNewTraining\", \"data\": { \"authToken\": \"%s\", \"workstationID\": %d, \"difficulty\": %d, \"totalTime\": %d} }",
                authToken,
                1,
                trainingHistory.getDifficulty(),
                trainingHistory.getTotalTime()
        );
    }

    public String getWorkerList(){
        return String.format("{\"event\": \"getWorkerStatus\", \"data\": { \"authToken\": \"%s\"} }",
                authToken
        );
    }

    public String getStartGameJson(List<TrainingHistory> trainingHistoryList){
        JSONArray jsonArray = new JSONArray();
        for (TrainingHistory t : trainingHistoryList) {
            try {
                jsonArray.put(new JSONObject()
                        .put("userID", t.getWorker().getId())
                        .put("difficulty", t.getDifficulty())
                        .put("time", t.getTotalTime())
                        .put("workstationID", t.getWorker().getWorkStationID())
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String jsonString = String.format("{\"event\": \"startMatch\", \"data\": { \"authToken\": \"%s\", \"trainings\": %s } }",
                authToken,
                jsonArray
        );

        System.out.println(jsonString);

        return jsonString;
    }

    public String getConnectedMachinesJson(){
        return String.format("{\"event\": \"getConnectedMachine\", \"data\": { \"authToken\": \"%s\" } }",
                authToken
        );
    }

    public String addFingerLoginJson(){
        return String.format("{\"event\": \"addFingerprint\", \"data\": { \"authToken\": \"%s\" } }",
                authToken
        );
    }

    public String checkFingerPrintJson(String fingerprint_id){
        return String.format("{\"event\": \"checkFingerprint\", \"data\": { \"authToken\": \"%s\", \"fingerprint_id\": %s } }",
                authToken,fingerprint_id
        );
    }
}
