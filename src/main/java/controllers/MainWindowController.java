package controllers;

import data.ActionField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainWindowController {
    @FXML private VBox vBoxActions;
    @FXML private Button buttonAddAction;
    @FXML private Button buttonRemoveAction;
    @FXML private Button buttonSave;
    @FXML private Button buttonRead;
    @FXML private Button buttonStart;
    @FXML private Button buttonStop;

    private final FileChooser fileChooser = new FileChooser();
    private Stage stage;
    private final ArrayList<ActionField> actionFields = new ArrayList<>();

    private boolean alive = true;

    public void initialize(){
        ActionField actionField = new ActionField();
        actionFields.add(actionField);
        vBoxActions.getChildren().add(actionField.getControl());
    }

    public void start(){
        buttonAddAction.setDisable(true);
        buttonRemoveAction.setDisable(true);
        buttonSave.setDisable(true);
        buttonRead.setDisable(true);
        buttonStart.setDisable(true);
        buttonStop.setDisable(false);
        for(ActionField a : actionFields){
            a.getCheckboxActive().setDisable(true);
            a.getComboBoxAction().setDisable(true);
            a.getComboBoxTrigger().setDisable(true);
            a.getButtonConfigure().setDisable(true);
            a.start();
        }
    }

    public void stop(){
        buttonAddAction.setDisable(false);
        buttonRemoveAction.setDisable(false);
        buttonSave.setDisable(false);
        buttonRead.setDisable(false);
        buttonStart.setDisable(false);
        buttonStop.setDisable(true);
        for(ActionField a : actionFields){
            a.getCheckboxActive().setDisable(false);
            a.getComboBoxAction().setDisable(false);
            a.getComboBoxTrigger().setDisable(false);
            a.getButtonConfigure().setDisable(false);
            a.kill();
        }
    }

    public void addAction(){
        ActionField actionField = new ActionField();
        actionFields.add(actionField);
        vBoxActions.getChildren().add(actionField.getControl());
    }

    public void removeAction(){
        vBoxActions.getChildren().remove(vBoxActions.getChildren().size() - 1);
    }

    public void actionSave(){
        try {
            fileChooser.setTitle("Wybierz miejsce do zapisu");
            fileChooser.setInitialFileName("Clicker 1");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Auto clicker", "*.aclkr"));
            File file = fileChooser.showSaveDialog(stage);
            if(file != null) {
                if(!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                for(ActionField a : actionFields){
                    oos.writeObject(a);
                }
                oos.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionRead(){

    }

    public void setStage(Stage stage){
        this.stage = stage;
    }
}