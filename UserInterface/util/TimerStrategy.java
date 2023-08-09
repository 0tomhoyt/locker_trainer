package util;

import javafx.scene.control.Label;

public interface TimerStrategy {
    void doWork();

    void reset();

    int getDifficulty();
}
