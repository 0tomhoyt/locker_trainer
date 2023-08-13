package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import models.Worker;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ConnectedMachineCardController implements Initializable, Controller {
    private List<Worker> workers = new ArrayList<>();
    @FXML
    private Label machine_label;
    @FXML
    private ChoiceBox<String> worker_choiceBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        MainController.addController(this);
    }

    public void setupChoiceBox(){
        ObservableList<String> options = FXCollections.observableArrayList();
        options.add("null");
        for(Worker worker : workers){
            options.add(worker.getId()+":"+worker.getUsername());
        }

        worker_choiceBox.setItems(options);
    }

    public void setWorkers(List<Worker> workers){

        this.workers.addAll(workers);


        setupChoiceBox();
    }

    public void setInfo(int machineID, Worker worker){
        machine_label.setText("机器"+machineID);
        worker_choiceBox.setValue(worker.getId()+":"+worker.getUsername());
    }

    public Worker getWorker(){
        Worker worker = new Worker(-1,-1);
        if(worker_choiceBox.getValue().equals("null")){
            System.out.println("no worker selected");
        }
        else {
            String user = worker_choiceBox.getValue();
            int colonIndex = user.indexOf(":");
            int userID = Integer.parseInt(user.substring(0, colonIndex));

            for(int i=0;i<workers.size();i++){
                if(workers.get(i).getId() == userID){
                    worker = workers.get(i);
                }
            }
        }

        return worker;
    }
}
