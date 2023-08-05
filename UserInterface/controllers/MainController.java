package controllers;

import main.Main;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class MainController implements Initializable {
    private FXMLLoader outerLoader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.controllers.put(this.getClass().getSimpleName(),this);
    }

    public static void addController(Object controller){
        boolean flag = true;
        Set<String> names = Main.controllers.keySet();
        for(int i=0;i< names.size();i++){
            if(names.contains(controller.getClass().getSimpleName())){
                Main.controllers.put(controller.getClass().getSimpleName()+"2",controller);
                flag = false;
            }
        }
        if(flag){
            Main.controllers.put(controller.getClass().getSimpleName(),controller);
        }
    }

    public void setOuterLoader(FXMLLoader outerLoader){
        this.outerLoader = outerLoader;
    }
}