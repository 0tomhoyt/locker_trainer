package util;

import controllers.WorkerUIController;
import models.TrainingHistory;
import org.json.JSONException;

import java.io.IOException;

public class ConstantTimeStrategy implements TimerStrategy{
    private TrainingHistory trainingHistory;
    private WorkerUIController workerUIController;
    @Override
    public void doWork() throws JSONException, IOException {
        if(trainingHistory.getTime() == 0)
            workerUIController.notifyEndingTrain();
        else {
            trainingHistory.decSeconds();
        }
    }

    @Override
    public void reset() {
        trainingHistory.setTime(trainingHistory.getTotalTime());
    }

    @Override
    public int getDifficulty() {
        if(trainingHistory.getTotalTime() < 60){
            return 5;
        }
        else if(trainingHistory.getTotalTime() < 2 * 60){
            return 4;
        }
        else if(trainingHistory.getTotalTime() < 3 * 60){
            return 3;
        }
        else if(trainingHistory.getTotalTime() < 4 * 60){
            return 2;
        }
        else {
            return 1;
        }
    }

    public ConstantTimeStrategy(TrainingHistory trainingHistory, WorkerUIController workerUIController) {
        this.trainingHistory = trainingHistory;
        this.workerUIController = workerUIController;
    }
}
