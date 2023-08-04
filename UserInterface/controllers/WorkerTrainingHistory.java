package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WorkerTrainingHistory implements Initializable {
    private FXMLLoader outerLoader;
    private FXMLLoader innerLoader;
    private String panePosition;
    @FXML
    private RadioButton btn_all;
    @FXML
    private RadioButton btn_train;
    @FXML
    private RadioButton btn_match;
    @FXML
    private VBox vBox;
    @FXML
    private AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.controllers.put(this.getClass().getSimpleName(),this);

        ToggleGroup toggleGroup = new ToggleGroup();
        btn_all.setToggleGroup(toggleGroup);
        btn_train.setToggleGroup(toggleGroup);
        btn_match.setToggleGroup(toggleGroup);
        btn_all.setSelected(true);

        try {
            vBox.getChildren().add(createPane());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AnchorPane createPane() throws IOException {
        return FXMLLoader.load(getClass().getResource("../fxml/history_card.fxml"));
    }

    public void setOuterLoader(FXMLLoader outerLoader) {
        this.outerLoader = outerLoader;
    }

    public void setInnerLoader(FXMLLoader innerLoader) {
        this.innerLoader = innerLoader;
    }

    public void setPanePosition(String panePosition) {
        this.panePosition = panePosition;
    }

    @FXML
    void back(ActionEvent event) throws IOException {
        anchorPane.getChildren().clear();
        FXMLLoader innerLoader = new FXMLLoader(getClass().getResource("../fxml/worker_UI.fxml"));
        innerLoader.setRoot(outerLoader.getNamespace().get(panePosition));
        innerLoader.load();

        WorkerUIController workerUIController = innerLoader.getController();
        workerUIController.setOuterLoader(outerLoader);
        workerUIController.setPanePosition(panePosition);
    }
}
