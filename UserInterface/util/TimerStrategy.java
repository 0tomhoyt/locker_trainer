package util;

import javafx.scene.control.Label;
import org.json.JSONException;

import java.io.IOException;

public interface TimerStrategy {
    void doWork() throws JSONException, IOException;

    void reset();

    int getDifficulty();
}
