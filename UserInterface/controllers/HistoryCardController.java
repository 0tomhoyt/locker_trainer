package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import models.Lock;
import models.TrainingHistory;
import models.Worker;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HistoryCardController implements Initializable, Controller {
    private TrainingHistory trainingHistory;
    private Worker worker;
    @FXML
    private Label time_label;

    @FXML
    private Label total_time_label;
    @FXML
    private Label unlocked_label;
    @FXML
    private Label difficulty_label;
    @FXML
    private VBox vBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        MainController.addController(this);
    }

    public void setWorker(Worker worker){
        this.worker = worker;
    }

    public void setTrainingHistory(TrainingHistory trainingHistory){
        this.trainingHistory = trainingHistory;

        time_label.setText("时间："+ trainingHistory.getDateTimeString());
        total_time_label.setText("总用时："+ trainingHistory.getTotalTime());
        unlocked_label.setText("未解锁数："+ trainingHistory.getUnlocked());
        difficulty_label.setText("难度："+ trainingHistory.getDifficulty());

        if(!(trainingHistory.getUnlocked() == 0)){
            List<Lock> locks = trainingHistory.getLocks();
            List<Lock> distinctLocks = new ArrayList<>(locks.stream()
                    .collect(Collectors.toMap(
                            Lock::getSerialNumber,
                            Function.identity(),
                            (existing, replacement) -> existing))
                    .values());
            for(int i=0;i<locks.size();i++){
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/lock_card.fxml"));
                    Pane pane = loader.load();
                    vBox.getChildren().add(pane);

                    LockCardController controller = loader.getController();
                    controller.setWorker(worker);
                    controller.setLock(distinctLocks.get(i));
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
