package util;

import models.TrainingHistory;

public class VariableTimeStrategy implements TimerStrategy{
    private TrainingHistory trainingHistory;
    @Override
    public void doWork() {
        trainingHistory.incSeconds();
    }

    @Override
    public void reset() {
        trainingHistory.setSeconds(0);
        trainingHistory.setMinutes(0);
    }

    @Override
    public int getDifficulty() {
        if(trainingHistory.getTime() < 60){
            return 5;
        }
        else if(trainingHistory.getTime() < 2 * 60){
            return 4;
        }
        else if(trainingHistory.getTime() < 3 * 60){
            return 3;
        }
        else if(trainingHistory.getTime() < 4 * 60){
            return 2;
        }
        else {
            return 1;
        }
    }

    public VariableTimeStrategy(TrainingHistory trainingHistory){
        this.trainingHistory = trainingHistory;
    }
}
