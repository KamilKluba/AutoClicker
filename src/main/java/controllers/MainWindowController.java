package controllers;

import data.ActionField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Auto clicker", "*.aclkr"));
    }

    public void start(){
        buttonAddAction.setDisable(true);
        buttonRemoveAction.setDisable(true);
        buttonSave.setDisable(true);
        buttonRead.setDisable(true);
        buttonStart.setDisable(true);
        buttonStop.setDisable(false);
        for(ActionField a : actionFields){
            a.recreateService();
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
        ActionField actionField = actionFields.remove(actionFields.size() - 1);
        actionField.kill();
    }

    public void actionSave(){
        try {
            fileChooser.setTitle("Wybierz miejsce do zapisu");
            fileChooser.setInitialFileName("Clicker schema");
            File file = fileChooser.showSaveDialog(stage);
            if(file != null) {
                if(!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeInt(actionFields.size());
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
        try{
            File file = fileChooser.showOpenDialog(stage);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            actionFields.clear();
            vBoxActions.getChildren().clear();
            int objectsToReadNumber = ois.readInt();
            while(objectsToReadNumber-- != 0){
                ActionField actionField = (ActionField)ois.readObject();
                actionField.prepareControl();
                vBoxActions.getChildren().add(actionField.getControl());
                actionFields.add(actionField);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }
}
