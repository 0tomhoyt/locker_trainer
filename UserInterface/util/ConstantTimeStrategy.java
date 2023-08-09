package util;

import controllers.Controller;
import controllers.WorkerUIController;
import javafx.scene.control.Label;
import main.Main;

public class ConstantTimeStrategy implements TimerStrategy{
    private Label label;
    private int seconds;
    private int max;
    private WorkerUIController workerUIController;
    @Override
    public void doWork() {
        if(seconds == 0){
            workerUIController.notifyEndingTrain();
        }
        else{
            seconds--;
            label.setText(seconds+"s");
        }
    }

    @Override
    public void reset() {
        label.setText("时间");
        seconds = max;
    }

    @Override
    public int getSeconds() {
        return seconds;
    }

    @Override
    public void setSeconds(int seconds) {
        this.seconds = seconds * 60;
        this.max = seconds * 60;
    }

    public ConstantTimeStrategy(Label label, WorkerUIController workerUIController) {
        this.label = label;
        this.workerUIController = workerUIController;
    }
}
