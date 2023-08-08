package util;

import javafx.scene.control.Label;

public class VariableTimeStrategy implements TimerStrategy{
    private Label label;
    private int seconds;
    @Override
    public void doWork() {
        seconds++;
        label.setText(seconds+"s");
    }

    @Override
    public void reset() {
        label.setText("时间");
        seconds = 0;
    }

    @Override
    public int getSeconds() {
        return seconds;
    }

    @Override
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public VariableTimeStrategy(Label label) {
        this.label = label;
    }
}
