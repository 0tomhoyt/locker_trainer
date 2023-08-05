package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WorkerUIController implements Initializable {
    private FXMLLoader outerLoader;
    private FXMLLoader selfLoader;
    private String panePosition;
    private List<Label> labels = new ArrayList<>(50);
    private Timeline timer;
    private int seconds;
    @FXML
    private GridPane gridPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private RadioButton const_time_btn;
    @FXML
    private RadioButton var_time_btn;
    @FXML
    private Label time_label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.controllers.put(this.getClass().getSimpleName(),this);

        for(int i=0;i<5;i++){
            for(int j=0;j<10;j++){
                Label label = new Label("OFF");
                labels.add(label);
                gridPane.add(label,i,j);
            }
        }

        ToggleGroup toggleGroup = new ToggleGroup();
        const_time_btn.setToggleGroup(toggleGroup);
        var_time_btn.setToggleGroup(toggleGroup);
        const_time_btn.setSelected(true);

        seconds = 0;
        timer = new Timeline(new KeyFrame(Duration.seconds(1),actionEvent -> {
            seconds++;
            time_label.setText(seconds+"s");
        }));
        timer.setCycleCount(Animation.INDEFINITE);
    }

    public void setOuterLoader(FXMLLoader outerLoader) {
        this.outerLoader = outerLoader;
    }

    public void setSelfLoader(FXMLLoader selfLoader) {
        this.selfLoader = selfLoader;
    }

    public void setPanePosition(String panePosition) {
        this.panePosition = panePosition;
    }

    @FXML
    void train_history(ActionEvent event) {
        Platform.runLater(() -> {
            anchorPane.getChildren().clear();
            FXMLLoader innerLoader = new FXMLLoader(getClass().getResource("../fxml/worker_training_history.fxml"));
            innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
            try {
                innerLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            WorkerTrainingHistory workerTrainingHistory = innerLoader.getController();
            workerTrainingHistory.setOuterLoader(outerLoader);
//            workerTrainingHistory.setInnerLoader(innerLoader);
            workerTrainingHistory.setPanePosition(panePosition);
        });
    }

    @FXML
    void start(ActionEvent event) {
        timer.play();
    }
}